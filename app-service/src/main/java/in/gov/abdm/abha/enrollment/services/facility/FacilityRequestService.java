package in.gov.abdm.abha.enrollment.services.facility;

import com.password4j.BadParametersException;
import in.gov.abdm.abha.enrollment.client.AadhaarClient;
import in.gov.abdm.abha.enrollment.client.DocumentClient;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.EnrollErrorConstants;
import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.enums.AccountAuthMethods;
import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarErrorCodes;
import in.gov.abdm.abha.enrollment.exception.abha_db.EnrolmentIdNotFoundException;
import in.gov.abdm.abha.enrollment.exception.abha_db.HealthIdNumberNotFoundException;
import in.gov.abdm.abha.enrollment.exception.abha_db.TransactionNotFoundException;
import in.gov.abdm.abha.enrollment.exception.application.AbhaUnProcessableException;
import in.gov.abdm.abha.enrollment.exception.application.UnauthorizedUserToSendOrVerifyOtpException;
import in.gov.abdm.abha.enrollment.exception.notification.FailedToSendNotificationException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AccountResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AuthResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolProfileDto;
import in.gov.abdm.abha.enrollment.model.enrol.facility.EnrollmentResponse;
import in.gov.abdm.abha.enrollment.model.enrol.facility.EnrollmentStatusUpdate;
import in.gov.abdm.abha.enrollment.model.entities.*;
import in.gov.abdm.abha.enrollment.model.notification.NotificationResponseDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpResponseDto;
import in.gov.abdm.abha.enrollment.model.redis.otp.ReceiverOtpTracker;
import in.gov.abdm.abha.enrollment.model.redis.otp.RedisOtp;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.account_auth_methods.AccountAuthMethodService;
import in.gov.abdm.abha.enrollment.services.database.accountaction.AccountActionService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.idp.IdpService;
import in.gov.abdm.abha.enrollment.services.notification.NotificationService;
import in.gov.abdm.abha.enrollment.services.notification.TemplatesHelper;
import in.gov.abdm.abha.enrollment.services.redis.RedisService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import in.gov.abdm.abha.enrollment.utilities.argon2.Argon2Util;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import in.gov.abdm.error.ABDMError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static in.gov.abdm.abha.enrollment.enums.AccountStatus.*;
import static java.time.LocalDateTime.now;

/**
 * service for OTP Request coming from ui
 * otp can be sent via aadhaar / abdm
 */
@Service
@Slf4j
public class FacilityRequestService {


    private static final String MOBILE_NUMBER_IS_VERIFIED = "Mobile Number is Verified";
    private static final String MOBILE_NUMBER_NOT_VERIFIED = "Mobile Number Not Verified";
    private static final String ENROL_VERIFICATION_STATUS = "success";
    private static final String ACCEPT = "ACCEPT";
    private static final String VERIFIED = "VERIFIED";
    private static final String REJECTED = "REJECTED";
    private static final String REACTIVATION = "REACTIVATION";
    private static final String DEACTIVATION = "DEACTIVATION";
    private static final String STATUS = "status";
    private static final String ENROL_VERIFICATION_ACCEPT_MESSAGE = "Successfully verified";
    private static final String ENROL_VERIFICATION_REJECT_MESSAGE = "Successfully rejected";
    private static final String OTP_IS_SENT_TO_MOBILE_ENDING = "OTP is sent to Mobile number ending with ";
    private static final String OTP_SUBJECT = "mobile verification";
    private static final String SENT = "sent";
    private static final String FAILED_TO_SEND_OTP_FOR_MOBILE_VERIFICATION = "Failed to Send OTP for Mobile verification";
    private static final String EMAIL_LINKED_SUCCESSFULLY = "Email linked successfully";
    private static final String FOUND = " found.";
    private static final String TRANSACTION = " Transaction.";
    private RedisOtp redisOtp;

    private static final String OTP_VALUE_DID_NOT_MATCH_PLEASE_TRY_AGAIN = "OTP value did not match, please try again.";
    private static final int OTP_EXPIRE_TIME = 10;
    private static final String OTP_EXPIRED_RESEND_OTP_AND_RETRY = "OTP expired, please try again.";
    private static final String MOBILE_NUMBER_LINKED_SUCCESSFULLY = "Mobile Number linked successfully";
    public static final String FAILED_TO_VALIDATE_OTP_PLEASE_TRY_AGAIN = "Failed to Validate OTP, please Try again.";


    @Autowired
    TransactionService transactionService;
    @Autowired
    AadhaarClient aadhaarClient;
    @Autowired
    NotificationService notificationService;
    @Autowired
    RSAUtil rsaUtil;
    @Autowired
    TemplatesHelper templatesHelper;
    @Autowired
    IdpService idpService;
    @Autowired
    RedisService redisService;
    @Autowired
    AccountService accountService;
    @Autowired
    AccountActionService accountActionService;
    @Autowired
    DocumentClient documentClient;
    @Autowired
    private AccountAuthMethodService accountAuthMethodService;


    public Mono<MobileOrEmailOtpResponseDto> sendOtpForEnrollmentNumberService(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto) {
        String enrolmentNumber = rsaUtil.decrypt(mobileOrEmailOtpRequestDto.getLoginId());
        String newOtp = GeneralUtils.generateRandomOTP();
        Mono<AccountDto> accountDtoMono = accountService.getAccountByHealthIdNumber(enrolmentNumber);

        return accountDtoMono.flatMap(accountDto -> {
            if (!redisService.isResendOtpAllowed(accountDto.getMobile())) {
                throw new UnauthorizedUserToSendOrVerifyOtpException(AadhaarErrorCodes.E_952.getValue(), EnrollErrorConstants.RESEND_OR_REMATCH_OTP_EXCEPTION);
            }
            Mono<NotificationResponseDto> notificationResponseDtoMono = notificationService.sendSMSOtp(accountDto.getMobile(), OTP_SUBJECT, templatesHelper.prepareUpdateMobileMessage(newOtp));

            return notificationResponseDtoMono.flatMap(response -> {
                if (response.getStatus().equals(SENT)) {
                    TransactionDto transactionDto = new TransactionDto();
                    transactionDto.setMobile(accountDto.getMobile());
                    transactionDto.setOtp(Argon2Util.encode(newOtp));
                    transactionDto.setOtpRetryCount(transactionDto.getOtpRetryCount() + 1);
                    transactionDto.setCreatedDate(LocalDateTime.now());
                    transactionDto.setHealthIdNumber(accountDto.getHealthIdNumber());
                    transactionDto.setTxnId(UUID.randomUUID());
                    transactionDto.setKycPhoto(StringConstants.EMPTY);
                    return transactionService.createTransactionEntity(transactionDto).flatMap(res -> {
                        handleNewOtpRedisObjectCreation(res.getTxnId().toString(), accountDto.getMobile(), StringUtils.EMPTY, Argon2Util.encode(newOtp));
                        return Mono.just(MobileOrEmailOtpResponseDto.builder().txnId(res.getTxnId().toString()).message(OTP_IS_SENT_TO_MOBILE_ENDING + Common.hidePhoneNumber(accountDto.getMobile())).build());
                    });
                } else {
                    throw new FailedToSendNotificationException(FAILED_TO_SEND_OTP_FOR_MOBILE_VERIFICATION);
                }
            });
        }).switchIfEmpty(Mono.error(new EnrolmentIdNotFoundException(AbhaConstants.ENROLMENT_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    public Mono<EnrolByDocumentResponseDto> fetchDetailsByEnrollmentNumber(String enrollmentNumber) {
        Mono<AccountDto> accountDtoMono = accountService.getAccountByHealthIdNumber(enrollmentNumber);
        return accountDtoMono.flatMap(accountDto -> {
            if (accountDto.getVerificationStatus() != null && accountDto.getVerificationStatus().equalsIgnoreCase(AbhaConstants.PROVISIONAL)) {
                Mono<IdentityDocumentsDto> response = documentClient.getIdentityDocuments(accountDto.getHealthIdNumber());
                return response.flatMap(res -> {
                    EnrolProfileDto enrolProfileDto = EnrolProfileDto.builder().enrolmentNumber(accountDto.getHealthIdNumber()).enrolmentState(accountDto.getVerificationStatus()).firstName(accountDto.getFirstName()).middleName(accountDto.getMiddleName()).lastName(accountDto.getLastName()).dob(accountDto.getYearOfBirth() + StringConstants.DASH + accountDto.getMonthOfBirth() + StringConstants.DASH + accountDto.getDayOfBirth()).gender(accountDto.getGender()).photo(accountDto.getProfilePhoto()).photoFront(res.getPhoto()).photoBack(res.getPhotoBack()).mobile(accountDto.getMobile()).email(accountDto.getEmail()).address(accountDto.getAddress()).districtCode(accountDto.getDistrictCode()).stateCode(accountDto.getStateCode()).abhaType(accountDto.getType() == null ? null : StringUtils.upperCase(accountDto.getType().getValue())).pinCode(accountDto.getPincode()).state(accountDto.getStateName()).district(accountDto.getDistrictName()).phrAddress(Collections.singletonList(accountDto.getHealthId())).abhaStatus(StringUtils.upperCase(accountDto.getStatus())).build();
                    return Mono.just(new EnrolByDocumentResponseDto(enrolProfileDto));
                }).switchIfEmpty(Mono.error(new HealthIdNumberNotFoundException(AbhaConstants.HEALTH_ID_NUMBER_NOT_FOUND_EXCEPTION_MESSAGE)));
            } else{
                return Mono.error(new HealthIdNumberNotFoundException(AbhaConstants.VERIFICATION_STATUS_NOT_PROVISIONAL));
            }
        }).switchIfEmpty(Mono.error(new EnrolmentIdNotFoundException(AbhaConstants.ENROLMENT_NOT_FOUND_EXCEPTION_MESSAGE)));

    }

    private void handleNewOtpRedisObjectCreation(String txnId, String receiver, String aadhaarTxn, String otpValue) {
        RedisOtp redisOtp = RedisOtp.builder().txnId(txnId).otpValue(otpValue).aadhaarTxnId(aadhaarTxn).receiver(receiver).build();

        ReceiverOtpTracker receiverOtpTracker = redisService.getReceiverOtpTracker(receiver);

        if (receiverOtpTracker != null) {
            receiverOtpTracker.setSentOtpCount(receiverOtpTracker.getSentOtpCount() + 1);
            receiverOtpTracker.setVerifyOtpCount(0);
        } else {
            receiverOtpTracker = new ReceiverOtpTracker();
            receiverOtpTracker.setReceiver(receiver);
            receiverOtpTracker.setSentOtpCount(1);
            receiverOtpTracker.setVerifyOtpCount(0);
            receiverOtpTracker.setBlocked(false);
        }
        redisService.saveReceiverOtpTracker(receiver, receiverOtpTracker);
        redisService.saveRedisOtp(txnId, redisOtp);
    }

    public Mono<AuthResponseDto> verifyOtpViaNotification(AuthRequestDto authByAbdmRequest, boolean isMobile) {
        redisOtp = redisService.getRedisOtp(authByAbdmRequest.getAuthData().getOtp().getTxnId());

        Mono<AuthResponseDto> redisResponse = handleRedisABDMOtpVerification(authByAbdmRequest);
        if (redisResponse != null) {
            return redisResponse;
        }

        return transactionService.findTransactionDetailsFromDB(authByAbdmRequest.getAuthData().getOtp().getTxnId()).flatMap(transactionDto -> verifyOtpViaNotification(authByAbdmRequest.getAuthData().getOtp().getOtpValue(), transactionDto, isMobile)).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }


    private Mono<AuthResponseDto> handleRedisABDMOtpVerification(AuthRequestDto authByAbdmRequest) {
        redisOtp = redisService.getRedisOtp(authByAbdmRequest.getAuthData().getOtp().getTxnId());
        if (redisOtp == null) {
            throw new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE);
        } else {
            if (!redisService.isMultipleOtpVerificationAllowed(redisOtp.getReceiver())) {
                throw new UnauthorizedUserToSendOrVerifyOtpException(AadhaarErrorCodes.E_952.getValue(), EnrollErrorConstants.RESEND_OR_REMATCH_OTP_EXCEPTION);
            }
            if (!Argon2Util.verify(redisOtp.getOtpValue(), authByAbdmRequest.getAuthData().getOtp().getOtpValue())) {
                ReceiverOtpTracker receiverOtpTracker = redisService.getReceiverOtpTracker(redisOtp.getReceiver());
                receiverOtpTracker.setVerifyOtpCount(receiverOtpTracker.getVerifyOtpCount() + 1);
                redisService.saveReceiverOtpTracker(redisOtp.getReceiver(), receiverOtpTracker);

                TransactionDto transactionDto = new TransactionDto();
                transactionDto.setTxnId(UUID.fromString(authByAbdmRequest.getAuthData().getOtp().getTxnId()));
                return prepareAuthByAdbmResponse(transactionDto, false, OTP_VALUE_DID_NOT_MATCH_PLEASE_TRY_AGAIN);
            }
        }
        return null;
    }


    private Mono<AuthResponseDto> prepareAuthByAdbmResponse(TransactionDto transactionDto, boolean status, String message) {

        AccountResponseDto accountResponseDto = null;

        if (status) {
            accountResponseDto = AccountResponseDto.builder().ABHANumber(transactionDto.getHealthIdNumber()).name(transactionDto.getName()).build();
        }

        return Mono.just(AuthResponseDto.builder().txnId(transactionDto.getTxnId().toString()).authResult(status ? StringConstants.SUCCESS : StringConstants.FAILED).message(message).accounts(status && StringUtils.isNoneEmpty(accountResponseDto.getABHANumber()) ? Collections.singletonList(accountResponseDto) : null).build());
    }


    private Mono<AuthResponseDto> verifyOtpViaNotification(String otp, TransactionDto transactionDto, boolean isMobile) {
        try {
            if (GeneralUtils.isOtpExpired(transactionDto.getCreatedDate(), OTP_EXPIRE_TIME)) {
                return prepareAuthByAdbmResponse(transactionDto, false, OTP_EXPIRED_RESEND_OTP_AND_RETRY);
            } else if (Argon2Util.verify(transactionDto.getOtp(), otp)) {
                return accountService.getAccountByHealthIdNumber(transactionDto.getHealthIdNumber()).flatMap(accountDto -> {
                    if (isMobile) {
                        return updatePhoneNumberInAccountEntity(accountDto, transactionDto);
                    } else {
                        return updateEmailInAccountEntity(accountDto, transactionDto);
                    }
                });
            } else {
                return prepareAuthByAdbmResponse(transactionDto, false, OTP_VALUE_DID_NOT_MATCH_PLEASE_TRY_AGAIN);
            }
        } catch (BadParametersException ex) {
            return prepareAuthByAdbmResponse(transactionDto, false, FAILED_TO_VALIDATE_OTP_PLEASE_TRY_AGAIN);
        }
    }


    private Mono<AuthResponseDto> updatePhoneNumberInAccountEntity(AccountDto accountDto, TransactionDto transactionDto) {

        transactionDto.setMobileVerified(Boolean.TRUE);
        accountDto.setMobile(transactionDto.getMobile());
        accountDto.setUpdateDate(now());
        redisService.deleteRedisOtp(transactionDto.getTxnId().toString());
        redisService.deleteReceiverOtpTracker(redisOtp.getReceiver());
        return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getId())).flatMap(transactionDto1 -> accountService.updateAccountByHealthIdNumber(accountDto, accountDto.getHealthIdNumber())).flatMap(accountDto1 -> updateAccountAuthMethodsWithMobileOtp(accountDto1.getHealthIdNumber())).flatMap(res -> prepareAuthByAdbmResponse(transactionDto, true, MOBILE_NUMBER_LINKED_SUCCESSFULLY));
    }

    private Mono<List<AccountAuthMethodsDto>> updateAccountAuthMethodsWithMobileOtp(String abhaNumber) {
        return accountAuthMethodService.addAccountAuthMethods(Collections.singletonList(new AccountAuthMethodsDto(abhaNumber, AccountAuthMethods.MOBILE_OTP.getValue())));
    }

    private Mono<? extends AuthResponseDto> updateEmailInAccountEntity(AccountDto accountDto, TransactionDto transactionDto) {

        transactionDto.setEmailVerified(Boolean.TRUE);
        accountDto.setEmail(transactionDto.getEmail());
        accountDto.setEmailVerified(transactionDto.getEmail());
        accountDto.setEmailVerificationDate(now());
        accountDto.setUpdateDate(now());
        redisService.deleteRedisOtp(transactionDto.getTxnId().toString());
        redisService.deleteReceiverOtpTracker(redisOtp.getReceiver());
        return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getId())).flatMap(transactionDto1 -> accountService.updateAccountByHealthIdNumber(accountDto, accountDto.getHealthIdNumber())).flatMap(accountDto1 -> prepareAuthByAdbmResponse(transactionDto, true, EMAIL_LINKED_SUCCESSFULLY));
    }

    public Mono<EnrollmentResponse> verifyFacilityByEnroll(EnrollmentStatusUpdate enrollmentStatusUpdate) {
        String status = enrollmentStatusUpdate.getVerificationStatus().getValue();
        return transactionService.findTransactionDetailsFromDB(enrollmentStatusUpdate.getTxnId()).flatMap(txnDto -> {
            log.info(TRANSACTION + txnDto.getTxnId() + FOUND);
            if (txnDto.isMobileVerified()) {
                log.info(MOBILE_NUMBER_IS_VERIFIED);
                return accountService.getAccountByHealthIdNumber(txnDto.getHealthIdNumber()).flatMap(accountDto -> {
                    EnrollmentResponse enrollmentResponse = new EnrollmentResponse();
                    if (status.equalsIgnoreCase(ACCEPT)) {
                        accountDto.setVerificationStatus(VERIFIED);
                        accountDto.setStatus(ACTIVE.getValue());
                        accountDto.setUpdateDate(LocalDateTime.now());
                        accountService.updateAccountByHealthIdNumber(accountDto, txnDto.getHealthIdNumber()).subscribe();
                        accountActionService.getAccountActionByHealthIdNumber(txnDto.getHealthIdNumber())
                                .flatMap(accountActionDto -> {
                                    String prevValue = accountActionDto.getNewValue();
                                    AccountActionDto newAccountActionDto = new AccountActionDto();
                                    newAccountActionDto.setAction(REACTIVATION);
                                    newAccountActionDto.setField(STATUS);
                                    newAccountActionDto.setCreatedDate(accountActionDto.getCreatedDate());
                                    newAccountActionDto.setHealthIdNumber(txnDto.getHealthIdNumber());
                                    newAccountActionDto.setNewValue(ACTIVE.getValue());
                                    newAccountActionDto.setPreviousValue(prevValue);
                                    newAccountActionDto.setReactivationDate(LocalDateTime.now().toString());
                                    newAccountActionDto.setReason(enrollmentStatusUpdate.getMessage());
                                    accountActionService.createAccountActionEntity(newAccountActionDto).subscribe();
                                    return Mono.empty();
                                }).subscribe();
                        enrollmentResponse = new EnrollmentResponse(ENROL_VERIFICATION_STATUS, ENROL_VERIFICATION_ACCEPT_MESSAGE);
                    } else {
                        formatAccountDto(accountDto);
                        accountService.updateAccountByHealthIdNumber(accountDto, txnDto.getHealthIdNumber()).subscribe();
                        accountActionService.getAccountActionByHealthIdNumber(txnDto.getHealthIdNumber())
                                .flatMap(accountActionDto -> {
                                    String prevValue = accountActionDto.getNewValue();
                                    AccountActionDto newAccountActionDto = new AccountActionDto();
                                    newAccountActionDto.setAction(DEACTIVATION);
                                    newAccountActionDto.setField(STATUS);
                                    newAccountActionDto.setCreatedDate(accountActionDto.getCreatedDate());
                                    newAccountActionDto.setHealthIdNumber(txnDto.getHealthIdNumber());
                                    newAccountActionDto.setNewValue(DEACTIVATED.getValue());
                                    newAccountActionDto.setPreviousValue(prevValue);
                                    newAccountActionDto.setReactivationDate(LocalDateTime.now().toString());
                                    newAccountActionDto.setReason(enrollmentStatusUpdate.getMessage());
                                    accountActionService.createAccountActionEntity(newAccountActionDto).subscribe();
                                    return Mono.empty();
                                }).subscribe();
                        enrollmentResponse = new EnrollmentResponse(ENROL_VERIFICATION_STATUS, ENROL_VERIFICATION_REJECT_MESSAGE);
                    }
                    return Mono.just(enrollmentResponse);
                });
            } else {
                log.info(MOBILE_NUMBER_NOT_VERIFIED);
                throw new AbhaUnProcessableException(ABDMError.MOBILE_NUMBER_NOT_VERIFIED.getCode(), ABDMError.MOBILE_NUMBER_NOT_VERIFIED.getMessage());
            }
        }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    private AccountDto formatAccountDto(AccountDto accountDto) {
        accountDto.setStatus(REJECTED);
        accountDto.setStatus(DELETED.toString());
        accountDto.setUpdateDate(LocalDateTime.now());
        accountDto.setAddress(null);
        accountDto.setDayOfBirth(null);
        accountDto.setDistrictCode(null);
        accountDto.setDistrictName(null);
        accountDto.setEmail(null);
        accountDto.setFacilityId(null);
        accountDto.setFirstName(null);
        accountDto.setGender("XXX");
        accountDto.setHealthId(null);
        accountDto.setKycdob(null);
        accountDto.setKycVerified(false);
        accountDto.setLastName(null);
        accountDto.setMiddleName(null);
        accountDto.setMonthOfBirth(null);
        accountDto.setOkycVerified(false);
        accountDto.setPassword(null);
        accountDto.setPincode(null);
        accountDto.setStateCode(null);
        accountDto.setStateName(null);
        accountDto.setSubDistrictCode(null);
        accountDto.setSubDistrictName(null);
        accountDto.setTownCode(null);
        accountDto.setTownName(null);
        accountDto.setVillageCode(null);
        accountDto.setVillageName(null);
        accountDto.setWardCode(null);
        accountDto.setWardName(null);
        accountDto.setHipId(null);
        accountDto.setXmlUID(null);
        accountDto.setConsentDate(null);
        accountDto.setEmailVerificationDate(null);
        accountDto.setEmailVerified(null);
        accountDto.setVerificationStatus(null);
        accountDto.setVerificationType(null);
        accountDto.setDocumentCode(null);
        accountDto.setConsentVersion(null);
        accountDto.setCmMigrated(null);
        accountDto.setPhrMigrated(null);
        accountDto.setHealthWorkerMobile(null);
        accountDto.setHealthWorkerName(null);
        accountDto.setMobileType(null);
        accountDto.setKycPhoto(null);
        accountDto.setProfilePhoto(null);
        accountDto.setType(null);
        return accountDto;
    }
}
