package in.gov.abdm.abha.enrollment.services.idp;


import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gov.abdm.abha.enrollment.client.IDPClient;
import in.gov.abdm.abha.enrollment.enums.LoginHint;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.idp.sendotp.IdpSendOtpRequest;
import in.gov.abdm.abha.enrollment.model.idp.sendotp.IdpSendOtpResponse;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpResponseDto;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
/**
 * It is Service class IdpService
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

            IdpSendOtpRequest idpMobileSendOtpRequest = new IdpSendOtpRequest();
            idpMobileSendOtpRequest.setScope(SCOPE);
            TransactionDto transactionDto = new TransactionDto();
            transactionDto.setTxnId(UUID.randomUUID());
            Mono<IdpSendOtpResponse> idpMobileSendOtpResponse = idpClient.sendOtp(idpMobileSendOtpRequest);
            return idpMobileSendOtpResponse.flatMap(res -> HandleIdpSendOtpResponse(res, transactionDto));
        }
        return null;
    }

    private Mono<MobileOrEmailOtpResponseDto> HandleIdpSendOtpResponse(IdpSendOtpResponse idpSendOtpRes, TransactionDto transactionDto) {
        MobileOrEmailOtpResponseDto mobileOrEmailOtpResponseDto = new MobileOrEmailOtpResponseDto();
        mobileOrEmailOtpResponseDto.setTxnId("");
        mobileOrEmailOtpResponseDto.setMessage(MESSAGE);
        Mono<TransactionDto> createTransactionRes = transactionService.createTransactionEntity(transactionDto);

        return createTransactionRes.flatMap(res -> mobileOrEmailOtpResponse(res));
    }
    
    /**
     * update transaction details into database
     *
     * @param transactionDto
     */
    private Mono<MobileOrEmailOtpResponseDto> mobileOrEmailOtpResponse(TransactionDto transactionDto) {
        if (!StringUtils.isEmpty(transactionDto.getMobile())) {
            log.info("Transaction Id: "+transactionDto.getId()+" transaction : "+transactionDto.getTxnId());
            return Mono.just(MobileOrEmailOtpResponseDto.builder()
                    .txnId(transactionDto.getTxnId().toString())
                    .message(OTP_IS_SENT_TO_AADHAAR_REGISTERED_MOBILE_ENDING + Common.hidePhoneNumber(transactionDto.getMobile()))
                    .build());
        } else {
            return Mono.empty();
        }
    }
    
    public Mono<MobileOrEmailOtpResponseDto> sendOtpByIDP(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto){
        if(mobileOrEmailOtpRequestDto.getLoginHint().equalsIgnoreCase(LoginHint.ABHA_NUMBER.getValue())){
            IdpSendOtpRequest idpSendOtpRequest = new IdpSendOtpRequest();
            idpSendOtpRequest.setScope(SCOPE);
            TransactionDto transactionDto = new TransactionDto();
            transactionDto.setTxnId(UUID.randomUUID());
            Mono<IdpSendOtpResponse> idpSendOtpResponse = idpClient.sendOtp(idpSendOtpRequest);
            return idpSendOtpResponse.flatMap(idpSendOtpRes->HandleIdpSendOtpResponse(idpSendOtpRes, transactionDto));
        }

        return null;
    }

}
