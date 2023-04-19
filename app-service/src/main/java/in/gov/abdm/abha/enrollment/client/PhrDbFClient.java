package in.gov.abdm.abha.enrollment.client;
import in.gov.abdm.abha.enrollment.configuration.BeanConfiguration;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.phr.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import java.sql.Timestamp;

@ReactiveFeignClient(name = AbhaConstants.PHR_APP_CLIENT, url = "${enrollment.gateway.phr.baseuri}", configuration = BeanConfiguration.class)
public interface PhrDbFClient {

    public static final String REQUEST_ID = "REQUEST_ID";
    public static final String TIMESTAMP = "TIMESTAMP";
    @GetMapping(URIConstant.GET_USERS_BY_ABHA_ADDRESS_LIST_URI)
    Flux<User> getUsersByAbhaAddressList(
            @RequestHeader(REQUEST_ID) String requestId,
            @RequestHeader(TIMESTAMP) Timestamp timestamp,
            @RequestParam("abhaAddress") String abhaAddressList);
}
