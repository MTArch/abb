package in.gov.abdm.abha.enrollment.services.auth_byabdm.impl;

import in.gov.abdm.abha.enrollment.client.IDPClient;
import in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse.IdpVerifyOtpResponse;
import in.gov.abdm.abha.enrollment.services.auth_byabdm.AuthByAbdmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthByAbdmServiceImpl<T> implements AuthByAbdmService<T> {
	
    @Autowired
    IDPClient idpClient;
    
    @Override
    public Mono<IdpVerifyOtpResponse> verifyOtp() {
        Class<T> t; String authorization; String xTransactionId; String hipRequestId; String requestId;
        Mono<IdpVerifyOtpResponse> idpVerifyOtpResponse = idpClient.verifyOtp("authorization", "xTransactionId", "hipRequestId", "requestId");
        return idpVerifyOtpResponse;
    }
}
