package in.gov.abdm.abha.enrollment.services.database.account.impl;

import in.gov.abdm.abha.enrollment.client.AbhaDBAccountFClient;
import in.gov.abdm.abha.enrollment.client.HidBenefitDBFClient;
import in.gov.abdm.abha.enrollment.client.IntegratedProgramDBFClient;
import in.gov.abdm.abha.enrollment.configuration.ContextHolder;
import in.gov.abdm.abha.enrollment.configuration.FacilityContextHolder;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.enums.AccountAuthMethods;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.enums.hidbenefit.HidBenefitStatus;
import in.gov.abdm.abha.enrollment.exception.abha_db.AbhaDBException;
import in.gov.abdm.abha.enrollment.exception.abha_db.AbhaDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.hidbenefit.BenefitNotFoundException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidBenefitDto;
import in.gov.abdm.abha.enrollment.model.entities.IntegratedProgramDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import in.gov.abdm.abha.enrollment.model.procedure.SaveAllDataRequest;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.redis.RedisService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import in.gov.abdm.error.ABDMError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.*;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Autowired
    AbhaDBAccountFClient abhaDBAccountFClient;

    @Autowired
    HidBenefitDBFClient hidBenefitDBFClient;

    @Autowired
    IntegratedProgramDBFClient integratedProgramDBFClient;

    @Autowired
    RedisService redisService;

    public static final String PARSER_EXCEPTION_OCCURRED_DURING_PARSING = "Parser Exception occurred during parsing :";
    public static final String EXCEPTION_IN_PARSING_INVALID_VALUE_OF_DOB = "Exception in parsing Invalid value of DOB: {}";
    private DateFormat kycDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    public Mono<AccountDto> findByXmlUid(String xmlUid) {
        return abhaDBAccountFClient.getAccountByXmlUid(Common.base64Encode(xmlUid))
                .onErrorResume((throwable -> Mono.error(new AbhaDBGatewayUnavailableException())));
    }

    @Override
    public Mono<AccountDto> prepareNewAccount(TransactionDto transactionDto, EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, List<LgdDistrictResponse> lgdDistrictResponses) {
        AccountDto newUser = new AccountDto();

        AccountDto accountDto = findUserAlreadyExist();
        if (isItNewUser(accountDto)) {
            newUser.setAddress(transactionDto.getAddress());
            newUser.setName(transactionDto.getName());
            newUser.setGender(transactionDto.getGender());

            validateKycAndProfilePhoto(transactionDto, newUser, accountDto);
            newUser.setKycdob(transactionDto.getKycdob());
            setDateOfBrith(transactionDto.getKycdob(), newUser);
            newUser.setDistrictName(transactionDto.getDistrictName());
            newUser.setStateName(transactionDto.getStateName());
            newUser.setVerificationType(AbhaConstants.AADHAAR);
            newUser.setVerificationStatus(AbhaConstants.VERIFIED);
            if (!lgdDistrictResponses.isEmpty()) {
                LgdDistrictResponse lgdDistrictResponse = Common.getLGDDetails(lgdDistrictResponses);
                newUser.setDistrictCode(lgdDistrictResponse.getDistrictCode());
                newUser.setDistrictName(lgdDistrictResponse.getDistrictName().equalsIgnoreCase("Unknown") ? transactionDto.getDistrictName() : lgdDistrictResponse.getDistrictName());
                newUser.setStateCode(lgdDistrictResponse.getStateCode());
                newUser.setStateName(lgdDistrictResponse.getStateName());
            } else {
                newUser.setDistrictName(transactionDto.getDistrictName());
                newUser.setStateName(transactionDto.getStateName());
            }

            newUser.setSubDistrictName(transactionDto.getSubDistrictName());
            newUser.setTownName(transactionDto.getTownName());
            newUser.setXmluid(transactionDto.getXmluid());
            if (!StringUtils.isBlank(transactionDto.getEmail())) {
                newUser.setEmail(transactionDto.getEmail());
            }

            newUser.setConsentVersion(enrolByAadhaarRequestDto.getConsent().getVersion());
            newUser.setConsentDate(LocalDateTime.now());

            Set<AccountAuthMethods> accountAuthMethods = new HashSet<>();
            accountAuthMethods.add(AccountAuthMethods.AADHAAR_OTP);
            accountAuthMethods.add(AccountAuthMethods.AADHAAR_BIO);
            accountAuthMethods.add(AccountAuthMethods.DEMOGRAPHICS);
            if (transactionDto.isMobileVerified() && !StringUtils.isBlank(transactionDto.getMobile())) {
                newUser.setMobile(transactionDto.getMobile());
                accountAuthMethods.add(AccountAuthMethods.MOBILE_OTP);
            }
            newUser.setKycVerified(true);
            newUser.setStatus(AccountStatus.ACTIVE.toString());
            breakName(newUser);
        }

        newUser.setCreatedDate(LocalDateTime.now());
        return Mono.just(newUser);
    }

    private static void validateKycAndProfilePhoto(TransactionDto transactionDto, AccountDto newUser, AccountDto accountDto) {
        if (accountDto.getKycPhoto() == null) {
            newUser.setKycPhoto(transactionDto.getKycPhoto());
        }
        newUser.setProfilePhoto(transactionDto.getKycPhoto());
        if (!StringUtils.isBlank(transactionDto.getPincode())) {
            newUser.setPincode(transactionDto.getPincode());
        }
    }

    @Override
    public boolean isItNewUser(AccountDto accountDto) {
        return accountDto == null || accountDto.getName() == null;
    }

    @Override
    public Mono<AccountDto> getAccountByHealthIdNumber(String healthIdNumber) {
        return abhaDBAccountFClient.getAccountByHealthIdNumber(healthIdNumber)
                .onErrorResume((throwable -> Mono.error(new AbhaDBGatewayUnavailableException())));
    }

    @Override
    public Mono<AccountDto> updateAccountByHealthIdNumber(AccountDto accountDto, String healthIdNumber) {
        accountDto.setLstUpdatedBy(ContextHolder.getClientId()!=null?ContextHolder.getClientId():DEFAULT_CLIENT_ID);
        accountDto.setUpdateDate(LocalDateTime.now());
        return abhaDBAccountFClient.updateAccount(accountDto, healthIdNumber)
                .onErrorResume((throwable -> Mono.error(new AbhaDBGatewayUnavailableException())));
    }

    @Override
    public Mono<AccountDto> getAccountByDocumentCode(String documentCode) {
        return abhaDBAccountFClient.getAccountEntityByDocumentCode(documentCode)
                .onErrorResume((throwable -> Mono.error(new AbhaDBGatewayUnavailableException())));
    }

    @Override
    public Flux<AccountDto> getAccountsByHealthIdNumbers(List<String> healthIdNumbers) {
        return abhaDBAccountFClient.getAccountsByHealthIdNumbers(healthIdNumbers.stream().collect(Collectors.joining(",")))
                .onErrorResume((throwable -> Mono.error(new AbhaDBGatewayUnavailableException())));
    }

    @Override
    public Mono<Integer> getMobileLinkedAccountCount(String mobileNumber) {
        return abhaDBAccountFClient.getMobileLinkedAccountCount(mobileNumber)
                .onErrorResume((throwable -> Mono.error(new AbhaDBGatewayUnavailableException())));
    }

    private void breakName(AccountDto accountDto) {

        String firstName = "";
        String lastName = "";
        String middleName = "";

        if (!StringUtils.isEmpty(accountDto.getName())) {
            List<String> name = new ArrayList<>(Arrays.asList(accountDto.getName().split(" ")));
            if (name.size() == 1) {
                firstName = name.get(0);
            } else if (name.size() == 2) {
                firstName = name.get(0);
                lastName = name.get(1);
            } else {
                firstName = name.get(0);
                lastName = name.get(name.size() - 1);
                name.remove(0);
                name.remove(name.size() - 1);
                middleName = String.join(" ", name);
            }

        }
        accountDto.setFirstName(GeneralUtils.stringTrimmer(firstName));
        accountDto.setLastName(GeneralUtils.stringTrimmer(lastName));
        accountDto.setMiddleName(GeneralUtils.stringTrimmer(middleName));
    }

    private void setDateOfBrith(String birthdate, AccountDto accountDto) {
        if (birthdate != null && birthdate.length() > 4) {
            try {

                LocalDate birthDate = kycDateFormat.parse(birthdate).toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                accountDto.setMonthOfBirth(String.valueOf(birthDate.getMonth().getValue()));
                accountDto.setDayOfBirth(String.valueOf(birthDate.getDayOfMonth()));
                accountDto.setYearOfBirth(String.valueOf(birthDate.getYear()));
            } catch (ParseException e) {
                log.error(PARSER_EXCEPTION_OCCURRED_DURING_PARSING, e);
            } catch (Exception ex) {
                log.error(EXCEPTION_IN_PARSING_INVALID_VALUE_OF_DOB, birthdate);
                log.error(ex.getMessage(), ex);
            }
        } else if (birthdate != null && birthdate.length() == 4) {
            accountDto.setYearOfBirth(birthdate);
        }
    }

    private AccountDto findUserAlreadyExist() {
        return new AccountDto();
    }

    public Mono<AccountDto> createAccountEntity(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, AccountDto accountDto, RequestHeaders requestHeaders) {
        String subject = requestHeaders.getFTokenClaims() == null ? null : requestHeaders.getFTokenClaims().get(SUB).toString();
        if (requestHeaders.getFTokenClaims() == null && subject == null) {
            accountDto.setOrigin(requestHeaders.getClientId() != null ? requestHeaders.getClientId() : null);
            accountDto.setLstUpdatedBy(requestHeaders.getClientId() != null ? requestHeaders.getClientId() : null);
        } else {
            accountDto.setOrigin(requestHeaders.getClientId() != null ? requestHeaders.getClientId() : null);
            accountDto.setFacilityId(subject != null ? String.valueOf(requestHeaders.getFTokenClaims().get(SUB)) : null);
            accountDto.setLstUpdatedBy(subject != null ? String.valueOf(requestHeaders.getFTokenClaims().get(SUB)) : null);
        }
        accountDto.setNewAccount(true);
        accountDto.setUpdateDate(LocalDateTime.now());
        accountDto.setCreatedDate(LocalDateTime.now());

        if (requestHeaders.getBenefitName() != null && !accountDto.getVerificationType().equals(DRIVING_LICENCE)
                && (enrolByAadhaarRequestDto.getAuthData().getAuthMethods().contains(AuthMethods.OTP)
                || enrolByAadhaarRequestDto.getAuthData().getAuthMethods().contains(AuthMethods.DEMO)
                || enrolByAadhaarRequestDto.getAuthData().getAuthMethods().contains(AuthMethods.BIO))) {
            //HID benefit Flow
            return handleAccountByBenefitProgram(accountDto, requestHeaders);
        } else {
            //normal flow
            return abhaDBAccountFClient.createAccount(accountDto)
                    .onErrorResume((throwable -> Mono.error(new AbhaDBGatewayUnavailableException(throwable.getMessage()))));
        }
    }

    private Mono<AccountDto> handleAccountByBenefitProgram(AccountDto accountDto, RequestHeaders requestHeaders) {
        List<IntegratedProgramDto> integratedProgramDtos = redisService.getIntegratedPrograms();
        if (!validBenefitProgram(requestHeaders, integratedProgramDtos)) {
            return redisService.reloadAndGetIntegratedPrograms().flatMap(integratedProgramDtoList -> validateBenefitIfExistsAndCreateAccount(integratedProgramDtos, accountDto, requestHeaders));
        } else {
            return validateBenefitIfExistsAndCreateAccount(integratedProgramDtos, accountDto, requestHeaders);
        }
    }

    private boolean validBenefitProgram(RequestHeaders requestHeaders, List<IntegratedProgramDto> integratedProgramDtos) {
        return !integratedProgramDtos.isEmpty() && integratedProgramDtos.stream().anyMatch(res -> res.getBenefitName().equals(requestHeaders.getBenefitName()));
    }

    private Mono<AccountDto> findBenefitIfNotPresentAndCreateAccount(List<IntegratedProgramDto> integratedProgramDtos, AccountDto accountDto, RequestHeaders requestHeaders) {
        String requestId = UUID.randomUUID().toString();
        String timestamp = String.valueOf(LocalDateTime.now());
        return integratedProgramDBFClient.getAll(requestId, timestamp)
                .collectList().flatMap(Mono::just).flatMap(integratedProgramDtoList -> {
                    integratedProgramDtos.clear();
                    integratedProgramDtos.addAll(integratedProgramDtoList);

                    if (integratedProgramDtos.stream().anyMatch(res -> res.getBenefitName().equals(requestHeaders.getBenefitName()) && res.getClientId().equals(requestHeaders.getClientId()))
                            && requestHeaders.getRoleList().contains(INTEGRATED_PROGRAM_ROLE)) {
                        return hidBenefitDBFClient.saveHidBenefit(prepareHidBenefitDto(accountDto, requestHeaders, integratedProgramDtos))
                                .flatMap(response -> abhaDBAccountFClient.createAccount(accountDto)
                                        .onErrorResume((throwable -> Mono.error(new AbhaDBGatewayUnavailableException(throwable.getMessage())))));
                    } else {
                        throw new BenefitNotFoundException(ABDMError.BENEFIT_NOT_FOUND.getCode(), ABDMError.BENEFIT_NOT_FOUND.getMessage());
                    }
                }).switchIfEmpty(Mono.defer(() -> {
                    throw new BenefitNotFoundException(ABDMError.BENEFIT_NOT_FOUND.getCode(), ABDMError.BENEFIT_NOT_FOUND.getMessage());
                }));
    }

    private Mono<AccountDto> validateBenefitIfExistsAndCreateAccount(List<IntegratedProgramDto> integratedProgramDtos, AccountDto accountDto, RequestHeaders requestHeaders) {

        if (integratedProgramDtos.stream().anyMatch(integratedProgramDto -> integratedProgramDto.getClientId().equals(requestHeaders.getClientId()))
                && requestHeaders.getRoleList().contains(INTEGRATED_PROGRAM_ROLE)) {

            return hidBenefitDBFClient.saveHidBenefit(prepareHidBenefitDto(accountDto, requestHeaders, integratedProgramDtos))
                    .flatMap(response -> abhaDBAccountFClient.createAccount(accountDto)
                            .onErrorResume((throwable -> Mono.error(new AbhaDBGatewayUnavailableException(throwable.getMessage())))));
        } else {
            throw new BenefitNotFoundException(ABDMError.BENEFIT_NOT_FOUND.getCode(), ABDMError.BENEFIT_NOT_FOUND.getMessage());
        }
    }

    private HidBenefitDto prepareHidBenefitDto(AccountDto accountDto, RequestHeaders requestHeaders, List<IntegratedProgramDto> integratedProgramDtos) {
        String benefitId = String.valueOf(Common.systemGeneratedBenefitId());
        List<IntegratedProgramDto> integratedProgramDtoList
                = integratedProgramDtos.stream().filter(v -> v.getBenefitName().equals(requestHeaders.getBenefitName())
                && v.getClientId().equals(requestHeaders.getClientId())).collect(Collectors.toList());
        List<String> programName = integratedProgramDtoList.stream().map(IntegratedProgramDto::getProgramName).collect(Collectors.toList());

        return HidBenefitDto.builder()
                .benefitName(requestHeaders.getBenefitName())
                .programName(programName.get(0) != null ? programName.get(0) : null)
                .benefitId(benefitId)
                .validTill(LocalDateTime.now())
                .status(HidBenefitStatus.LINKED.value())
                .createdBy(requestHeaders.getClientId() != null ? requestHeaders.getClientId() : null)
                .stateCode(accountDto.getStateCode())
                .linkedBy(requestHeaders.getClientId() != null ? requestHeaders.getClientId() : null)
                .linkedDate(LocalDateTime.now())
                .healthIdNumber(accountDto.getHealthIdNumber())
                .mobileNumber(accountDto.getMobile()!=null ? accountDto.getMobile() : null)
                .build();
    }

    @Override
    public Mono<Integer> getEmailLinkedAccountCount(String email) {
        return abhaDBAccountFClient.getEmailLinkedAccountCount(email)
                .onErrorResume((throwable -> Mono.error(new AbhaDBGatewayUnavailableException())));
    }

    @Override
    public Mono<String> saveAllData(SaveAllDataRequest saveAllDataRequest) {
        return abhaDBAccountFClient.saveAllData(saveAllDataRequest)
                .onErrorResume((throwable ->
                {
                    log.info(throwable.getMessage());
                    return Mono.error(new AbhaDBException(throwable.getMessage()));
                }));
    }

    public Mono<AccountDto> settingClientIdAndOrigin(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, AccountDto accountDto, RequestHeaders requestHeaders) {
        String subject = requestHeaders.getFTokenClaims() == null ? null : requestHeaders.getFTokenClaims().get(SUB).toString();
        if (requestHeaders.getFTokenClaims() == null && subject == null) {
            accountDto.setOrigin(requestHeaders.getClientId() != null ? requestHeaders.getClientId() : null);
            accountDto.setLstUpdatedBy(requestHeaders.getClientId() != null ? requestHeaders.getClientId() : null);
        } else {
            accountDto.setOrigin(requestHeaders.getClientId() != null ? requestHeaders.getClientId() : null);
            accountDto.setFacilityId(subject != null ? String.valueOf(requestHeaders.getFTokenClaims().get(SUB)) : null);
            accountDto.setLstUpdatedBy(subject != null ? String.valueOf(requestHeaders.getFTokenClaims().get(SUB)) : null);
        }
        accountDto.setNewAccount(true);
        accountDto.setUpdateDate(LocalDateTime.now());
        accountDto.setCreatedDate(LocalDateTime.now());

        if (requestHeaders.getBenefitName() != null && !accountDto.getVerificationType().equals(DRIVING_LICENCE)
                && (enrolByAadhaarRequestDto.getAuthData().getAuthMethods().contains(AuthMethods.OTP)
                || enrolByAadhaarRequestDto.getAuthData().getAuthMethods().contains(AuthMethods.DEMO)
                || enrolByAadhaarRequestDto.getAuthData().getAuthMethods().contains(AuthMethods.BIO))) {
            //HID benefit Flow
            return handleAccountByBenefitProgramForSpCall(accountDto, requestHeaders);
        } else {
            //normal flow
            return Mono.just(accountDto);
        }
    }

    private Mono<AccountDto> handleAccountByBenefitProgramForSpCall(AccountDto accountDto, RequestHeaders requestHeaders) {
        List<IntegratedProgramDto> integratedProgramDtos = redisService.getIntegratedPrograms();
        if (!validBenefitProgram(requestHeaders, integratedProgramDtos)) {
            return redisService.reloadAndGetIntegratedPrograms().flatMap(integratedProgramDtoList -> validateBenefitIfExistsAndCreateAccountForSpCall(integratedProgramDtos, accountDto, requestHeaders));
        } else {
            return validateBenefitIfExistsAndCreateAccountForSpCall(integratedProgramDtos, accountDto, requestHeaders);
        }
    }

    private Mono<AccountDto> validateBenefitIfExistsAndCreateAccountForSpCall(List<IntegratedProgramDto> integratedProgramDtos, AccountDto accountDto, RequestHeaders requestHeaders) {

        if (integratedProgramDtos.stream().anyMatch(integratedProgramDto -> integratedProgramDto.getClientId().equals(requestHeaders.getClientId()))
                && requestHeaders.getRoleList().contains(INTEGRATED_PROGRAM_ROLE)) {

            return hidBenefitDBFClient.saveHidBenefit(prepareHidBenefitDto(accountDto, requestHeaders, integratedProgramDtos))
                    .flatMap(response -> Mono.just(accountDto)
                            .onErrorResume((throwable -> Mono.error(new AbhaDBGatewayUnavailableException(throwable.getMessage())))));
        } else {
            throw new BenefitNotFoundException(ABDMError.BENEFIT_NOT_FOUND.getCode(), ABDMError.BENEFIT_NOT_FOUND.getMessage());
        }
    }


    private Mono<AccountDto> findBenefitIfNotPresentAndCreateAccountForSpCall(List<IntegratedProgramDto> integratedProgramDtos, AccountDto accountDto, RequestHeaders requestHeaders) {
        String requestId = UUID.randomUUID().toString();
        String timestamp = String.valueOf(LocalDateTime.now());
        return integratedProgramDBFClient.getAll(requestId, timestamp)
                .collectList().flatMap(Mono::just).flatMap(integratedProgramDtoList -> {
                    integratedProgramDtos.clear();
                    integratedProgramDtos.addAll(integratedProgramDtoList);

                    if (integratedProgramDtos.stream().anyMatch(res -> res.getBenefitName().equals(requestHeaders.getBenefitName()) && res.getClientId().equals(requestHeaders.getClientId()))
                            && requestHeaders.getRoleList().contains(INTEGRATED_PROGRAM_ROLE)) {
                        return hidBenefitDBFClient.saveHidBenefit(prepareHidBenefitDto(accountDto, requestHeaders, integratedProgramDtos))
                                .flatMap(response -> Mono.just(accountDto)
                                        .onErrorResume((throwable -> Mono.error(new AbhaDBGatewayUnavailableException(throwable.getMessage())))));
                    } else {
                        throw new BenefitNotFoundException(ABDMError.BENEFIT_NOT_FOUND.getCode(), ABDMError.BENEFIT_NOT_FOUND.getMessage());
                    }
                }).switchIfEmpty(Mono.defer(() -> {
                    throw new BenefitNotFoundException(ABDMError.BENEFIT_NOT_FOUND.getCode(), ABDMError.BENEFIT_NOT_FOUND.getMessage());
                }));
    }
}
