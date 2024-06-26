package in.gov.abdm.abha.enrollment.services.otp_request;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.enums.LoginHint;
import in.gov.abdm.abha.enrollment.enums.TransactionStatus;
import in.gov.abdm.abha.enrollment.enums.request.AadhaarLogType;
import in.gov.abdm.abha.enrollment.enums.request.OtpSystem;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarExceptions;
import in.gov.abdm.abha.enrollment.exception.abha_db.AbhaDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.abha_db.TransactionNotFoundException;
import in.gov.abdm.abha.enrollment.exception.application.AbhaUnProcessableException;
import in.gov.abdm.abha.enrollment.exception.application.UnauthorizedUserToSendOrVerifyOtpException;
import in.gov.abdm.abha.enrollment.exception.idp.IdpGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.notification.NotificationGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.notification.NotificationResponseDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpResponseDto;
import in.gov.abdm.abha.enrollment.model.redis.otp.ReceiverOtpTracker;
import in.gov.abdm.abha.enrollment.model.redis.otp.RedisOtp;
import in.gov.abdm.abha.enrollment.services.aadhaar.AadhaarAppService;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;
import java.util.stream.Collectors;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.*;
import static in.gov.abdm.abha.enrollment.constants.PropertyConstants.ENROLLMENT_MAX_MOBILE_LINKING_COUNT;

/**
 * service for OTP Request coming from ui
 * otp can be sent via aadhaar / abdm
 */
@Service
@Slf4j
public class OtpRequestService {

    /**
     * Content for logs
     */
    private static final String FAILED_TO_GENERATE_AADHAAR_OTP_TRANSACTION_REASON = "Failed to Generate Aadhaar OTP : Transaction : {} : Reason : {}";
    private static final String OTP_IS_SENT_TO_AADHAAR_REGISTERED_MOBILE_ENDING = "OTP sent to Aadhaar registered mobile number ending with ";
    private static final String OTP_IS_SENT_TO_ABHA_REGISTERED_MOBILE_ENDING = "OTP sent to ABHA registered mobile number ending with ";
    private static final String OTP_IS_SENT_TO_MOBILE_ENDING = "OTP sent to mobile number ending with ";
    private static final String SENT_AADHAAR_OTP = "Sent Aadhaar OTP";
    private static final String EMAIL_OTP_SUBJECT = "email verification";

    private static final String OTP_IS_SENT_TO_EMAIL_ENDING = "OTP sent to email address ending with ";

    /**
     * transaction service to helps to prepare transaction entity details
     */
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
    AadhaarAppService aadhaarAppService;

    @Value(ENROLLMENT_MAX_MOBILE_LINKING_COUNT)
    private int maxMobileLinkingCount;

    public Mono<MobileOrEmailOtpResponseDto> sendOtpViaNotificationService(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto) {
        String phoneNumber = rsaUtil.decrypt(mobileOrEmailOtpRequestDto.getLoginId());
        String newOtp = GeneralUtils.generateRandomOTP();

        if (!redisService.isResendOtpAllowed(phoneNumber)) {
            throw new UnauthorizedUserToSendOrVerifyOtpException();
        }
        return transactionService.findTransactionDetailsFromDB(mobileOrEmailOtpRequestDto.getTxnId()).flatMap(transactionDto -> {
            if (transactionDto.getStatus().equalsIgnoreCase(AccountStatus.DEACTIVATED.getValue())) {
                throw new AbhaUnProcessableException(ABDMError.UN_PROCESSABLE_ENTITY.getCode(), THIS_ACCOUNT_ALREADY_EXIST_AND_DEACTIVATED);
            }
            return accountService.getMobileLinkedAccountCount(phoneNumber).flatMap(mobileLinkedAccountCount -> {
                if (mobileLinkedAccountCount >= maxMobileLinkingCount) {
                    throw new AbhaUnProcessableException(ABDMError.MOBILE_ALREADY_LINKED_TO_6_ACCOUNTS.getCode(), MessageFormat.format(MOBILE_ALREADY_LINKED_TO_MAX_ACCOUNTS, maxMobileLinkingCount));
                } else {
                    Mono<NotificationResponseDto> notificationResponseDtoMono = notificationService.sendRegistrationOtp(phoneNumber, newOtp);

                    return notificationResponseDtoMono.flatMap(response -> {
                        if (response.getStatus().equals(SENT)) {
                            transactionDto.setMobile(phoneNumber);
                            transactionDto.setOtp(Argon2Util.encode(newOtp));
                            transactionDto.setOtpRetryCount(transactionDto.getOtpRetryCount() + 1);
                            transactionDto.setCreatedDate(LocalDateTime.now());
                            transactionDto.setScope(Scopes.MOBILE_VERIFY.getValue());
                            return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getId())).flatMap(res -> {
                                handleNewOtpRedisObjectCreation(transactionDto.getTxnId().toString(), phoneNumber, StringUtils.EMPTY, Argon2Util.encode(newOtp));
                                return Mono.just(MobileOrEmailOtpResponseDto.builder().txnId(mobileOrEmailOtpRequestDto.getTxnId()).message(OTP_IS_SENT_TO_MOBILE_ENDING + Common.hidePhoneNumber(phoneNumber)).build());
                            });
                        } else {
                            throw new NotificationGatewayUnavailableException();
                        }
                    });
                }
            });

        }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }


    /**
     * call aadhaar service and prepare transaction details
     *
     * @param mobileOrEmailOtpRequestDto
     * @return
     */
    public Mono<MobileOrEmailOtpResponseDto> sendAadhaarOtp(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto) {

        if (!redisService.isResendOtpAllowed(rsaUtil.decrypt(mobileOrEmailOtpRequestDto.getLoginId()))) {
            throw new UnauthorizedUserToSendOrVerifyOtpException();
        }

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setStatus(TransactionStatus.ACTIVE.toString());
        transactionDto.setAadharNo(mobileOrEmailOtpRequestDto.getLoginId());
        transactionDto.setClientIp(Common.getIpAddress());
        transactionDto.setTxnId(UUID.randomUUID());
        transactionDto.setKycPhoto(null);

        // Child abha parent Linking send parent aadhaar otp flow
        if (Common.isScopeAvailable(mobileOrEmailOtpRequestDto.getScope().stream().distinct().collect(Collectors.toList()), Scopes.CHILD_ABHA_ENROL)
                && Common.isOtpSystem(mobileOrEmailOtpRequestDto.getOtpSystem(), OtpSystem.AADHAAR)) {
            return transactionService.findTransactionDetailsFromDB(mobileOrEmailOtpRequestDto.getTxnId())
                    .flatMap(res1 -> {
                        if (res1.getHealthIdNumber() != null)
                            transactionDto.setHealthIdNumber(res1.getHealthIdNumber());

                        transactionDto.setKycPhoto(res1.getKycPhoto());
                        Mono<AadhaarResponseDto> aadhaarResponseDto = aadhaarAppService.sendOtp(new AadhaarOtpRequestDto(mobileOrEmailOtpRequestDto.getLoginId(), AadhaarLogType.KYC_GEN_OTP.name()));
                        return aadhaarResponseDto.flatMap(res ->
                                handleAadhaarOtpResponse(res, transactionDto)
                        );
                    }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
        } else { // standard abha send aadhaar otp flow
            Mono<AadhaarResponseDto> aadhaarResponseDto = aadhaarAppService.sendOtp(new AadhaarOtpRequestDto(mobileOrEmailOtpRequestDto.getLoginId(), AadhaarLogType.KYC_GEN_OTP.name()));
            return aadhaarResponseDto.flatMap(res ->
                    {
                        handleNewOtpRedisObjectCreation(transactionDto.getTxnId().toString(), rsaUtil.decrypt(mobileOrEmailOtpRequestDto.getLoginId()), res.getAadhaarAuthOtpDto().getUidtkn(), StringUtils.EMPTY);
                        return handleAadhaarOtpResponse(res, transactionDto);
                    }
            );
        }
    }

    private Mono<MobileOrEmailOtpResponseDto> handleAadhaarOtpResponse(AadhaarResponseDto aadhaarResponseDto, TransactionDto transactionDto) {

        handleAadhaarOtpExceptions(aadhaarResponseDto, transactionDto);

        transactionDto.setMobile(aadhaarResponseDto.getAadhaarAuthOtpDto().getMobileNumber());
        transactionDto.setAadharTxn(aadhaarResponseDto.getAadhaarAuthOtpDto().getUidtkn());
        transactionDto.setCreatedDate(LocalDateTime.now());

        Mono<TransactionDto> createTransactionResponse = transactionService.createTransactionEntity(transactionDto);
        return createTransactionResponse.flatMap(res -> mobileOrEmailOtpResponse(res, transactionDto));

    }

    /**
     * update transaction details into database by calling abha db service
     *
     * @param transactionDto
     */
    private Mono<MobileOrEmailOtpResponseDto> mobileOrEmailOtpResponse(TransactionDto createTransactionResponse, TransactionDto transactionDto) {
        if (!StringUtils.isEmpty(createTransactionResponse.getAadharNo())) {
            log.info("Transaction Id: " + createTransactionResponse.getId() + " transaction : " + createTransactionResponse.getTxnId());
            return Mono.just(MobileOrEmailOtpResponseDto.builder()
                    .txnId(transactionDto.getTxnId().toString())
                    .message(OTP_IS_SENT_TO_AADHAAR_REGISTERED_MOBILE_ENDING + Common.hidePhoneNumber(transactionDto.getMobile()))
                    .build());
        } else {
            return Mono.empty();
        }
    }

    /**
     * handle aadhaar global service send oto response
     * and throw response to client applications
     *
     * @param aadhaarResponseDto
     * @param transactionDto
     * @return
     */
    private void handleAadhaarOtpExceptions(AadhaarResponseDto aadhaarResponseDto, TransactionDto transactionDto) {
        if (!aadhaarResponseDto.getAadhaarAuthOtpDto().getStatus().equalsIgnoreCase(StringConstants.SUCCESS)) {
            log.error(FAILED_TO_GENERATE_AADHAAR_OTP_TRANSACTION_REASON,
                    transactionDto.getTxnId(),
                    aadhaarResponseDto.getReason());
            throw new AadhaarExceptions(aadhaarResponseDto.getErrorCode());
        }
        log.info(SENT_AADHAAR_OTP);
    }

    public Mono<MobileOrEmailOtpResponseDto> sendIdpOtp(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto) {
        mobileOrEmailOtpRequestDto.setLoginId(rsaUtil.decrypt(mobileOrEmailOtpRequestDto.getLoginId()));
        return transactionService.findTransactionDetailsFromDB(mobileOrEmailOtpRequestDto.getTxnId())
                .flatMap(res -> sendIdpOtpAndUpdateTransaction(mobileOrEmailOtpRequestDto, res))
                .switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    private Mono<MobileOrEmailOtpResponseDto> sendIdpOtpAndUpdateTransaction(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto, TransactionDto transactionDto) {
        return idpService.sendOtp(mobileOrEmailOtpRequestDto)
                .flatMap(idpSendOtpResponse -> {
                    if (!StringUtils.isEmpty(idpSendOtpResponse.getTransactionId())) {
                        TransactionDto trDto = transactionDto;
                        trDto.setTxnId(UUID.fromString(idpSendOtpResponse.getTransactionId()));
                        trDto.setAadharTxn(idpSendOtpResponse.getResponse().getRequestId());
                        trDto.setId(null);
                        trDto.setCreatedDate(LocalDateTime.now());
                        return transactionService.createTransactionEntity(trDto)
                                .flatMap(res -> {
                                    String message = StringConstants.EMPTY;
                                    if (mobileOrEmailOtpRequestDto.getLoginHint().equals(LoginHint.ABHA_NUMBER)) {
                                        message = OTP_IS_SENT_TO_ABHA_REGISTERED_MOBILE_ENDING
                                                .concat(idpSendOtpResponse.getOtpSentTo());
                                    } else if (mobileOrEmailOtpRequestDto.getLoginHint().equals(LoginHint.MOBILE)) {
                                        message = OTP_IS_SENT_TO_MOBILE_ENDING.concat(idpSendOtpResponse.getOtpSentTo());
                                    }

                                    return Mono.just(MobileOrEmailOtpResponseDto.builder()
                                            .txnId(res.getTxnId().toString())
                                            .message(message)
                                            .build());
                                }).switchIfEmpty(Mono.error(new AbhaDBGatewayUnavailableException()));
                    }
                    return Mono.empty();
                }).switchIfEmpty(Mono.error(new IdpGatewayUnavailableException()));
    }

    public Mono<MobileOrEmailOtpResponseDto> sendEmailOtpViaNotificationService(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto) {
        String email = rsaUtil.decrypt(mobileOrEmailOtpRequestDto.getLoginId());
        String newOtp = GeneralUtils.generateRandomOTP();
        return transactionService.findTransactionDetailsFromDB(mobileOrEmailOtpRequestDto.getTxnId()).flatMap(transactionDto -> {
            if (transactionDto.getStatus().equalsIgnoreCase(AccountStatus.DEACTIVATED.getValue())) {
                throw new AbhaUnProcessableException(ABDMError.UN_PROCESSABLE_ENTITY.getCode(), THIS_ACCOUNT_ALREADY_EXIST_AND_DEACTIVATED);
            }
            return accountService.getEmailLinkedAccountCount(email).flatMap(emailLinkedAccountCount -> {
                if (emailLinkedAccountCount >= maxMobileLinkingCount) {
                    throw new AbhaUnProcessableException(ABDMError.EMAIL_ALREADY_LINKED_TO_6_ACCOUNTS.getCode(), MessageFormat.format(EMAIL_ALREADY_LINKED_TO_MAX_ACCOUNTS, maxMobileLinkingCount));
                } else {
                    return templatesHelper.prepareSMSMessage(REGISTRATION_OTP_TEMPLATE_ID, newOtp).flatMap(message -> {
                        Mono<NotificationResponseDto> notificationResponseDtoMono = notificationService.sendEmailOtp(email, EMAIL_OTP_SUBJECT, message);

                        return notificationResponseDtoMono.flatMap(response -> {
                            if (response.getStatus().equals(SENT)) {
                                transactionDto.setEmail(email);
                                transactionDto.setOtp(Argon2Util.encode(newOtp));
                                transactionDto.setOtpRetryCount(transactionDto.getOtpRetryCount() + 1);
                                transactionDto.setCreatedDate(LocalDateTime.now());
                                transactionDto.setScope(Scopes.EMAIL_VERIFY.getValue());
                                return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getId())).flatMap(res -> {
                                    handleNewOtpRedisObjectCreation(transactionDto.getTxnId().toString(), email, StringUtils.EMPTY, Argon2Util.encode(newOtp));
                                    return Mono.just(MobileOrEmailOtpResponseDto.builder().txnId(mobileOrEmailOtpRequestDto.getTxnId()).message(OTP_IS_SENT_TO_EMAIL_ENDING + Common.hideEmail(email)).build());
                                });
                            } else {
                                throw new NotificationGatewayUnavailableException();
                            }
                        });
                    });
                }
            });

        }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));

    }

    public Mono<MobileOrEmailOtpResponseDto> sendOtpViaNotificationServiceDLFlow(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto) {
        String phoneNumber = rsaUtil.decrypt(mobileOrEmailOtpRequestDto.getLoginId());
        String newOtp = GeneralUtils.generateRandomOTP();

        return accountService.getMobileLinkedAccountCount(phoneNumber)
                .flatMap(mobileLinkedAccountCount -> {
                    if (mobileLinkedAccountCount >= maxMobileLinkingCount) {
                        throw new AbhaUnProcessableException(ABDMError.MOBILE_ALREADY_LINKED_TO_6_ACCOUNTS.getCode(), MessageFormat.format(MOBILE_ALREADY_LINKED_TO_MAX_ACCOUNTS, maxMobileLinkingCount));
                    } else {
                        if (!redisService.isResendOtpAllowed(phoneNumber)) {
                            throw new UnauthorizedUserToSendOrVerifyOtpException();
                        }
                        TransactionDto transactionDto = new TransactionDto();
                        transactionDto.setStatus(TransactionStatus.ACTIVE.toString());
                        transactionDto.setMobile(phoneNumber);
                        transactionDto.setClientIp(Common.getIpAddress());
                        transactionDto.setTxnId(UUID.randomUUID());
                        transactionDto.setOtp(Argon2Util.encode(newOtp));
                        transactionDto.setKycPhoto(null);

                        Mono<NotificationResponseDto> notificationResponseDtoMono
                                = notificationService.sendRegistrationOtp(phoneNumber, newOtp);

                        return notificationResponseDtoMono.flatMap(response -> {
                            if (response.getStatus().equals(SENT)) {
                                transactionDto.setOtpRetryCount(transactionDto.getOtpRetryCount() + 1);
                                transactionDto.setCreatedDate(LocalDateTime.now());
                                return transactionService.createTransactionEntity(transactionDto)
                                        .flatMap(res -> {
                                            handleNewOtpRedisObjectCreation(transactionDto.getTxnId().toString(), phoneNumber, StringUtils.EMPTY, Argon2Util.encode(newOtp));
                                            return Mono.just(MobileOrEmailOtpResponseDto.builder()
                                                    .txnId(transactionDto.getTxnId().toString())
                                                    .message(OTP_IS_SENT_TO_MOBILE_ENDING + Common.hidePhoneNumber(phoneNumber))
                                                    .build());
                                        });
                            } else {
                                throw new NotificationGatewayUnavailableException();
                            }
                        });
                    }
                });
    }

    private void handleNewOtpRedisObjectCreation(String txnId, String receiver, String aadhaarTxn, String otpValue) {
        RedisOtp redisOtp = RedisOtp.builder()
                .txnId(txnId)
                .otpValue(otpValue)
                .aadhaarTxnId(aadhaarTxn)
                .receiver(receiver)
                .build();

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
}
