package in.gov.abdm.abha.enrollment.services.enrol.aadhaar.demographic;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.PropertyConstants;
import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.enums.AccountAuthMethods;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.enums.childabha.AbhaType;
import in.gov.abdm.abha.enrollment.exception.abha_db.AbhaDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.application.AbhaUnProcessableException;
import in.gov.abdm.abha.enrollment.exception.notification.NotificationGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicRequest;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.demographic.Demographic;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ResponseTokensDto;
import in.gov.abdm.abha.enrollment.model.entities.*;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import in.gov.abdm.abha.enrollment.model.procedure.SaveAllDataRequest;
import in.gov.abdm.abha.enrollment.services.aadhaar.AadhaarAppService;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.account_auth_methods.AccountAuthMethodService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.de_duplication.DeDuplicationService;
import in.gov.abdm.abha.enrollment.services.document.IdentityDocumentDBService;
import in.gov.abdm.abha.enrollment.services.notification.NotificationService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.LgdUtility;
import in.gov.abdm.abha.enrollment.utilities.MapperUtils;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaAddressGenerator;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaNumberGenerator;
import in.gov.abdm.abha.enrollment.utilities.jwt.JWTUtil;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import in.gov.abdm.error.ABDMError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.DEFAULT_CLIENT_ID;
import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.SENT;

@Slf4j
@Service
public class EnrolByDemographicService extends EnrolByDemographicValidatorService {

    @Autowired
    private AadhaarAppService aadhaarAppService;
    @Autowired
    private RSAUtil rsaUtils;
    @Autowired
    private AccountService accountService;
    @Autowired
    private HidPhrAddressService hidPhrAddressService;
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private AbhaAddressGenerator abhaAddressGenerator;
    @Autowired
    private AccountAuthMethodService accountAuthMethodService;
    @Autowired
    NotificationService notificationService;
    @Autowired
    IdentityDocumentDBService identityDocumentDBService;
    @Autowired
    LgdUtility lgdUtility;
    @Autowired
    DeDuplicationService deDuplicationService;
    @Value(PropertyConstants.ENROLLMENT_MAX_MOBILE_LINKING_COUNT)
    private int maxMobileLinkingCount;
    @Value(PropertyConstants.ENROLLMENT_IS_TRANSACTION)
    private boolean isTransactionManagementEnable;

    public Mono<EnrolByAadhaarResponseDto> validateAndEnrolByDemoAuth(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, RequestHeaders requestHeaders) {
        Demographic demographic = enrolByAadhaarRequestDto.getAuthData().getDemographic();
        VerifyDemographicRequest verifyDemographicRequest = new VerifyDemographicRequest();
        verifyDemographicRequest.setAadhaarNumber(rsaUtils.decrypt(demographic.getAadhaarNumber()));
        verifyDemographicRequest.setName(Common.getName(demographic.getFirstName(), demographic.getMiddleName(), demographic.getLastName()));
        verifyDemographicRequest.setDob(formatDob(demographic.getYearOfBirth(), demographic.getMonthOfBirth(), demographic.getDayOfBirth()));
        verifyDemographicRequest.setGender(demographic.getGender());
        String mobileNumber = enrolByAadhaarRequestDto.getAuthData().getDemographic().getMobile();
        Mono<Integer> mobileLinkedAccountCountMono = Mono.just(0);
        if (!StringUtils.isEmpty(mobileNumber)) {
            mobileLinkedAccountCountMono = accountService.getMobileLinkedAccountCount(mobileNumber);
        }
        return mobileLinkedAccountCountMono.flatMap(mobileLinkedAccountCount -> {
            if (mobileLinkedAccountCount >= maxMobileLinkingCount) {
                throw new AbhaUnProcessableException(ABDMError.MOBILE_ALREADY_LINKED_TO_6_ACCOUNTS);
            } else {
                return aadhaarAppService.verifyDemographicDetails(verifyDemographicRequest)
                        .flatMap(verifyDemographicResponse -> {
                            if (verifyDemographicResponse.isVerified()) {
                                //check if account exist
                                return accountService.findByXmlUid(verifyDemographicResponse.getXmlUid())
                                        .flatMap(existingAccount -> {
                                            if (existingAccount.getStatus().equals(AccountStatus.DELETED.getValue())) {
                                                return createNewAccount(enrolByAadhaarRequestDto, verifyDemographicResponse.getXmlUid(), requestHeaders);
                                            } else if (existingAccount.getStatus().equals(AccountStatus.DEACTIVATED.getValue())) {
                                                return respondExistingAccount(existingAccount, false, AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST_AND_DEACTIVATED);
                                            } else {
                                                //existing account
                                                return respondExistingAccount(existingAccount, true, AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST);
                                            }
                                        })
                                        .switchIfEmpty(Mono.defer(() -> createNewAccount(enrolByAadhaarRequestDto, verifyDemographicResponse.getXmlUid(), requestHeaders)));
                            } else {
                                throw new AbhaUnProcessableException(ABDMError.INVALID_DEMOGRAPHIC_DETAILS);
                            }
                        });
            }
        });

    }

    private Mono<EnrolByAadhaarResponseDto> createNewAccount(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, String xmlUid, RequestHeaders requestHeaders) {
        Demographic demographic = enrolByAadhaarRequestDto.getAuthData().getDemographic();
        String newAbhaNumber = AbhaNumberGenerator.generateAbhaNumber();
        String defaultAbhaAddress = abhaAddressGenerator.generateDefaultAbhaAddress(newAbhaNumber);

        AccountDto accountDto = new AccountDto();
        accountDto.setFirstName(demographic.getFirstName() != null ? demographic.getFirstName().trim() : null);
        accountDto.setMiddleName(demographic.getMiddleName() != null ? demographic.getMiddleName().trim() : null);
        accountDto.setLastName(demographic.getLastName() != null ? demographic.getLastName().trim() : null);
        accountDto.setName(Common.getName(accountDto.getFirstName(), accountDto.getMiddleName(), accountDto.getLastName()));
        accountDto.setConsentDate(LocalDateTime.now());
        accountDto.setVerificationStatus(AbhaConstants.VERIFIED);
        accountDto.setVerificationType(AbhaConstants.OFFLINE_AADHAAR);
        accountDto.setYearOfBirth(demographic.getYearOfBirth());
        accountDto.setMonthOfBirth(preFixZero(demographic.getMonthOfBirth()));
        accountDto.setDayOfBirth(preFixZero(demographic.getDayOfBirth()));
        accountDto.setKycdob(Common.getDob(accountDto.getDayOfBirth(), accountDto.getMonthOfBirth(), accountDto.getYearOfBirth()));
        accountDto.setAddress(demographic.getAddress() != null ? demographic.getAddress().trim() : null);
        accountDto.setGender(demographic.getGender());
        accountDto.setConsentVersion(enrolByAadhaarRequestDto.getConsent().getVersion());
        accountDto.setConsentDate(LocalDateTime.now());
        accountDto.setHealthId(defaultAbhaAddress);
        accountDto.setXmluid(xmlUid);
        accountDto.setPincode(demographic.getPinCode());
        accountDto.setHealthIdNumber(newAbhaNumber);
        accountDto.setMobile(demographic.getMobile());
        accountDto.setHealthWorkerName(demographic.getHealthWorkerName());
        accountDto.setHealthWorkerMobile(demographic.getHealthWorkerMobile());
        accountDto.setStatus(AccountStatus.ACTIVE.getValue());
        accountDto.setMobileType(demographic.getMobileType().getValue());
        accountDto.setKycVerified(true);

        return deDuplicationService.checkDeDuplication(deDuplicationService.prepareRequest(accountDto))
                .flatMap(duplicateAccount -> {
                    return respondExistingAccount(duplicateAccount, false, AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST);
                }).switchIfEmpty(Mono.defer(() -> {
                    int age = Common.calculateYearDifference(accountDto.getYearOfBirth(), accountDto.getMonthOfBirth(), accountDto.getDayOfBirth());
                    if (age >= 18) {
                        accountDto.setType(AbhaType.STANDARD);
                    } else {
                        accountDto.setType(AbhaType.CHILD);
                    }
                    return lgdUtility.getLgdData(demographic.getPinCode(), demographic.getState())
                            .flatMap(lgdDistrictResponses -> {
                                if (!lgdDistrictResponses.isEmpty()) {
                                    LgdDistrictResponse lgdDistrictResponse = Common.getLGDDetails(lgdDistrictResponses);
                                    accountDto.setDistrictCode(lgdDistrictResponse.getDistrictCode());
                                    accountDto.setDistrictName(lgdDistrictResponse.getDistrictName() == null || lgdDistrictResponse.getDistrictName().equalsIgnoreCase("Unknown") ? demographic.getDistrict() : lgdDistrictResponse.getDistrictName());
                                    accountDto.setStateCode(lgdDistrictResponse.getStateCode());
                                    accountDto.setStateName(lgdDistrictResponse.getStateName());
                                } else {
                                    accountDto.setDistrictName(demographic.getDistrict());
                                    accountDto.setStateName(demographic.getState());
                                }
                                return saveAccountDetails(enrolByAadhaarRequestDto, accountDto, demographic.getConsentFormImage(), requestHeaders);
                            });
                }));
    }

    private Mono<EnrolByAadhaarResponseDto> saveAccountDetails(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, AccountDto accountDto, String consentFormImage, RequestHeaders requestHeaders) {
        if(isTransactionManagementEnable){
            return accountService.settingClientIdAndOrigin(enrolByAadhaarRequestDto, accountDto, requestHeaders).flatMap(accountDtoResponse -> {
                //HidPhrAddressDto hidPhrAddressDto = hidPhrAddressService.prepareNewHidPhrAddress(accountDtoResponse);
                return callProcedureToCreateAccount(accountDtoResponse,requestHeaders);
            });
        }else{
            return accountService.createAccountEntity(enrolByAadhaarRequestDto, accountDto, requestHeaders).flatMap(accountDtoResponse -> {
                HidPhrAddressDto hidPhrAddressDto = hidPhrAddressService.prepareNewHidPhrAddress(accountDtoResponse);
                return hidPhrAddressService.createHidPhrAddressEntity(hidPhrAddressDto).flatMap(phrAddressDto -> addDocumentsInIdentityDocumentEntity(accountDto, consentFormImage)
                        .flatMap(identityDocumentsDto -> addAuthMethods(accountDto, hidPhrAddressDto,requestHeaders)));
            });
        }

    }

    private Mono<IdentityDocumentsDto> addDocumentsInIdentityDocumentEntity(AccountDto accountDto, String consentFromImage) {

        IdentityDocumentsDto identityDocumentsDto = new IdentityDocumentsDto();
        identityDocumentsDto.setDocumentNumber(accountDto.getXmluid());
        identityDocumentsDto.setDocumentType(AbhaConstants.OFFLINE_AADHAAR);
        identityDocumentsDto.setDob(Common.getDob(accountDto.getDayOfBirth(), accountDto.getMonthOfBirth(), accountDto.getYearOfBirth()));
        identityDocumentsDto.setGender(accountDto.getGender());
        identityDocumentsDto.setFirstName(accountDto.getFirstName());
        identityDocumentsDto.setLastName(accountDto.getLastName());
        identityDocumentsDto.setMiddleName(accountDto.getMiddleName());
        identityDocumentsDto.setStatus(accountDto.getStatus());
        identityDocumentsDto.setHealthIdNumber(accountDto.getHealthIdNumber());
        identityDocumentsDto.setPhoto(consentFromImage);
        identityDocumentsDto.setVerificationStatus(accountDto.getVerificationStatus());
        identityDocumentsDto.setVerificationType(AbhaConstants.OFFLINE_AADHAAR);

        return identityDocumentDBService.addIdentityDocuments(identityDocumentsDto);
    }

    private Mono<EnrolByAadhaarResponseDto> addAuthMethods(AccountDto accountDto, HidPhrAddressDto hidPhrAddressDto, RequestHeaders requestHeaders) {
        List<AccountAuthMethodsDto> accountAuthMethodsDtoList = new ArrayList<>();
        accountAuthMethodsDtoList.add(new AccountAuthMethodsDto(accountDto.getHealthIdNumber(), AccountAuthMethods.AADHAAR_OTP.getValue()));
        accountAuthMethodsDtoList.add(new AccountAuthMethodsDto(accountDto.getHealthIdNumber(), AccountAuthMethods.DEMOGRAPHICS.getValue()));
        accountAuthMethodsDtoList.add(new AccountAuthMethodsDto(accountDto.getHealthIdNumber(), AccountAuthMethods.AADHAAR_BIO.getValue()));
        accountAuthMethodsDtoList.add(new AccountAuthMethodsDto(accountDto.getHealthIdNumber(), AccountAuthMethods.MOBILE_OTP.getValue()));
        return accountAuthMethodService.addAccountAuthMethods(accountAuthMethodsDtoList)
                .flatMap(authMethods -> sendAccountCreatedSMS(accountDto, hidPhrAddressDto, requestHeaders));
    }

    private Mono<EnrolByAadhaarResponseDto> sendAccountCreatedSMS(AccountDto accountDto, HidPhrAddressDto hidPhrAddressDto, RequestHeaders requestHeaders) {
        if (accountDto.getMobile() != null && !accountDto.getMobile().isBlank() && !DEFAULT_CLIENT_ID.equalsIgnoreCase(requestHeaders.getClientId())) {
            return notificationService.sendABHACreationSMS(accountDto.getMobile(), accountDto.getName(), accountDto.getHealthIdNumber()).flatMap(notificationResponseDto -> {
                if (notificationResponseDto.getStatus().equals(SENT)) {
                    ResponseTokensDto responseTokensDto = ResponseTokensDto.builder()
                            .token(jwtUtil.generateToken(UUID.randomUUID().toString(), accountDto))
                            .expiresIn(jwtUtil.jwtTokenExpiryTime())
                            .refreshToken(jwtUtil.generateRefreshToken(accountDto.getHealthIdNumber()))
                            .refreshExpiresIn(jwtUtil.jwtRefreshTokenExpiryTime())
                            .build();
                    ABHAProfileDto abhaProfileDto = MapperUtils.mapProfileDetails(accountDto);
                    abhaProfileDto.setPhrAddress(Collections.singletonList(hidPhrAddressDto.getPhrAddress()));
                    //Final create account response
                    return Mono.just(EnrolByAadhaarResponseDto.builder()
                            .abhaProfileDto(abhaProfileDto)
                            .message(AbhaConstants.ACCOUNT_CREATED_SUCCESSFULLY)
                            .isNew(true)
                            .responseTokensDto(responseTokensDto).build());
                } else {
                    throw new NotificationGatewayUnavailableException();
                }
            });
        }else{
            ResponseTokensDto responseTokensDto = ResponseTokensDto.builder()
                    .token(jwtUtil.generateToken(UUID.randomUUID().toString(), accountDto))
                    .expiresIn(jwtUtil.jwtTokenExpiryTime())
                    .refreshToken(jwtUtil.generateRefreshToken(accountDto.getHealthIdNumber()))
                    .refreshExpiresIn(jwtUtil.jwtRefreshTokenExpiryTime())
                    .build();
            ABHAProfileDto abhaProfileDto = MapperUtils.mapProfileDetails(accountDto);
            abhaProfileDto.setPhrAddress(Collections.singletonList(hidPhrAddressDto.getPhrAddress()));
            //Final create account response
            return Mono.just(EnrolByAadhaarResponseDto.builder()
                    .abhaProfileDto(abhaProfileDto)
                    .message(AbhaConstants.ACCOUNT_CREATED_SUCCESSFULLY)
                    .isNew(true)
                    .responseTokensDto(responseTokensDto).build());
        }

    }

    private Mono<EnrolByAadhaarResponseDto> respondExistingAccount(AccountDto accountDto, boolean generateToken, String responseMessage) {
        ABHAProfileDto abhaProfileDto = MapperUtils.mapProfileDetails(accountDto);
        String txnId = UUID.randomUUID().toString();
        Flux<String> fluxPhrAddress = hidPhrAddressService
                .getHidPhrAddressByHealthIdNumbersAndPreferredIn(Arrays.asList(accountDto.getHealthIdNumber()), Arrays.asList(1, 0))
                .map(HidPhrAddressDto::getPhrAddress);

        return fluxPhrAddress.collectList().flatMap(Mono::just).flatMap(phrAddressList -> {
            abhaProfileDto.setPhrAddress(phrAddressList);

            EnrolByAadhaarResponseDto enrolByAadhaarResponseDto = EnrolByAadhaarResponseDto.builder()
                    .abhaProfileDto(abhaProfileDto)
                    .message(responseMessage)
                    .isNew(false)
                    .build();

            //Final response for existing user
            if (generateToken) {
                ResponseTokensDto responseTokensDto = ResponseTokensDto.builder()
                        .token(jwtUtil.generateToken(txnId, accountDto))
                        .expiresIn(jwtUtil.jwtTokenExpiryTime())
                        .refreshToken(jwtUtil.generateRefreshToken(accountDto.getHealthIdNumber()))
                        .refreshExpiresIn(jwtUtil.jwtRefreshTokenExpiryTime())
                        .build();
                enrolByAadhaarResponseDto.setResponseTokensDto(responseTokensDto);
            }
            return Mono.just(enrolByAadhaarResponseDto);
        });
    }

    private String preFixZero(String value) {
        return value != null && value.length() == 1 ? "0" + value : value;
    }

    private String formatDob(String year, String month, String day) {
        return year + StringConstants.DASH + preFixZero(month) + StringConstants.DASH + preFixZero(day);
    }


    private Mono<EnrolByAadhaarResponseDto> callProcedureToCreateAccount(AccountDto accountDtoResponse, RequestHeaders requestHeaders) {
        List<AccountDto> accountList = new ArrayList<>();
        List<HidPhrAddressDto> hidPhrAddressDtoList = new ArrayList<>();
        accountList.add(accountDtoResponse);
        HidPhrAddressDto hidPhrAddressDto = hidPhrAddressService.prepareNewHidPhrAddress(accountDtoResponse);
        hidPhrAddressDtoList.add(hidPhrAddressDto);
        if (!accountDtoResponse.getHealthIdNumber().isEmpty()) {
            List<AccountAuthMethodsDto> accountAuthMethodsDtos = new ArrayList<>();
            accountAuthMethodsDtos.add(new AccountAuthMethodsDto(accountDtoResponse.getHealthIdNumber(), AccountAuthMethods.AADHAAR_OTP.getValue()));
            accountAuthMethodsDtos.add(new AccountAuthMethodsDto(accountDtoResponse.getHealthIdNumber(), AccountAuthMethods.DEMOGRAPHICS.getValue()));
            accountAuthMethodsDtos.add(new AccountAuthMethodsDto(accountDtoResponse.getHealthIdNumber(), AccountAuthMethods.AADHAAR_BIO.getValue()));
            if (accountDtoResponse.getMobile() != null) {
                accountAuthMethodsDtos.add(new AccountAuthMethodsDto(accountDtoResponse.getHealthIdNumber(), AccountAuthMethods.MOBILE_OTP.getValue()));
            }

            log.info("going to call procedure to create account using demographic");
            return accountService.saveAllData(SaveAllDataRequest.builder().accounts(accountList).hidPhrAddress(hidPhrAddressDtoList).accountAuthMethods(accountAuthMethodsDtos).build())
                    .flatMap(v -> sendAccountCreatedSMS(accountDtoResponse,hidPhrAddressDto,requestHeaders));
        } else {
            throw new AbhaDBGatewayUnavailableException();
        }
    }

}
