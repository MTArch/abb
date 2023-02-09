package in.gov.abdm.abha.enrollment.services.idp;

import in.gov.abdm.abha.enrollment.client.IdpAppFClient;
import in.gov.abdm.abha.enrollment.exception.idp.IdpGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse.IdpVerifyOtpRequest;
import in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse.IdpVerifyOtpResponse;
import in.gov.abdm.abha.enrollment.model.idp.sendotp.IdpSendOtpRequest;
import in.gov.abdm.abha.enrollment.model.idp.sendotp.IdpSendOtpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class IdpAppService {
    @Autowired
    IdpAppFClient idpAppFClient;

    public Mono<IdpSendOtpResponse> sendOtp(IdpSendOtpRequest idpSendOtpRequest, String authorization, String timestamp, String hipRequestId, String requestId) {
        return idpAppFClient.sendOtp(idpSendOtpRequest, authorization, timestamp, hipRequestId, requestId).doOnError(throwable -> Mono.error(new IdpGatewayUnavailableException()));
    }

    public Mono<IdpVerifyOtpResponse> verifyOtp(IdpVerifyOtpRequest idpVerifyOtpRequest, String authorization, String timeStamp, String hipRequestId, String requestId) {
        return idpAppFClient.verifyOtp(idpVerifyOtpRequest, authorization, timeStamp, hipRequestId, requestId).doOnError(throwable -> Mono.error(new IdpGatewayUnavailableException()));
    }
}
