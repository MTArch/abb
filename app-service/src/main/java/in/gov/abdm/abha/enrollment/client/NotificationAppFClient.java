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

import static in.gov.abdm.abha.enrollment.constants.PropertyConstants.ENROLLMENT_GATEWAY_NOTIFICATION_BASEURI;
import static in.gov.abdm.abha.enrollment.constants.URIConstant.TIMESTAMP;
import static in.gov.abdm.constant.ABDMConstant.REQUEST_ID;

@ReactiveFeignClient(name= AbhaConstants.NOTIFICATION_APP_SERVICE, url=ENROLLMENT_GATEWAY_NOTIFICATION_BASEURI, configuration = BeanConfiguration.class)
public interface NotificationAppFClient {
    @PostMapping(URIConstant.NOTIFICATION_SEND_OTP_URI)
    public Mono<NotificationResponseDto> sendOtp(@RequestBody NotificationRequestDto notificationRequestDto, @RequestHeader(REQUEST_ID) String requestId,
                                                 @RequestHeader(TIMESTAMP) String timestamp);
}
