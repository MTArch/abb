package in.gov.abdm.abha.enrollment.services.otp_request;

import in.gov.abdm.abha.enrollment.client.AadhaarClient;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.EnrollErrorConstants;
import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.enums.LoginHint;
import in.gov.abdm.abha.enrollment.enums.TransactionStatus;
import in.gov.abdm.abha.enrollment.enums.request.OtpSystem;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarExceptions;
import in.gov.abdm.abha.enrollment.exception.application.GenericExceptionMessage;
import in.gov.abdm.abha.enrollment.exception.database.constraint.DatabaseConstraintFailedException;
import in.gov.abdm.abha.enrollment.exception.database.constraint.TransactionNotFoundException;
import in.gov.abdm.abha.enrollment.exception.notification.FailedToSendNotificationException;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.notification.NotificationResponseDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpResponseDto;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.idp.IdpService;
import in.gov.abdm.abha.enrollment.services.notification.NotificationService;
import in.gov.abdm.abha.enrollment.services.notification.TemplatesHelper;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import in.gov.abdm.abha.enrollment.utilities.argon2.Argon2Util;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private static final String OTP_IS_SENT_TO_AADHAAR_REGISTERED_MOBILE_ENDING = "OTP is sent to Aadhaar registered mobile number ending with ";
    private static final String OTP_IS_SENT_TO_ABHA_REGISTERED_MOBILE_ENDING = "OTP is sent to ABHA registered mobile number ending with ";
    private static final String OTP_IS_SENT_TO_MOBILE_ENDING = "OTP is sent to Mobile number ending with ";
    private static final String OTP_SUBJECT = "mobile verification";
    private static final String SENT = "sent";
    private static final String FAILED_TO_SEND_OTP_FOR_MOBILE_VERIFICATION = "Failed to Send OTP for Mobile verification";
    private static final String FAILED_TO_CALL_IDP_SERVICE = "Failed to call IDP service";
    private static final String SENT_AADHAAR_OTP = "Sent Aadhaar OTP";

    private static final String EMAIL_OTP_SUBJECT = "email verification";

    private static final String OTP_IS_SENT_TO_EMAIL_ENDING = "OTP is sent to email ending with ";

    /**
     * transaction service to helps to prepare transaction entity details
     */
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

    public Mono<MobileOrEmailOtpResponseDto> sendOtpViaNotificationService(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto) {
        String phoneNumber = rsaUtil.decrypt(mobileOrEmailOtpRequestDto.getLoginId());
        String newOtp = GeneralUtils.generateRandomOTP();
        Mono<TransactionDto> transactionDtoMono = transactionService.findTransactionDetailsFromDB(mobileOrEmailOtpRequestDto.getTxnId());
        return transactionDtoMono.flatMap(transactionDto -> {
            Mono<NotificationResponseDto> notificationResponseDtoMono = notificationService.sendSMSOtp(
                    phoneNumber,
                    OTP_SUBJECT,
                    templatesHelper.prepareUpdateMobileMessage(newOtp));

            return notificationResponseDtoMono.flatMap(response -> {
                if (response.getStatus().equals(SENT)) {
                    transactionDto.setMobile(phoneNumber);
                    transactionDto.setOtp(Argon2Util.encode(newOtp));
                    transactionDto.setOtpRetryCount(transactionDto.getOtpRetryCount() + 1);
                    return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getId()))
                            .flatMap(res -> Mono.just(MobileOrEmailOtpResponseDto.builder()
                                    .txnId(mobileOrEmailOtpRequestDto.getTxnId())
                                    .message(OTP_IS_SENT_TO_MOBILE_ENDING + Common.hidePhoneNumber(phoneNumber))
                                    .build()));
                } else {
                    throw new FailedToSendNotificationException(FAILED_TO_SEND_OTP_FOR_MOBILE_VERIFICATION);
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

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setState(TransactionStatus.ACTIVE.toString());
        transactionDto.setAadharNo(mobileOrEmailOtpRequestDto.getLoginId());
        transactionDto.setClientIp(Common.getIpAddress());
        transactionDto.setTxnId(UUID.randomUUID());
        transactionDto.setKycPhoto(Base64.getEncoder().encodeToString(new byte[1]));

        if (Common.isScopeAvailable(mobileOrEmailOtpRequestDto.getScope().stream().distinct().collect(Collectors.toList()), Scopes.CHILD_ABHA_ENROL)
                && Common.isOtpSystem(mobileOrEmailOtpRequestDto.getOtpSystem(), OtpSystem.AADHAAR)) {
            return transactionService.findTransactionDetailsFromDB(mobileOrEmailOtpRequestDto.getTxnId())
                    .flatMap(res1 -> {
                        if (res1.getHealthIdNumber() != null)
                            transactionDto.setHealthIdNumber(res1.getHealthIdNumber());
                        
                        transactionDto.setKycPhoto(res1.getKycPhoto());
                        Mono<AadhaarResponseDto> aadhaarResponseDto = aadhaarClient.sendOtp(new AadhaarOtpRequestDto(mobileOrEmailOtpRequestDto.getLoginId()));
                        return aadhaarResponseDto.flatMap(res ->
                                {
                                    return handleAadhaarOtpResponse(res, transactionDto);
                                }
                        );
                    }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
        } else {
            Mono<AadhaarResponseDto> aadhaarResponseDto = aadhaarClient.sendOtp(new AadhaarOtpRequestDto(mobileOrEmailOtpRequestDto.getLoginId()));
            return aadhaarResponseDto.flatMap(res ->
                    {
                        return handleAadhaarOtpResponse(res, transactionDto);
                    }
            );
        }
    }

    private Mono<MobileOrEmailOtpResponseDto> handleAadhaarOtpResponse(AadhaarResponseDto aadhaarResponseDto, TransactionDto transactionDto) {

        handleAadhaarOtpExceptions(aadhaarResponseDto, transactionDto);

        transactionDto.setMobile(aadhaarResponseDto.getAadhaarAuthOtpDto().getMobileNumber());
        transactionDto.setAadharTxn(aadhaarResponseDto.getAadhaarAuthOtpDto().getUidtkn());

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
                                }).switchIfEmpty(Mono.error(new DatabaseConstraintFailedException(EnrollErrorConstants.EXCEPTION_OCCURRED_POSTGRES_DATABASE_CONSTRAINT_FAILED_WHILE_UPDATE)));
                    }
                    return Mono.empty();
                }).switchIfEmpty(Mono.error(new GenericExceptionMessage(FAILED_TO_CALL_IDP_SERVICE)));
    }

    public Mono<MobileOrEmailOtpResponseDto> sendEmailOtpViaNotificationService(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto) {
        String email = rsaUtil.decrypt(mobileOrEmailOtpRequestDto.getLoginId());
        String newOtp = GeneralUtils.generateRandomOTP();
        Mono<TransactionDto> transactionDtoMono = transactionService.findTransactionDetailsFromDB(mobileOrEmailOtpRequestDto.getTxnId());
        return transactionDtoMono.flatMap(transactionDto -> {
            Mono<NotificationResponseDto> notificationResponseDtoMono = notificationService.sendEmailOtp(
                    email,
                    EMAIL_OTP_SUBJECT,
                    templatesHelper.prepareUpdateMobileMessage(newOtp));

            return notificationResponseDtoMono.flatMap(response -> {
                if (response.getStatus().equals(SENT)) {
                    transactionDto.setEmail(email);
                    transactionDto.setOtp(Argon2Util.encode(newOtp));
                    transactionDto.setOtpRetryCount(transactionDto.getOtpRetryCount() + 1);
                    return transactionService.updateTransactionEntity(transactionDto, String.valueOf(transactionDto.getId()))
                            .flatMap(res -> Mono.just(MobileOrEmailOtpResponseDto.builder()
                                    .txnId(mobileOrEmailOtpRequestDto.getTxnId())
                                    .message(OTP_IS_SENT_TO_EMAIL_ENDING + Common.hidePhoneNumber(email))
                                    .build()));
                } else {
                    throw new FailedToSendNotificationException(FAILED_TO_SEND_OTP_FOR_MOBILE_VERIFICATION);
                }
            });
        }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }
}
