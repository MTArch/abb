package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.constants.EnrollConstant;
import in.gov.abdm.abha.enrollment.model.notification.NotificationRequestDto;
import in.gov.abdm.abha.enrollment.model.notification.NotificationResponseDto;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class NotificationClient {
    @Autowired
    private WebClient.Builder webClient;

    public Mono<NotificationResponseDto> sendOtp(NotificationRequestDto notificationRequestDto) {
        return webClient.baseUrl(EnrollConstant.NOTIFICATION_ENDPOINT_URI)
                .build()
                .post()
                .uri(EnrollConstant.NOTIFICATION_SEND_OTP_URI)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(notificationRequestDto))
                .retrieve()
                .bodyToMono(NotificationResponseDto.class);
    }
}
