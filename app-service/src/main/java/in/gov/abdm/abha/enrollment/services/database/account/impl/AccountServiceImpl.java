package in.gov.abdm.abha.enrollment.services.database.account.impl;

import in.gov.abdm.abha.enrollment.client.AbhaDBClient;
import in.gov.abdm.abha.enrollment.client.AbhaDBAccountFClient;
import in.gov.abdm.abha.enrollment.configuration.ContextHolder;
import in.gov.abdm.abha.enrollment.configuration.FacilityContextHolder;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.enums.AccountAuthMethods;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.exception.abha_db.AbhaDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

@Service
@Slf4j
public class AccountServiceImpl extends AbhaDBClient implements AccountService {

    @Autowired
    AbhaDBAccountFClient abhaDBAccountFClient;

    public static final String PARSER_EXCEPTION_OCCURRED_DURING_PARSING = "Parser Exception occurred during parsing :";
    public static final String EXCEPTION_IN_PARSING_INVALID_VALUE_OF_DOB = "Exception in parsing Invalid value of DOB: {}";
    private DateFormat KYC_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    public Mono<AccountDto> findByXmlUid(String xmlUid) {
        return abhaDBAccountFClient.getAccountByXmlUid(Common.base64Encode(xmlUid))
                .onErrorResume((throwable -> Mono.error(new AbhaDBGatewayUnavailableException())));
    }

    @Override
    public Mono<AccountDto> prepareNewAccount(TransactionDto transactionDto, EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, List<LgdDistrictResponse> lgdDistrictResponses) {
        AccountDto newUser = new AccountDto();

        AccountDto accountDto = findUserAlreadyExist(transactionDto);
        if (isItNewUser(accountDto)) {
            newUser.setAddress(transactionDto.getAddress());
            newUser.setName(transactionDto.getName());
            newUser.setGender(transactionDto.getGender());

            if (accountDto.getKycPhoto() == null) {
                newUser.setKycPhoto(transactionDto.getKycPhoto());
            }
            newUser.setProfilePhoto(transactionDto.getKycPhoto());
            if (!StringUtils.isBlank(transactionDto.getPincode())) {
                newUser.setPincode(transactionDto.getPincode());
            }
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
            newUser.setXmlUID(transactionDto.getXmluid());
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
        accountDto.setLstUpdatedBy(ContextHolder.getClientId());
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

                LocalDate birthDate = KYC_DATE_FORMAT.parse(birthdate).toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                accountDto.setMonthOfBirth(String.valueOf(birthDate.getMonth().getValue()));
                accountDto.setDayOfBirth(String.valueOf(birthDate.getDayOfMonth()));
                accountDto.setYearOfBirth(String.valueOf(birthDate.getYear()));
            } catch (ParseException e) {
                log.error(PARSER_EXCEPTION_OCCURRED_DURING_PARSING, e);
            } catch (Exception ex) {
                log.error(EXCEPTION_IN_PARSING_INVALID_VALUE_OF_DOB, birthdate);
            }
        } else if (birthdate != null && birthdate.length() == 4) {
            accountDto.setYearOfBirth(birthdate);
        }
    }

    private AccountDto findUserAlreadyExist(TransactionDto transactionDto) {
        return new AccountDto();
    }

    public Mono<AccountDto> createAccountEntity(AccountDto accountDto) {
        if (FacilityContextHolder.getSubject() == null) {
            accountDto.setOrigin(ContextHolder.getClientId());
            accountDto.setLstUpdatedBy(ContextHolder.getClientId());
        } else {
            accountDto.setOrigin(ContextHolder.getClientId());
            accountDto.setFacilityId(FacilityContextHolder.getSubject());
            accountDto.setLstUpdatedBy(FacilityContextHolder.getSubject());
        }
        accountDto.setNewAccount(true);
        accountDto.setCreatedDate(LocalDateTime.now());
        return abhaDBAccountFClient.createAccount(accountDto)
                .onErrorResume((throwable -> Mono.error(new AbhaDBGatewayUnavailableException(throwable.getMessage()))));
    }

    @Override
    public Mono<Integer> getEmailLinkedAccountCount(String email) {
        return abhaDBAccountFClient.getEmailLinkedAccountCount(email)
                .onErrorResume((throwable-> Mono.error(new AbhaDBGatewayUnavailableException())));
    }
}
