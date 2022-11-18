package in.gov.abdm.abha.enrollment.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.notification.NotificationRequestDto;
import in.gov.abdm.abha.enrollment.model.notification.NotificationResponseDto;
import reactor.core.publisher.Mono;

@Component
public class NotificationClient {
	
    @Autowired
    private WebClient.Builder webClient;
    
    @Value("${enrollment.gateway.notification.baseuri}")
    private String NOTIFICATION_SERVICE_BASE_URI;

    public Mono<NotificationResponseDto> sendOtp(NotificationRequestDto notificationRequestDto) {
    	// http://global2dev.abdm.gov.internal
        return webClient.baseUrl(NOTIFICATION_SERVICE_BASE_URI)
                .build()
                .post()
                .uri(URIConstant.NOTIFICATION_SEND_OTP_URI)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(notificationRequestDto))
                .retrieve()
                .bodyToMono(NotificationResponseDto.class);
    }
}
