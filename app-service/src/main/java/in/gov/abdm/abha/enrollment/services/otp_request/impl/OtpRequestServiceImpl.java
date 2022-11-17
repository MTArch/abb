package in.gov.abdm.abha.enrollment.services.otp_request.impl;

import in.gov.abdm.abha.enrollment.client.AadhaarClient;
import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.enums.TransactionStatus;
import in.gov.abdm.abha.enrollment.enums.request.OtpSystem;
import in.gov.abdm.abha.enrollment.exception.aadhaar.UidaiException;
import in.gov.abdm.abha.enrollment.model.aadhaar.AadhaarOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpResponseDto;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.otp_request.OtpRequestService;
import in.gov.abdm.abha.enrollment.utilities.Common;
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
public class OtpRequestServiceImpl implements OtpRequestService {

    /**
     * Content for logs
     */
    public static final String FAILED_TO_GENERATE_AADHAAR_OTP_TRANSACTION_REASON = "Failed to Generate Aadhaar OTP : Transaction : {} : Reason : {}";
    public static final String OTP_IS_SENT_TO_AADHAAR_REGISTERED_MOBILE_ENDING = "OTP is sent to Aadhaar registered mobile ending ";

    /**
     * transaction service to helps to prepare transaction entity details
     */
    @Autowired
    TransactionService transactionService;
    @Autowired
    AadhaarClient aadhaarClient;

    /**
     * if otp requested using aadhaar service then call aadhaar global service
     * if otp is requested using abdm then call notification global service
     *
     * @param mobileOrEmailOtpRequestDto
     * @return
     */
    @Override
    public Mono<MobileOrEmailOtpResponseDto> sendOtp(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto) {
        if (mobileOrEmailOtpRequestDto.getOtpSystem().equals(OtpSystem.AADHAAR.getValue())) {
            return sendAadhaarOtp(mobileOrEmailOtpRequestDto);
        }
        return null;
    }

    /**
     * call aadhaar service and prepare transaction details
     *
     * @param mobileOrEmailOtpRequestDto
     * @return
     */
    private Mono<MobileOrEmailOtpResponseDto> sendAadhaarOtp(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto) {
        TransactionDto transactionDto = new TransactionDto();

        transactionDto.setTxnId(UUID.randomUUID());
        transactionDto.setState(TransactionStatus.ACTIVE.toString());
        transactionDto.setAadharNo(mobileOrEmailOtpRequestDto.getLoginId());
        //TODO discuss about Transaction DTO UUID
        //transactionDto.setAadharTxn(transactionService.generateTransactionId(false));
        transactionDto.setClientIp(Common.getIpAddress());
        //TODO call aadhaar global service
        Mono<AadhaarResponseDto> aadhaarResponseDto = aadhaarClient.sendOtp(new AadhaarOtpRequestDto(mobileOrEmailOtpRequestDto.getLoginId()));
        return aadhaarResponseDto.flatMap(res -> handleAadhaarOtpResponse(res, transactionDto));
    }

    private Mono<MobileOrEmailOtpResponseDto> handleAadhaarOtpResponse(AadhaarResponseDto aadhaarResponseDto, TransactionDto transactionDto) {

        handleAadhaarOtpExceptions(aadhaarResponseDto, transactionDto);

        transactionDto.setMobile(aadhaarResponseDto.getAadhaarAuthOtpDto().getMobileNumber());
        transactionDto.setAadharTxn(aadhaarResponseDto.getAadhaarAuthOtpDto().getUidtkn());

        //TODO call DB service
        Mono<TransactionDto> createTransactionResponse = transactionService.createTransactionEntity(transactionDto);

        return createTransactionResponse.flatMap(res -> mobileOrEmailOtpResponse(res, transactionDto));
        //return mobileOrEmailOtpResponse(transactionDto);
    }

    /**
     * update transaction details into database by calling abha db service
     *
     * @param transactionDto
     */
    private Mono<MobileOrEmailOtpResponseDto> mobileOrEmailOtpResponse(TransactionDto createTransactionResponse, TransactionDto transactionDto) {
        if (!StringUtils.isEmpty(createTransactionResponse.getAadharNo())) {
            log.info("Transaction Id: "+createTransactionResponse.getId()+" transaction : "+createTransactionResponse.getTxnId());
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
}
