package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.configuration.BeanConfiguration;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import java.util.List;

@ReactiveFeignClient(name= AbhaConstants.LGD_APP_CLIENT, url="${enrollment.gateway.lgd.baseuri}", configuration = BeanConfiguration.class)
public interface LgdAppFClient {

    public static final String PIN_CODE="pinCode";
    public static final String VIEW="view";

    @GetMapping(URIConstant.FLGD_BASE_URI)
    public Mono<List<LgdDistrictResponse>> getDetailsByAttribute(@RequestParam(value = PIN_CODE,required = false) String pinCode,
                                                           @RequestParam(value = VIEW,required = false) String district);
}