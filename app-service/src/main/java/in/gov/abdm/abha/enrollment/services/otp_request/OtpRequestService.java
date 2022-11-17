package in.gov.abdm.abha.enrollment.services.otp_request;

import in.gov.abdm.abha.enrollment.client.AadhaarClient;
import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.enums.TransactionStatus;
import in.gov.abdm.abha.enrollment.exception.aadhaar.UidaiException;
import in.gov.abdm.abha.enrollment.exception.application.GenericExceptionMessage;
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
import in.gov.abdm.abha.enrollment.utilities.argon2.Argon2;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

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
    private static final String OTP_IS_SENT_TO_AADHAAR_REGISTERED_MOBILE_ENDING = "OTP is sent to Aadhaar registered mobile ending ";
    private static final String OTP_SUBJECT = "mobile verification";
    private static final String SENT = "sent";
    private static final String FAILED_TO_SEND_OTP_FOR_MOBILE_VERIFICATION = "Failed to Send OTP for Mobile verification";
    private static final String TRANSACTION_DETAILS_NOT_FOUND = "Transaction details not found";
    private static final String FAILED_TO_SEND_OTP = "Failed to send OTP";
    private static final String MESSAGE = "OTP is sent to Aadhaar/ABHA registered mobile ending xxx3604";
    private static final String FAILED_TO_CALL_IDP_SERVICE = "Failed to call IDP service";

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
                    transactionDto.setOtp(Argon2.encode(newOtp));
                    transactionDto.setOtpRetryCount(transactionDto.getOtpRetryCount() + 1);
                    return transactionService.updateTransactionEntity(transactionDto, transactionDto.getTxnId().toString())
                            .flatMap(res -> Mono.just(MobileOrEmailOtpResponseDto.builder()
                                    .txnId(mobileOrEmailOtpRequestDto.getTxnId())
                                    .message(OTP_IS_SENT_TO_AADHAAR_REGISTERED_MOBILE_ENDING + Common.hidePhoneNumber(phoneNumber))
                                    .build()));
                }else{
                    throw new FailedToSendNotificationException(FAILED_TO_SEND_OTP_FOR_MOBILE_VERIFICATION);
                }
            });
        }).switchIfEmpty(Mono.defer(() -> {
            throw new GenericExceptionMessage(TRANSACTION_DETAILS_NOT_FOUND);
        }));
    }


    /**
     * call aadhaar service and prepare transaction details
     *
     * @param mobileOrEmailOtpRequestDto
     * @return
     */
    public Mono<MobileOrEmailOtpResponseDto> sendAadhaarOtp(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto) {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setTxnId(UUID.randomUUID());
        transactionDto.setState(TransactionStatus.ACTIVE.toString());
        transactionDto.setAadharNo(mobileOrEmailOtpRequestDto.getLoginId());
        transactionDto.setClientIp(Common.getIpAddress());

        Mono<AadhaarResponseDto> aadhaarResponseDto = aadhaarClient.sendOtp(new AadhaarOtpRequestDto(mobileOrEmailOtpRequestDto.getLoginId()));
        return aadhaarResponseDto.flatMap(res -> handleAadhaarOtpResponse(res, transactionDto));
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
            throw new UidaiException(aadhaarResponseDto);
        }
    }

    public Mono<MobileOrEmailOtpResponseDto> sendIdpOtp(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto) {
        mobileOrEmailOtpRequestDto.setLoginId(rsaUtil.decrypt(mobileOrEmailOtpRequestDto.getLoginId()));
        return transactionService.findTransactionDetailsFromDB(mobileOrEmailOtpRequestDto.getTxnId())
                        .flatMap(res->sendIdpOtpAndUpdateTransaction(mobileOrEmailOtpRequestDto, res));
    }

    private Mono<MobileOrEmailOtpResponseDto> sendIdpOtpAndUpdateTransaction(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto, TransactionDto transactionDto) {
        return idpService.sendOtp(mobileOrEmailOtpRequestDto)
                .flatMap(idpSendOtpResponse -> {
                    if(!StringUtils.isEmpty(idpSendOtpResponse.getTransactionId())){
                        String oldTransactionId = transactionDto.getTxnId().toString();
                        transactionDto.setTxnId(UUID.fromString(idpSendOtpResponse.getTransactionId()));
                        transactionService.updateTransactionEntity(transactionDto, oldTransactionId)
                                .flatMap(res->{
                                    MobileOrEmailOtpResponseDto mobileOrEmailOtpResponseDto = new MobileOrEmailOtpResponseDto();
                                    mobileOrEmailOtpResponseDto.setTxnId(res.getTxnId().toString());
                                    //TODO get mobile number from IDP
                                    mobileOrEmailOtpResponseDto.setMessage(MESSAGE);
                                    return Mono.just(mobileOrEmailOtpResponseDto);
                                });
                    }
                    throw new GenericExceptionMessage(FAILED_TO_CALL_IDP_SERVICE);
                });
    }
}
