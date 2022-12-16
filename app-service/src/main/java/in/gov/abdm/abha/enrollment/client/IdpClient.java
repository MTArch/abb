package in.gov.abdm.abha.enrollment.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse.IdpVerifyOtpRequest;
import in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse.IdpVerifyOtpResponse;
import in.gov.abdm.abha.enrollment.model.idp.sendotp.IdpSendOtpRequest;
import in.gov.abdm.abha.enrollment.model.idp.sendotp.IdpSendOtpResponse;
import reactor.core.publisher.Mono;

@Component
public class IdpClient {

    @Autowired
    private WebClient.Builder webClient;

    @Value("${enrollment.gateway.idp.baseuri}")
    private String IDP_SERVICE_BASE_URI;

    //call to IDP service
    public Mono<IdpSendOtpResponse> sendOtp(IdpSendOtpRequest idpSendOtpRequest,String authorization,String timestamp,String hipRequestId,String requestId) {
    	// http://global2dev.abdm.gov.internal
        return webClient.baseUrl(IDP_SERVICE_BASE_URI)
                .build()
                .post()
                .uri(URIConstant.IDP_SEND_OTP_URI)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .header("timestamp",timestamp)
                .header("hipRequestId",hipRequestId)
                .header("requestId", requestId)
                .body(BodyInserters.fromValue(idpSendOtpRequest))
                .retrieve()
                .bodyToMono(IdpSendOtpResponse.class);
    }
    public Mono<IdpVerifyOtpResponse> verifyOtp(IdpVerifyOtpRequest idpVerifyOtpRequest, String authorization, String xTransactionId, String hipRequestId, String requestId) {
        return webClient.baseUrl(IDP_SERVICE_BASE_URI)
                .build()
                .post()
                .uri(URIConstant.IDP_VERIFY_OTP_URI)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .header("xTransactionId",xTransactionId)
                .header("hipRequestId",hipRequestId)
                .header("requestId", requestId)
                .body(BodyInserters.fromValue(idpVerifyOtpRequest))
                .retrieve()
                .bodyToMono(IdpVerifyOtpResponse.class)
                .onErrorResume(error -> {
                	return Mono.just(new IdpVerifyOtpResponse());
                });
    }
}
