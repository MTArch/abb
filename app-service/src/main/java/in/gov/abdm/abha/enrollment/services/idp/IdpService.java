package in.gov.abdm.abha.enrollment.services.idp;


import in.gov.abdm.abha.enrollment.client.IDPClient;
import in.gov.abdm.abha.enrollment.enums.LoginHint;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.idp.IdpMobileSendOtpRequest;
import in.gov.abdm.abha.enrollment.model.idp.IdpMobileSendOtpResponse;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpResponseDto;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
/**
 * A class which implements Business logic.
 */
public class IdpService {

    public static final String SCOPE = "OTP";
    public static final String MESSAGE = "OTP is sent to Aadhaar/ABHA registered mobile ending xxx3604";

    public static final String OTP_IS_SENT_TO_AADHAAR_REGISTERED_MOBILE_ENDING = "OTP is sent to Aadhaar registered mobile ending ";

    @Autowired
    IDPClient idpClient;

    @Autowired
    TransactionService transactionService;

    public Mono<MobileOrEmailOtpResponseDto> sendMobileOtpByIDP(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto) {

        if (mobileOrEmailOtpRequestDto.getLoginHint().equalsIgnoreCase(String.valueOf(LoginHint.MOBILE))) {

            IdpMobileSendOtpRequest idpMobileSendOtpRequest = new IdpMobileSendOtpRequest();
            idpMobileSendOtpRequest.setScope(SCOPE);
            TransactionDto transactionDto = new TransactionDto();
            transactionDto.setTxnId(UUID.randomUUID());
            Mono<IdpMobileSendOtpResponse> idpMobileSendOtpResponse = idpClient.sendOtp(idpMobileSendOtpRequest);
            return idpMobileSendOtpResponse.flatMap(res -> HandleIdpMobileSendOtpResponse(res, transactionDto));
        }
        return null;
    }

    private Mono<MobileOrEmailOtpResponseDto> HandleIdpMobileSendOtpResponse(IdpMobileSendOtpResponse idpMobileSendOtpResponse, TransactionDto transactionDto) {
        MobileOrEmailOtpResponseDto mobileOrEmailOtpResponseDto = new MobileOrEmailOtpResponseDto();
        mobileOrEmailOtpResponseDto.setTxnId("");
        mobileOrEmailOtpResponseDto.setMessage(MESSAGE);
        Mono<TransactionDto> createTransactionRes = transactionService.createTransactionEntity(transactionDto);
        return createTransactionRes.flatMap(res -> mobileOrEmailOtpResponse(res, transactionDto));

        //return Mono.just(mobileOrEmailOtpResponseDto);
    }

    /**
     * update transaction details into database
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

}
