package in.gov.abdm.abha.enrollment.services.facility;

import com.password4j.BadParametersException;
import in.gov.abdm.abha.enrollment.client.DocumentDBIdentityDocumentFClient;
import in.gov.abdm.abha.enrollment.configuration.FacilityContextHolder;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.exception.abha_db.EnrolmentIdNotFoundException;
import in.gov.abdm.abha.enrollment.exception.abha_db.TransactionNotFoundException;
import in.gov.abdm.abha.enrollment.exception.application.AbhaUnProcessableException;
import in.gov.abdm.abha.enrollment.exception.application.UnauthorizedUserToSendOrVerifyOtpException;
import in.gov.abdm.abha.enrollment.exception.notification.NotificationGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AccountResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AuthResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.facility.EnrollmentResponse;
import in.gov.abdm.abha.enrollment.model.enrol.facility.EnrollmentStatusUpdate;
import in.gov.abdm.abha.enrollment.model.entities.AccountActionDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.IdentityDocumentsDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.facility.document.EnrolProfileDetailsDto;
import in.gov.abdm.abha.enrollment.model.facility.document.GetByDocumentResponseDto;
import in.gov.abdm.abha.enrollment.model.notification.NotificationResponseDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpResponseDto;
import in.gov.abdm.abha.enrollment.model.redis.otp.ReceiverOtpTracker;
import in.gov.abdm.abha.enrollment.model.redis.otp.RedisOtp;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.account_auth_methods.AccountAuthMethodService;
import in.gov.abdm.abha.enrollment.services.database.accountaction.AccountActionService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.idp.IdpService;
import in.gov.abdm.abha.enrollment.services.notification.NotificationService;
import in.gov.abdm.abha.enrollment.services.notification.TemplatesHelper;
import in.gov.abdm.abha.enrollment.services.redis.RedisService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.EnrolmentCipher;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import in.gov.abdm.abha.enrollment.utilities.argon2.Argon2Util;
import in.gov.abdm.abha.enrollment.utilities.jwt.JWTUtil;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import in.gov.abdm.error.ABDMError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.PROVISIONAL;
import static in.gov.abdm.abha.enrollment.enums.AccountStatus.ACTIVE;
import static in.gov.abdm.abha.enrollment.services.auth.aadhaar.AuthByAadhaarService.OTP_VERIFIED_SUCCESSFULLY;
import static in.gov.abdm.error.ABDMError.INVALID_TRANSACTION_ID;
import static java.time.LocalDateTime.now;

/**
 * service for OTP Request coming from ui
 * otp can be sent via aadhaar / abdm
 */
@Service
@Slf4j
public class FacilityEnrolByEnrollmentNumberService {


    private static final String MOBILE_NUMBER_IS_VERIFIED = "Mobile Number is Verified";
    private static final String MOBILE_NUMBER_NOT_VERIFIED = "Mobile Number Not Verified";
    private static final String ENROL_VERIFICATION_STATUS = "success";
    private static final String ACCEPT = "ACCEPT";
    private static final String VERIFIED = "VERIFIED";
    private static final String REJECTED = "REJECTED";
    private static final String ACTIVATION = "ACTIVATION";
    private static final String DELETION = "DELETION";
    private static final String DELETED = "DELETED";
    private static final String STATUS = "status";
    private static final String ENROL_VERIFICATION_ACCEPT_MESSAGE = "Successfully verified";
    private static final String ENROL_VERIFICATION_REJECT_MESSAGE = "Successfully rejected";
    private static final String OTP_IS_SENT_TO_MOBILE_ENDING = "OTP is sent to Mobile number ending with ";
    private static final String SENT = "sent";
    private static final String FOUND = " found.";
    private static final String TRANSACTION = " Transaction.";
    private RedisOtp redisOtp;

    private static final String OTP_VALUE_DID_NOT_MATCH_PLEASE_TRY_AGAIN = "Entered OTP is incorrect. Kindly re-enter valid OTP.";
    private static final int OTP_EXPIRE_TIME = 10;
    private static final String OTP_EXPIRED_RESEND_OTP_AND_RETRY = "OTP expired, please try again.";
    public static final String FAILED_TO_VALIDATE_OTP_PLEASE_TRY_AGAIN = "Failed to Validate OTP, please Try again.";
    private static final String YEAR_OF_BIRTH = "9999";
    private static final String GENDER = "XXX";


    @Autowired
    TransactionService transactionService;
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
    DocumentDBIdentityDocumentFClient documentClient;
    @Autowired
    private AccountAuthMethodService accountAuthMethodService;
    @Autowired
    EnrolmentCipher enrolmentCipher;
    @Autowired
    HidPhrAddressService hidPhrAddressService;
    @Autowired
    JWTUtil jwtUtil;


    public Mono<MobileOrEmailOtpResponseDto> sendOtpForEnrollmentNumberService(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto) {
        String enrolmentNumber = rsaUtil.decrypt(mobileOrEmailOtpRequestDto.getLoginId());
        String newOtp = GeneralUtils.generateRandomOTP();
        Mono<AccountDto> accountDtoMono = accountService.getAccountByHealthIdNumber(enrolmentNumber);

        return accountDtoMono.flatMap(accountDto -> {
            if (accountDto.getVerificationStatus() == null) {
                throw new EnrolmentIdNotFoundException(AbhaConstants.ENROLLMENT_NOT_FOUND_EXCEPTION_MESSAGE);
            }
            if (!accountDto.getVerificationStatus().equalsIgnoreCase(PROVISIONAL)) {
                throw new EnrolmentIdNotFoundException(AbhaConstants.ENROLLMENT_NOT_FOUND_EXCEPTION_MESSAGE);
            }
            if (!redisService.isResendOtpAllowed(accountDto.getMobile())) {
                throw new UnauthorizedUserToSendOrVerifyOtpException();
            }
            Mono<NotificationResponseDto> notificationResponseDtoMono = notificationService.sendRegistrationOtp(accountDto.getMobile(), newOtp);

            return notificationResponseDtoMono.flatMap(response -> {
                if (response.getStatus().equals(SENT)) {
                    TransactionDto transactionDto = new TransactionDto();
                    transactionDto.setMobile(accountDto.getMobile());
                    transactionDto.setOtp(Argon2Util.encode(newOtp));
                    transactionDto.setOtpRetryCount(transactionDto.getOtpRetryCount() + 1);
                    transactionDto.setCreatedDate(now());
                    transactionDto.setHealthIdNumber(accountDto.getHealthIdNumber());
                    transactionDto.setTxnId(UUID.randomUUID());
                    transactionDto.setKycPhoto(StringConstants.EMPTY);
                    transactionDto.setStateName(accountDto.getStateName());
                    transactionDto.setDistrictName(accountDto.getDistrictName());
                    transactionDto.setPincode(accountDto.getPincode());
                    transactionDto.setDayOfBirth(accountDto.getDayOfBirth());
                    transactionDto.setGender(accountDto.getGender());
                    transactionDto.setEmail(accountDto.getEmail());
                    transactionDto.setName(accountDto.getName());
                    transactionDto.setAddress(accountDto.getAddress());
                    return transactionService.createTransactionEntity(transactionDto).flatMap(res -> {
                        handleNewOtpRedisObjectCreation(res.getTxnId().toString(), accountDto.getMobile(), StringUtils.EMPTY, Argon2Util.encode(newOtp));
                        return Mono.just(MobileOrEmailOtpResponseDto.builder().txnId(res.getTxnId().toString()).message(OTP_IS_SENT_TO_MOBILE_ENDING + Common.hidePhoneNumber(accountDto.getMobile())).build());
                    });
                } else {
                    throw new NotificationGatewayUnavailableException();
                }
            });
        }).switchIfEmpty(Mono.error(new EnrolmentIdNotFoundException(AbhaConstants.ENROLLMENT_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    public Mono<GetByDocumentResponseDto> fetchDetailsByEnrollmentNumber(String enrollmentNumber) {
        Mono<AccountDto> accountDtoMono = accountService.getAccountByHealthIdNumber(enrollmentNumber);
        return accountDtoMono.flatMap(accountDto -> {
            if(!accountDto.getVerificationStatus().equalsIgnoreCase(PROVISIONAL)
                && !accountDto.getStatus().equalsIgnoreCase(ACTIVE.getValue()))
                return Mono.error(new EnrolmentIdNotFoundException(AbhaConstants.ENROLLMENT_NOT_FOUND_EXCEPTION_MESSAGE));
            EnrolProfileDetailsDto enrolProfileDto = EnrolProfileDetailsDto.builder().enrolmentNumber(accountDto.getHealthIdNumber()).enrolmentState(accountDto.getVerificationStatus()).firstName(accountDto.getFirstName()).middleName(accountDto.getMiddleName()).lastName(accountDto.getLastName()).dob(accountDto.getYearOfBirth() + StringConstants.DASH + accountDto.getMonthOfBirth() + StringConstants.DASH + accountDto.getDayOfBirth()).gender(accountDto.getGender()).photo(accountDto.getProfilePhoto()).mobile(accountDto.getMobile()).email(accountDto.getEmail()).address(accountDto.getAddress()).districtCode(accountDto.getDistrictCode()).stateCode(accountDto.getStateCode()).abhaType(accountDto.getType() == null ? null : StringUtils.upperCase(accountDto.getType().getValue())).pinCode(accountDto.getPincode()).state(accountDto.getStateName()).district(accountDto.getDistrictName()).phrAddress(Collections.singletonList(accountDto.getHealthId())).abhaStatus(StringUtils.upperCase(accountDto.getStatus())).build();
            Mono<IdentityDocumentsDto> response = documentClient.getIdentityDocuments(accountDto.getHealthIdNumber());
            return response.flatMap(res -> {
                enrolProfileDto.setPhotoFront(res.getPhoto());
                enrolProfileDto.setDocumentNumber(enrolmentCipher.decrypt(res.getDocumentNumber()));
                enrolProfileDto.setPhotoBack(res.getPhotoBack());
                return Mono.just(new GetByDocumentResponseDto(enrolProfileDto));
            }).switchIfEmpty(Mono.just(new GetByDocumentResponseDto(enrolProfileDto)));
        }).switchIfEmpty(Mono.error(new EnrolmentIdNotFoundException(AbhaConstants.ENROLLMENT_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    private void handleNewOtpRedisObjectCreation(String txnId, String receiver, String aadhaarTxn, String otpValue) {
        RedisOtp otpRedis = RedisOtp.builder().txnId(txnId).otpValue(otpValue).aadhaarTxnId(aadhaarTxn).receiver(receiver).build();

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
        redisService.saveRedisOtp(txnId, otpRedis);
    }

    public Mono<AuthResponseDto> verifyOtpViaNotificationFlow(AuthRequestDto authByAbdmRequest) {
        Mono<AuthResponseDto> redisResponse = handleRedisABDMOtpVerification(authByAbdmRequest);
        if (redisResponse != null) {
            return redisResponse;
        }
        return transactionService.findTransactionDetailsFromDB(authByAbdmRequest.getAuthData().getOtp().getTxnId())
                .flatMap(transactionDto ->
                        verifyOtpViaNotificationDLFlow(authByAbdmRequest.getAuthData().getOtp().getOtpValue(), transactionDto))
                .switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    private Mono<AuthResponseDto> verifyOtpViaNotificationDLFlow(String otp, TransactionDto transactionDto) {
        try {
            if (GeneralUtils.isOtpExpired(transactionDto.getCreatedDate(), OTP_EXPIRE_TIME)) {
                return prepareAuthByAdbmResponse(transactionDto, false, OTP_EXPIRED_RESEND_OTP_AND_RETRY);
            } else if (Argon2Util.verify(transactionDto.getOtp(), otp)) {
                transactionDto.setMobileVerified(true);
                return transactionService.updateTransactionEntity(transactionDto, transactionDto.getTxnId().toString())
                        .flatMap(transactionDtoResponse -> {
                            redisService.deleteRedisOtp(transactionDto.getTxnId().toString());
                            redisService.deleteReceiverOtpTracker(redisOtp.getReceiver());
                            return prepareAuthByAdbmResponse(transactionDto, true, OTP_VERIFIED_SUCCESSFULLY);
                        });
            } else {
                return prepareAuthByAdbmResponse(transactionDto, false, OTP_VALUE_DID_NOT_MATCH_PLEASE_TRY_AGAIN);
            }
        } catch (BadParametersException ex) {
            return prepareAuthByAdbmResponse(transactionDto, false, FAILED_TO_VALIDATE_OTP_PLEASE_TRY_AGAIN);
        }
    }


    private Mono<AuthResponseDto> handleRedisABDMOtpVerification(AuthRequestDto authByAbdmRequest) {
        redisOtp = redisService.getRedisOtp(authByAbdmRequest.getAuthData().getOtp().getTxnId());
        if (redisOtp == null) {
            throw new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE);
        } else {
            if (!redisService.isMultipleOtpVerificationAllowed(redisOtp.getReceiver())) {
                throw new UnauthorizedUserToSendOrVerifyOtpException();
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
            accountResponseDto = AccountResponseDto.builder().EnrolmentNumber(transactionDto.getHealthIdNumber()).name(transactionDto.getName()).build();
        }

        return Mono.just(AuthResponseDto.builder().txnId(transactionDto.getTxnId().toString()).authResult(status ? StringConstants.SUCCESS : StringConstants.FAILED).message(message).accounts(status && StringUtils.isNoneEmpty(accountResponseDto.getEnrolmentNumber()) ? Collections.singletonList(accountResponseDto) : null).build());
    }

    public Mono<EnrollmentResponse> verifyFacilityByEnroll(EnrollmentStatusUpdate enrollmentStatusUpdate) {
        String status = enrollmentStatusUpdate.getVerificationStatus();
        return transactionService.findTransactionDetailsFromDB(enrollmentStatusUpdate.getTxnId()).flatMap(txnDto -> {
            log.info(TRANSACTION + txnDto.getTxnId() + FOUND);
            if (txnDto.isMobileVerified()) {
                log.info(MOBILE_NUMBER_IS_VERIFIED);
                return accountService.getAccountByHealthIdNumber(txnDto.getHealthIdNumber()).flatMap(accountDto -> {
                    if (accountDto.getVerificationStatus() == null) {
                        throw new AbhaUnProcessableException(INVALID_TRANSACTION_ID.getCode(), INVALID_TRANSACTION_ID.getMessage());
                    }
                    if (!accountDto.getVerificationStatus().equalsIgnoreCase(PROVISIONAL)) {
                        throw new AbhaUnProcessableException(INVALID_TRANSACTION_ID.getCode(), INVALID_TRANSACTION_ID.getMessage());
                    }
                    EnrollmentResponse enrollmentResponse;
                    if (status.equalsIgnoreCase(ACCEPT)) {
                        accountDto.setVerificationStatus(VERIFIED);
                        accountDto.setStatus(ACTIVE.getValue());
                        accountDto.setUpdateDate(now());
                        accountDto.setKycVerified(true);
                        accountService.updateAccountByHealthIdNumber(accountDto, txnDto.getHealthIdNumber()).subscribe();
                        AccountActionDto newAccountActionDto = new AccountActionDto();
                        newAccountActionDto.setAction(ACTIVATION);
                        newAccountActionDto.setField(STATUS);
                        newAccountActionDto.setCreatedDate(now());
                        newAccountActionDto.setHealthIdNumber(txnDto.getHealthIdNumber());
                        newAccountActionDto.setNewValue(ACTIVE.getValue());
                        newAccountActionDto.setPreviousValue(null);
                        newAccountActionDto.setReasons(enrollmentStatusUpdate.getMessage());
                        accountActionService.createAccountActionEntity(newAccountActionDto).subscribe();
                        notificationService.sendRegistrationSMS(accountDto.getMobile(), accountDto.getName(), accountDto.getHealthIdNumber()).subscribe();
                        enrollmentResponse = new EnrollmentResponse(ENROL_VERIFICATION_STATUS, ENROL_VERIFICATION_ACCEPT_MESSAGE, jwtUtil.generateToken(txnDto.getTxnId().toString(), accountDto));
                    } else {
                        if(enrollmentStatusUpdate.getMessage() == null || enrollmentStatusUpdate.getMessage().isBlank()) {
                            throw new AbhaUnProcessableException(ABDMError.INVALID_REASON.getCode(),ABDMError.INVALID_REASON.getMessage());
                        }
                        formatAccountDto(accountDto);
                        formatHidPhr(accountDto.getHealthIdNumber());
                        accountAuthMethodService.deleteAccountAuthMethodByHealthId(accountDto.getHealthIdNumber()).subscribe();
                        accountService.updateAccountByHealthIdNumber(accountDto, txnDto.getHealthIdNumber()).subscribe();
                        AccountActionDto newAccountActionDto = new AccountActionDto();
                        newAccountActionDto.setAction(DELETION);
                        newAccountActionDto.setField(STATUS);
                        newAccountActionDto.setCreatedDate(now());
                        newAccountActionDto.setHealthIdNumber(txnDto.getHealthIdNumber());
                        newAccountActionDto.setNewValue(DELETED);
                        newAccountActionDto.setPreviousValue(ACTIVE.getValue());
                        newAccountActionDto.setReasons(enrollmentStatusUpdate.getMessage());
                        accountActionService.createAccountActionEntity(newAccountActionDto).subscribe();
                        enrollmentResponse = new EnrollmentResponse(ENROL_VERIFICATION_STATUS, ENROL_VERIFICATION_REJECT_MESSAGE, null);
                    }
                    return Mono.just(enrollmentResponse);
                });
            } else {
                log.info(MOBILE_NUMBER_NOT_VERIFIED);
                throw new AbhaUnProcessableException(ABDMError.MOBILE_NUMBER_NOT_VERIFIED);
            }
        }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    private void formatAccountDto(AccountDto accountDto) {
        accountDto.setStatus(REJECTED);
        accountDto.setStatus(DELETED);
        accountDto.setUpdateDate(LocalDateTime.now());
        accountDto.setAddress(null);
        accountDto.setDayOfBirth(null);
        accountDto.setDistrictCode(null);
        accountDto.setDistrictName(null);
        accountDto.setEmail(null);
        accountDto.setFacilityId(null);
        accountDto.setFirstName(null);
        accountDto.setGender(GENDER);
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
        accountDto.setYearOfBirth(YEAR_OF_BIRTH);
    }

    private void formatHidPhr(String healthIdNumber) {
        hidPhrAddressService.findByHealthIdNumber(healthIdNumber)
                .flatMap(hidPhrAddressDto -> {
                    hidPhrAddressDto.setStatus(DELETED);
                    hidPhrAddressDto.setLastModifiedDate(LocalDateTime.now());
                    hidPhrAddressDto.setLastModifiedBy(FacilityContextHolder.getClientId());
                    return hidPhrAddressService.updateHidPhrAddressById(hidPhrAddressDto, hidPhrAddressDto.getHidPhrAddressId());
                }).subscribe();
    }
}
