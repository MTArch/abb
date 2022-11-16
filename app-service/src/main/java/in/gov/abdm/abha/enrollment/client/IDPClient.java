package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.constants.ABHAEnrollmentConstant;
import in.gov.abdm.abha.enrollment.model.idp.IdpSendOtpRequest;
import in.gov.abdm.abha.enrollment.model.idp.IdpSendOtpResponse;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class IDPClient {

    @Autowired
    private WebClient.Builder webClient;

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
}
