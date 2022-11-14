package in.gov.abdm.abha.enrollment.services.idp;

import in.gov.abdm.abha.enrollment.client.IDPClient;
import in.gov.abdm.abha.enrollment.enums.LoginHint;
import in.gov.abdm.abha.enrollment.model.idp.IdpSendOtpRequest;
import in.gov.abdm.abha.enrollment.model.idp.IdpSendOtpResponse;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class IdpService {

    public static final String SCOPE = "OTP";
    public static final String MESSAGE = "OTP is sent to Aadhaar/ABHA registered mobile ending xxx3604";
    @Autowired
    IDPClient idpClient;

    public Mono<MobileOrEmailOtpResponseDto> sendOtpByIDP(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto){
        if(mobileOrEmailOtpRequestDto.getLoginHint().equalsIgnoreCase(LoginHint.ABHA_NUMBER.getValue())){
            IdpSendOtpRequest idpSendOtpRequest = new IdpSendOtpRequest();
            idpSendOtpRequest.setScope(SCOPE);
            Mono<IdpSendOtpResponse> idpSendOtpResponse = idpClient.sendOtp(idpSendOtpRequest);
            return idpSendOtpResponse.flatMap(res->HandleIdpSendOtpResponse(res));
            //return idpSendOtpResponse.flatMap(idpSendOtpResponse1 -> HandleIdpSendOtpResponse(idpSendOtpResponse1));
        }
        return null;
    }

    private Mono<MobileOrEmailOtpResponseDto> HandleIdpSendOtpResponse(IdpSendOtpResponse idpSendOtpResponse) {
        MobileOrEmailOtpResponseDto mobileOrEmailOtpResponseDto = new MobileOrEmailOtpResponseDto();
        mobileOrEmailOtpResponseDto.setTxnId("");
        mobileOrEmailOtpResponseDto.setMessage(MESSAGE);
        return Mono.just(mobileOrEmailOtpResponseDto);
    }
}
