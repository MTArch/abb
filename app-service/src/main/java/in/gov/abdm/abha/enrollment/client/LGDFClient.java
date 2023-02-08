package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.configuration.BeanConfiguration;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import java.util.List;

@ReactiveFeignClient(name="LGD-service-client", url="${enrollment.gateway.lgd.baseuri}", configuration = BeanConfiguration.class)
public interface LGDFClient {

    public static final String PIN_CODE="pinCode";
    public static final String VIEW="view";

    @GetMapping(URIConstant.LGD_BASE_URI+"{/search}")
    public Mono<List<LgdDistrictResponse>> getDetailsByAttribute(@RequestParam(value = PIN_CODE,required = false) String pinCode,
                                                           @RequestParam(value = VIEW,required = false) String district);
}
