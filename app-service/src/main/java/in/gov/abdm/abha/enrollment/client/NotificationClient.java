package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.notification.NotificationRequestDto;
import in.gov.abdm.abha.enrollment.model.notification.NotificationResponseDto;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class NotificationClient {
    @Autowired
    private WebClient.Builder webClient;

    @Value("${enrollment.gateway.notification.baseuri}")
    private String NOTIFICATION_SERVICE_BASE_URI;

    public Mono<NotificationResponseDto> sendOtp(NotificationRequestDto notificationRequestDto) {
        return webClient.baseUrl(NOTIFICATION_SERVICE_BASE_URI)
                .build()
                .post()
                .uri(URIConstant.NOTIFICATION_SEND_OTP_URI)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("X-REQUEST-ID", String.valueOf(RandomUtil.getRandomNumber(5)))
                .header("TIMESTAMP", Common.getTimeStamp(true))
                .body(BodyInserters.fromValue(notificationRequestDto))
                .retrieve()
                .bodyToMono(NotificationResponseDto.class);
    }
}
