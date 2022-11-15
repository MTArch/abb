package in.gov.abdm.abha.enrollment.services.idp;

import in.gov.abdm.abha.enrollment.client.IDPClient;
import in.gov.abdm.abha.enrollment.enums.LoginHint;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.idp.IdpSendOtpRequest;
import in.gov.abdm.abha.enrollment.model.idp.IdpSendOtpResponse;
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

import static in.gov.abdm.abha.enrollment.services.otp_request.impl.OtpRequestServiceImpl.OTP_IS_SENT_TO_AADHAAR_REGISTERED_MOBILE_ENDING;
@Slf4j
@Service
/**
 * It is Service class IdpService
 */
public class IdpService {

    public static final String SCOPE = "OTP";
    public static final String MESSAGE = "OTP is sent to Aadhaar/ABHA registered mobile ending xxx3604";
    @Autowired
    IDPClient idpClient;
    @Autowired
    TransactionService transactionService;

    public Mono<MobileOrEmailOtpResponseDto> sendOtpByIDP(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto){
        if(mobileOrEmailOtpRequestDto.getLoginHint().equalsIgnoreCase(LoginHint.ABHA_NUMBER.getValue())){
            IdpSendOtpRequest idpSendOtpRequest = new IdpSendOtpRequest();
            idpSendOtpRequest.setScope(SCOPE);
            TransactionDto transactionDto = new TransactionDto();
            transactionDto.setTxnId(UUID.randomUUID());
            Mono<IdpSendOtpResponse> idpSendOtpResponse = idpClient.sendOtp(idpSendOtpRequest);
            System.out.println("Inside IDPService.sendOtpByIDP if Condition");
            return idpSendOtpResponse.flatMap(idpSendOtpRes->HandleIdpSendOtpResponse(idpSendOtpRes, transactionDto));
        }

        System.out.println("Inside IDPService.sendOtpByIDP if Condition");
        return null;
    }

    private Mono<MobileOrEmailOtpResponseDto> HandleIdpSendOtpResponse(IdpSendOtpResponse idpSendOtpRes, TransactionDto transactionDto) {
        MobileOrEmailOtpResponseDto mobileOrEmailOtpResponseDto = new MobileOrEmailOtpResponseDto();
        mobileOrEmailOtpResponseDto.setTxnId("");
        mobileOrEmailOtpResponseDto.setMessage(MESSAGE);
        System.out.println("Inside IDPService.HandleIdpSendOtpResponse");
        Mono<TransactionDto> createTransactionRes = transactionService.createTransactionEntity(transactionDto);
       // return Mono.just(mobileOrEmailOtpResponseDto);

        return createTransactionRes.flatMap(res -> mobileOrEmailOtpResponse(res, transactionDto));
    }

    /**
     * update transaction details into database by calling abha db service
     *
     * @param transactionDto
     */
    private Mono<MobileOrEmailOtpResponseDto> mobileOrEmailOtpResponse(TransactionDto createTransactionRes, TransactionDto transactionDto) {
        if (!StringUtils.isEmpty(createTransactionRes.getAadharNo())) {
            log.info("Transaction Id: "+createTransactionRes.getId()+" transaction : "+createTransactionRes.getTxnId());
            return Mono.just(MobileOrEmailOtpResponseDto.builder()
                    .txnId(transactionDto.getTxnId().toString())
                    .message(OTP_IS_SENT_TO_AADHAAR_REGISTERED_MOBILE_ENDING + Common.hidePhoneNumber(transactionDto.getMobile()))
                    .build());
        } else {
            return Mono.empty();
        }
    }


}
