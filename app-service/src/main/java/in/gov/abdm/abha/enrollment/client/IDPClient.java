package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.constants.ABHAEnrollmentConstant;
import in.gov.abdm.abha.enrollment.model.authbyabdm.AuthByAbdmResponse;
import in.gov.abdm.abha.enrollment.model.authbyabdm.AuthData;
import in.gov.abdm.abha.enrollment.model.authbyabdm.Otp;
import in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse.IdpVerifyOtpResponse;
import in.gov.abdm.abha.enrollment.model.idp.sendotp.IdpSendOtpRequest;
import in.gov.abdm.abha.enrollment.model.idp.sendotp.IdpSendOtpResponse;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class IDPClient<T> {

    @Autowired
    private WebClient.Builder webClient;
    @Autowired
    AuthByAbdmResponse authByAbdmResponse;
    @Autowired
    AuthData authData;
    @Autowired
    Otp otp;

    //call to IDP service
    public Mono<IdpSendOtpResponse> sendOtp(IdpSendOtpRequest idpSendOtpRequest) {
        //global2dev.abdm.gov.internal/api/v3/identity/authentication
        return webClient.baseUrl("http://global2dev.abdm.gov.internal")
                .build()
                .post()
                .uri(ABHAEnrollmentConstant.IDP_SEND_OTP_URI)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(idpSendOtpRequest))
                .retrieve()
                .bodyToMono(IdpSendOtpResponse.class);
    }
    public Mono<IdpVerifyOtpResponse> verifyOtp(String authorization, String xTransactionId, String hipRequestId, String requestId) {
        //http://global2dev.abdm.gov.internal/api/v3/identity/verify
        return webClient.baseUrl("http://global2dev.abdm.gov.internal")
                .build()
                .post()
                .uri(ABHAEnrollmentConstant.IDP_VERIFY_OTP_URI)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .header("xTransactionId",xTransactionId)
                .header("hipRequestId",hipRequestId)
                .header("requestId", requestId)
                .retrieve()
                .bodyToMono(IdpVerifyOtpResponse.class);
    }
}
