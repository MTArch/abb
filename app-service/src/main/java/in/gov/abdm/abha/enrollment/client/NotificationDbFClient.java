package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.configuration.AppConfigurations;
import in.gov.abdm.abha.enrollment.configuration.BeanConfiguration;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.notification.template.Templates;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;

import static in.gov.abdm.abha.enrollment.constants.PropertyConstants.ENROLLMENT_GATEWAY_NOTIFICATIONDB_BASEURI;
import static in.gov.abdm.abha.enrollment.constants.URIConstant.TIMESTAMP;
import static in.gov.abdm.constant.ABDMConstant.REQUEST_ID;

@ReactiveFeignClient(name= AbhaConstants.NOTIFICATION_DB_SERVICE, url=ENROLLMENT_GATEWAY_NOTIFICATIONDB_BASEURI, configuration = AppConfigurations.class)
public interface NotificationDbFClient {
    @GetMapping (URIConstant.NOTIFICATION_DB_GET_ALL_TEMPLATES_URI)
    public Flux<Templates> getAll(@RequestHeader(REQUEST_ID) String requestId,
                                  @RequestHeader(TIMESTAMP) String timestamp);
}
