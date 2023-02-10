package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.configuration.BeanConfiguration;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.notification.NotificationRequestDto;
import in.gov.abdm.abha.enrollment.model.notification.NotificationResponseDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import static in.gov.abdm.abha.enrollment.constants.URIConstant.REQUEST_ID;
import static in.gov.abdm.abha.enrollment.constants.URIConstant.TIMESTAMP;

@ReactiveFeignClient(name= AbhaConstants.NOTIFICATION_APP_SERVICE, url="${enrollment.gateway.notification.baseuri}", configuration = BeanConfiguration.class)
public interface NotificationAppFClient {
    @PostMapping(URIConstant.NOTIFICATION_SEND_OTP_URI)
    public Mono<NotificationResponseDto> sendOtp(@RequestBody NotificationRequestDto notificationRequestDto, @RequestHeader(REQUEST_ID) String requestId,
                                                 @RequestHeader(TIMESTAMP) String timestamp);
}
