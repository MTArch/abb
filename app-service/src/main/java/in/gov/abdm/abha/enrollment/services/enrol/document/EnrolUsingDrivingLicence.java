package in.gov.abdm.abha.enrollment.services.enrol.document;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.PropertyConstants;
import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.enums.AccountAuthMethods;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.enums.childabha.AbhaType;
import in.gov.abdm.abha.enrollment.exception.abha_db.AbhaDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.abha_db.TransactionNotFoundException;
import in.gov.abdm.abha.enrollment.exception.application.AbhaUnProcessableException;
import in.gov.abdm.abha.enrollment.exception.document.DocumentGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.notification.NotificationGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ResponseTokensDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolProfileDto;
import in.gov.abdm.abha.enrollment.model.enrol.facility.EnrollmentResponse;
import in.gov.abdm.abha.enrollment.model.entities.*;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import in.gov.abdm.abha.enrollment.model.nepix.VerifyDLRequest;
import in.gov.abdm.abha.enrollment.model.notification.NotificationResponseDto;
import in.gov.abdm.abha.enrollment.model.procedure.SaveAllDataRequest;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.account_auth_methods.AccountAuthMethodService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.de_duplication.DeDuplicationService;
import in.gov.abdm.abha.enrollment.services.document.DocumentAppService;
import in.gov.abdm.abha.enrollment.services.document.IdentityDocumentDBService;
import in.gov.abdm.abha.enrollment.services.notification.NotificationService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.EnrolmentCipher;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import in.gov.abdm.abha.enrollment.utilities.LgdUtility;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaAddressGenerator;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaNumberGenerator;
import in.gov.abdm.abha.enrollment.utilities.jwt.JWTUtil;
import in.gov.abdm.error.ABDMError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static in.gov.abdm.abha.constant.ABHAConstants.PROVISIONAL;
import static in.gov.abdm.abha.constant.ABHAConstants.VERIFIED;

@Slf4j
@Service
public class EnrolUsingDrivingLicence {

    private static final String FAILED_TO_DELETE_TRANSACTION = "Failed to delete Transaction: ";
    private static final String TRANSACTION = "Transaction ";
    private static final String FOUND = " found.";
    private static final String MOBILE_NUMBER_IS_VERIFIED = "Mobile number is verified";
    private static final String CLOSING = ")";
    private static final String WITH_SAME_DL = ") with same DL (";
    private static final String FOUND_ACCOUNT = "Found account (";
    private static final String ACCOUNT_NOT_FOUND_WITH_DL_VERIFYING_DL_DETAILS = "Account not found with DL, verifying DL details";
    private static final String MOBILE_NUMBER_NOT_VERIFIED = "mobile number not verified";
    private static final String DL_VERIFICATION_FAILED = "The details provided by you do not match against your documents details. Please provide the correct details.";
    private static final String DL_DETAILS_VERIFIED_CREATING_NEW_ENROLLMENT_ACCOUNT = "DL details verified, creating new Enrollment Account";
    private static final String DL_DETAILS_NOT_VERIFIED = "DL details not verified";
    private static final String NEW_ENROLLMENT_ACCOUNT_CREATED_AND_UPDATED_IN_DB = "new enrollment account created and updated in DB";
    private static final String TRANSACTION_DELETED = "transaction deleted";
    private static final String DL_DOCUMENTS_STORED_IN_ADV_DB = "DL documents stored in ADV DB";
    private static final String ACCOUNT_AUTH_METHODS_ADDED = "Account Auth methods added";
    public static final String DEFAULT_PHR_ADDRESS_UPDATED_IN_HID_PHR_ADDRESS_TABLE = "Default PHR Address Updated In HID PHR Address Table";
    private static final String NOTIFICATION_SENT_ON_ACCOUNT_CREATION = "Notification sent successfully on Account Creation";
    private static final String ON_MOBILE_NUMBER = "on Mobile Number:";
    private static final String FOR_HEALTH_ID_NUMBER = "for HealthIdNumber:";
    private static final String ENROL_VERIFICATION_STATUS = "success";
    private static final String ABHA_CREATED_SUCCESS = "ABHA created successfully";
    private static final String SUB = "sub";
    @Autowired
    TransactionService transactionService;

    @Autowired
    AccountService accountService;

    @Autowired
    HidPhrAddressService hidPhrAddressService;

    @Autowired
    AccountAuthMethodService accountAuthMethodService;

    @Autowired
    AbhaAddressGenerator abhaAddressGenerator;

    @Autowired
    DocumentAppService documentAppService;

    @Autowired
    IdentityDocumentDBService identityDocumentDBService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    EnrolmentCipher enrolmentCipher;
    @Autowired
    JWTUtil jwtUtil;
    @Autowired
    LgdUtility lgdUtility;

    @Autowired
    DeDuplicationService deDuplicationService;
    @Value(PropertyConstants.ENROLLMENT_IS_TRANSACTION_DL)
    private boolean isDLTransactionManagementEnable;

    public Mono<EnrolByDocumentResponseDto> verifyAndCreateAccount(EnrolByDocumentRequestDto enrolByDocumentRequestDto, RequestHeaders requestHeaders) {
        enrolByDocumentRequestDto.setDocumentId(GeneralUtils.removeSpecialChar(enrolByDocumentRequestDto.getDocumentId()));
        return transactionService.findTransactionDetailsFromDB(enrolByDocumentRequestDto.getTxnId())
                .flatMap(txnDto -> {
                    log.info(TRANSACTION + txnDto.getTxnId() + FOUND);
                    if (txnDto.isMobileVerified()) {
                        log.info(MOBILE_NUMBER_IS_VERIFIED);
                        return accountService.getAccountByDocumentCode(GeneralUtils.documentChecksum(enrolByDocumentRequestDto.getDocumentType(), enrolByDocumentRequestDto.getDocumentId()))
                                .flatMap(accountDto -> {
                                    log.info(FOUND_ACCOUNT + accountDto.getHealthIdNumber() + WITH_SAME_DL + enrolByDocumentRequestDto.getDocumentId() + CLOSING);
                                    return transactionService.deleteTransactionEntity(enrolByDocumentRequestDto.getTxnId()).flatMap(responseEntity -> {
                                        if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                                            log.warn(FAILED_TO_DELETE_TRANSACTION + enrolByDocumentRequestDto.getTxnId());
                                        } else {
                                            log.info(TRANSACTION_DELETED);
                                        }
                                        if (accountDto.getStatus().equals(AccountStatus.DELETED.getValue())) {
                                            log.info(ACCOUNT_NOT_FOUND_WITH_DL_VERIFYING_DL_DETAILS);
                                            return verifyDrivingLicence(enrolByDocumentRequestDto, txnDto, requestHeaders);
                                        } else if (accountDto.getStatus().equals(AccountStatus.DEACTIVATED.getValue())) {
                                            return prepareErolByDLResponse(accountDto, txnDto.getTxnId().toString(), false, false, AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST_AND_DEACTIVATED, requestHeaders);
                                        } else if (accountDto.getVerificationStatus().equals(PROVISIONAL)) {
                                            return prepareErolByDLResponse(accountDto, txnDto.getTxnId().toString(), false, false, AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST, requestHeaders);
                                        } else {
                                            //return existing account
                                            return prepareErolByDLResponse(accountDto, txnDto.getTxnId().toString(), false, true, AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST, requestHeaders);
                                        }
                                    });
                                }).switchIfEmpty(Mono.defer(() -> {
                                    //verify DL and create new account
                                    log.info(ACCOUNT_NOT_FOUND_WITH_DL_VERIFYING_DL_DETAILS);
                                    return verifyDrivingLicence(enrolByDocumentRequestDto, txnDto, requestHeaders);
                                }));
                    } else {
                        log.info(MOBILE_NUMBER_NOT_VERIFIED);
                        throw new AbhaUnProcessableException(ABDMError.MOBILE_NUMBER_NOT_VERIFIED);
                    }
                }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    private Mono<EnrolByDocumentResponseDto> verifyDrivingLicence(EnrolByDocumentRequestDto enrolByDocumentRequestDto, TransactionDto txnDto, RequestHeaders requestHeaders) {
        return documentAppService.verify(VerifyDLRequest.builder()
                .documentType(AbhaConstants.DRIVING_LICENCE)
                .documentId(enrolByDocumentRequestDto.getDocumentId())
                .dob(enrolByDocumentRequestDto.getDob())
                .build()).flatMap(verifyDLResponse -> {
            if (verifyDLResponse.getAuthResult().equals(StringConstants.SUCCESS)) {
                //create new account
                log.info(DL_DETAILS_VERIFIED_CREATING_NEW_ENROLLMENT_ACCOUNT);
                return createDLAccount(enrolByDocumentRequestDto, txnDto, requestHeaders);
            } else {
                //failure response
                log.info(DL_DETAILS_NOT_VERIFIED);
                throw new AbhaUnProcessableException(ABDMError.DRIVING_LICENSE_EXCEPTIONS.getCode(), DL_VERIFICATION_FAILED);
            }
        });
    }

    private Mono<EnrolByDocumentResponseDto> createDLAccount(EnrolByDocumentRequestDto enrolByDocumentRequestDto, TransactionDto transactionDto, RequestHeaders requestHeaders) {
        String enrollmentNumber = AbhaNumberGenerator.generateAbhaNumber();
        String subject = requestHeaders.getFTokenClaims() == null ? null:requestHeaders.getFTokenClaims().get(SUB).toString();
        String defaultAbhaAddress = abhaAddressGenerator.generateDefaultAbhaAddress(enrollmentNumber);
        AccountDto accountDto = AccountDto.builder()
                .healthIdNumber(enrollmentNumber)
                .name(StringUtils.isEmpty(enrolByDocumentRequestDto.getMiddleName()) ? Common.getName(enrolByDocumentRequestDto.getFirstName()
                        , enrolByDocumentRequestDto.getLastName()) : Common.getName(enrolByDocumentRequestDto.getFirstName(), enrolByDocumentRequestDto.getMiddleName(), enrolByDocumentRequestDto.getLastName()))
                .verificationStatus(subject != null ? AbhaConstants.VERIFIED : AbhaConstants.PROVISIONAL)
                .verificationType(AbhaConstants.DRIVING_LICENCE)
                .firstName(enrolByDocumentRequestDto.getFirstName())
                .middleName(enrolByDocumentRequestDto.getMiddleName())
                .lastName(enrolByDocumentRequestDto.getLastName())
                .dayOfBirth(Common.getDayOfBirth(enrolByDocumentRequestDto.getDob()))
                .monthOfBirth(Common.getMonthOfBirth(enrolByDocumentRequestDto.getDob()))
                .yearOfBirth(Common.getYearOfBirth(enrolByDocumentRequestDto.getDob()))
                .gender(enrolByDocumentRequestDto.getGender())
                .mobile(transactionDto.getMobile())
                .address(enrolByDocumentRequestDto.getAddress())
                .districtName(enrolByDocumentRequestDto.getDistrict())
                .stateName(enrolByDocumentRequestDto.getState())
                .type(AbhaType.STANDARD)
                .pincode(enrolByDocumentRequestDto.getPinCode())
                .kycVerified(subject != null)
                .status(AccountStatus.ACTIVE.getValue())
                .consentVersion(enrolByDocumentRequestDto.getConsent().getVersion())
                .consentDate(LocalDateTime.now())
                .documentCode(GeneralUtils.documentChecksum(enrolByDocumentRequestDto.getDocumentType(), enrolByDocumentRequestDto.getDocumentId()))
                .healthId(defaultAbhaAddress)
                .build();

        return deDuplicationService.checkDeDuplication(deDuplicationService.prepareRequest(accountDto))
                .flatMap(duplicateAccount ->
                    prepareErolByDLResponse(duplicateAccount, transactionDto.getTxnId().toString(), false, false, AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST, requestHeaders)
                ).switchIfEmpty(Mono.defer(() ->
                    lgdUtility.getLgdData(enrolByDocumentRequestDto.getPinCode(), enrolByDocumentRequestDto.getState())
                            .flatMap(lgdDistrictResponses -> {
                                if (!lgdDistrictResponses.isEmpty()) {
                                    LgdDistrictResponse lgdDistrictResponse = Common.getLGDDetails(lgdDistrictResponses);
                                    accountDto.setDistrictCode(lgdDistrictResponse.getDistrictCode());
                                    accountDto.setDistrictName(lgdDistrictResponse.getDistrictName() == null || lgdDistrictResponse.getDistrictName().equalsIgnoreCase("Unknown") ? enrolByDocumentRequestDto.getDistrict() : lgdDistrictResponse.getDistrictName());
                                    accountDto.setStateCode(lgdDistrictResponse.getStateCode());
                                    accountDto.setStateName(lgdDistrictResponse.getStateName());
                                } else {
                                    accountDto.setDistrictName(enrolByDocumentRequestDto.getDistrict());
                                    accountDto.setStateName(enrolByDocumentRequestDto.getState());
                                }
                                accountDto.setCreatedDate(LocalDateTime.now());
                                accountDto.setKycdob(Common.getDob(accountDto.getDayOfBirth(), accountDto.getMonthOfBirth(), accountDto.getYearOfBirth()));
                                if(isDLTransactionManagementEnable){
                                    return callProcedureToCreateDLAccount(accountDto,enrolByDocumentRequestDto,transactionDto,requestHeaders);
                                }else{
                                    return accountService.createAccountEntity(null,accountDto,requestHeaders).flatMap(accountDtoResponse -> {
                                        log.info(NEW_ENROLLMENT_ACCOUNT_CREATED_AND_UPDATED_IN_DB);
                                        HidPhrAddressDto hidPhrAddressDto = hidPhrAddressService.prepareNewHidPhrAddress(accountDto);
                                        return hidPhrAddressService.createHidPhrAddressEntity(hidPhrAddressDto).flatMap(phrAddressDto -> {
                                            if (phrAddressDto != null) {
                                                log.info(DEFAULT_PHR_ADDRESS_UPDATED_IN_HID_PHR_ADDRESS_TABLE);
                                                return addDocumentsInIdentityDocumentEntity(accountDtoResponse, enrolByDocumentRequestDto).flatMap(idDocumentResponse -> {
                                                    if (idDocumentResponse != null) {
                                                        log.info(DL_DOCUMENTS_STORED_IN_ADV_DB);
                                                        return accountAuthMethodService.addAccountAuthMethods(Collections.singletonList(new AccountAuthMethodsDto(accountDtoResponse.getHealthIdNumber(), AccountAuthMethods.MOBILE_OTP.getValue()))).flatMap(res -> {
                                                            if (!res.isEmpty()) {
                                                                log.info(ACCOUNT_AUTH_METHODS_ADDED);
                                                                return transactionService.deleteTransactionEntity(transactionDto.getTxnId().toString()).flatMap(responseEntity -> {
                                                                    if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                                                                        log.warn(FAILED_TO_DELETE_TRANSACTION + transactionDto.getTxnId().toString());
                                                                    } else {
                                                                        log.info(TRANSACTION_DELETED);
                                                                    }
                                                                    return sendSuccessNotificationAndPrepareDLResponse(accountDto, transactionDto.getTxnId().toString(), requestHeaders);
                                                                });
                                                            } else {
                                                                throw new AbhaDBGatewayUnavailableException();
                                                            }
                                                        });
                                                    } else {
                                                        throw new DocumentGatewayUnavailableException();
                                                    }
                                                });
                                            } else {
                                                throw new AbhaDBGatewayUnavailableException();
                                            }
                                        });
                                    });
                                }

                            })
                ));
    }

    private Mono<IdentityDocumentsDto> addDocumentsInIdentityDocumentEntity(AccountDto accountDto, EnrolByDocumentRequestDto enrolByDocumentRequestDto) {

        IdentityDocumentsDto identityDocumentsDto = new IdentityDocumentsDto();
        identityDocumentsDto.setDocumentNumber(enrolmentCipher.encrypt(enrolByDocumentRequestDto.getDocumentId()));
        identityDocumentsDto.setDocumentType(AbhaConstants.DRIVING_LICENCE);
        identityDocumentsDto.setDob(enrolByDocumentRequestDto.getDob());
        identityDocumentsDto.setGender(accountDto.getGender());
        identityDocumentsDto.setFirstName(accountDto.getFirstName());
        identityDocumentsDto.setLastName(accountDto.getLastName());
        identityDocumentsDto.setMiddleName(accountDto.getMiddleName());
        identityDocumentsDto.setStatus(accountDto.getStatus());
        identityDocumentsDto.setHealthIdNumber(accountDto.getHealthIdNumber());
        identityDocumentsDto.setPhoto(enrolByDocumentRequestDto.getFrontSidePhoto());
        identityDocumentsDto.setPhotoBack(enrolByDocumentRequestDto.getBackSidePhoto());
        identityDocumentsDto.setVerificationStatus(accountDto.getVerificationStatus());
        identityDocumentsDto.setVerificationType(AbhaConstants.DRIVING_LICENCE);

        return identityDocumentDBService.addIdentityDocuments(identityDocumentsDto);
    }

    private Mono<EnrolByDocumentResponseDto> prepareErolByDLResponse(AccountDto accountDto, String txnId, boolean isNewAccount, boolean generateToken, String responseMessage, RequestHeaders requestHeaders) {

        boolean isFacilityRequest = requestHeaders.getFTokenClaims() != null;
        EnrolProfileDto enrolProfileDto = EnrolProfileDto.builder()
                .enrolmentNumber(generateToken?null:accountDto.getHealthIdNumber())
                .abhaNumber(generateToken?accountDto.getHealthIdNumber():null)
                .enrolmentState(accountDto.getVerificationStatus())
                .firstName(accountDto.getFirstName())
                .middleName(accountDto.getMiddleName())
                .lastName(accountDto.getLastName())
                .dob(accountDto.getYearOfBirth() + StringConstants.DASH
                        + accountDto.getMonthOfBirth() + StringConstants.DASH
                        + accountDto.getDayOfBirth())
                .gender(accountDto.getGender())
                .mobile(accountDto.getMobile())
                .email(accountDto.getEmail())
                .address(accountDto.getAddress())
                .districtCode(accountDto.getDistrictCode())
                .stateCode(accountDto.getStateCode())
                .abhaType(accountDto.getType() == null ? null : StringUtils.upperCase(accountDto.getType().getValue()))
                .pinCode(accountDto.getPincode())
                .state(accountDto.getStateName())
                .district(accountDto.getDistrictName())
                .phrAddress(Collections.singletonList(accountDto.getHealthId()))
                .abhaStatus(StringUtils.upperCase(accountDto.getStatus()))
                .build();

        if (isFacilityRequest) {
            EnrollmentResponse enrolmentResponse = EnrollmentResponse.builder()
                    .status(ENROL_VERIFICATION_STATUS)
                    .token(generateToken?jwtUtil.generateToken(txnId, accountDto):null)
                    .expiresIn(generateToken?jwtUtil.jwtTokenExpiryTime():null)
                    .refreshToken(generateToken?jwtUtil.generateRefreshToken(accountDto.getHealthIdNumber()):null)
                    .refreshExpiresIn(generateToken?jwtUtil.jwtRefreshTokenExpiryTime():null)
                    .build();
            EnrolByDocumentResponseDto enrolByDocumentResponseDto = EnrolByDocumentResponseDto.builder()
                    .enrolProfileDto(enrolProfileDto)
                    .enrolmentResponse(enrolmentResponse)
                    .message(responseMessage)
                    .isNew(isNewAccount)
                    .build();
            return Mono.just(enrolByDocumentResponseDto);
        } else if (!isNewAccount && accountDto.getVerificationStatus().equalsIgnoreCase(VERIFIED)) {
            EnrolByDocumentResponseDto enrolByDocumentResponseDto = EnrolByDocumentResponseDto.builder()
                    .enrolProfileDto(enrolProfileDto)
                    .message(responseMessage)
                    .isNew(isNewAccount)
                    .build();
            if (generateToken) {
                ResponseTokensDto responseTokensDto = ResponseTokensDto.builder()
                        .token(jwtUtil.generateToken(txnId, accountDto))
                        .expiresIn(jwtUtil.jwtTokenExpiryTime())
                        .refreshToken(jwtUtil.generateRefreshToken(accountDto.getHealthIdNumber()))
                        .refreshExpiresIn(jwtUtil.jwtRefreshTokenExpiryTime())
                        .build();
                enrolByDocumentResponseDto.setResponseTokensDto(responseTokensDto);
            }
            return Mono.just(enrolByDocumentResponseDto);
        }
        EnrolByDocumentResponseDto enrolByDocumentResponseDto = EnrolByDocumentResponseDto.builder()
                .message(responseMessage)
                .enrolProfileDto(enrolProfileDto)
                .isNew(isNewAccount)
                .build();
        return Mono.just(enrolByDocumentResponseDto);
    }

    private Mono<EnrolByDocumentResponseDto> sendSuccessNotificationAndPrepareDLResponse(AccountDto accountDto, String txnId, RequestHeaders requestHeaders) {
        boolean isFacilityRequest = requestHeaders.getFTokenClaims() != null;
        if (isFacilityRequest) {
            return notificationService.sendABHACreationSMS(accountDto.getMobile(), accountDto.getName(), accountDto.getHealthIdNumber())
                    .flatMap(notificationResponseDto -> getEnrolByDocumentResponseDtoMono(accountDto, txnId, requestHeaders, notificationResponseDto));

        } else
        {
            return notificationService.sendEnrollCreationSMS(accountDto.getMobile(), accountDto.getName(), accountDto.getHealthIdNumber())
                    .flatMap(notificationResponseDto -> getEnrolByDocumentResponseDtoMono(accountDto, txnId, requestHeaders, notificationResponseDto));
    }
    }

    private Mono<EnrolByDocumentResponseDto> getEnrolByDocumentResponseDtoMono(AccountDto accountDto, String txnId, RequestHeaders requestHeaders, NotificationResponseDto notificationResponseDto) {
        if (notificationResponseDto.getStatus().equals(AbhaConstants.SENT)) {
            log.info(NOTIFICATION_SENT_ON_ACCOUNT_CREATION + ON_MOBILE_NUMBER + accountDto.getMobile() + FOR_HEALTH_ID_NUMBER + accountDto.getHealthIdNumber());
            return prepareErolByDLResponse(accountDto, txnId, true, true, AbhaConstants.ACCOUNT_CREATED_SUCCESSFULLY, requestHeaders);
        } else {
            throw new NotificationGatewayUnavailableException();
        }
    }

    private Mono<EnrolByDocumentResponseDto> callProcedureToCreateDLAccount(AccountDto accountDto, EnrolByDocumentRequestDto enrolByDocumentRequestDto, TransactionDto transactionDto, RequestHeaders requestHeaders) {
        List<AccountDto> accountList = new ArrayList<>();
        List<HidPhrAddressDto> hidPhrAddressDtoList = new ArrayList<>();
        accountList.add(accountDto);
        HidPhrAddressDto hidPhrAddressDto = hidPhrAddressService.prepareNewHidPhrAddress(accountDto);
        hidPhrAddressDtoList.add(hidPhrAddressDto);
        if (!accountDto.getHealthIdNumber().isEmpty()) {
            List<AccountAuthMethodsDto> accountAuthMethodsDtos = new ArrayList<>();
            if (accountDto.getMobile() != null) {
                accountAuthMethodsDtos.add(new AccountAuthMethodsDto(accountDto.getHealthIdNumber(), AccountAuthMethods.MOBILE_OTP.getValue()));
            }
           return addDocumentsInIdentityDocumentEntity(accountDto, enrolByDocumentRequestDto)
                    .flatMap(idDocumentResponse -> {
                        if (idDocumentResponse != null) {
                            log.info("going to call procedure to create account via DL");
                           return accountService.saveAllData(SaveAllDataRequest.builder().accounts(accountList).hidPhrAddress(hidPhrAddressDtoList).accountAuthMethods(accountAuthMethodsDtos).build())
                                    .flatMap(v-> sendSuccessNotificationAndPrepareDLResponse(accountDto, transactionDto.getTxnId().toString(), requestHeaders));
                        }else{
                            throw new DocumentGatewayUnavailableException();
                        }
                    });
        } else {
            throw new AbhaDBGatewayUnavailableException();
        }
    }
}
