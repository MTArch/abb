package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.configuration.BeanConfiguration;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static in.gov.abdm.abha.enrollment.constants.PropertyConstants.ENROLLMENT_GATEWAY_LGD_BASEURI;
import static in.gov.abdm.abha.enrollment.constants.URIConstant.TIMESTAMP;
import static in.gov.abdm.constant.ABDMConstant.REQUEST_ID;

@ReactiveFeignClient(name = AbhaConstants.LGD_APP_CLIENT, url = ENROLLMENT_GATEWAY_LGD_BASEURI, configuration = BeanConfiguration.class)
public interface LgdAppFClient {

    public static final String PIN_CODE = "pinCode";
    public static final String VIEW = "view";
    public static final String NAME = "name";

    public static final String DISTRICT_CODE = "districtCode";

    @GetMapping(URIConstant.FLGD_BASE_URI)
    public Mono<List<LgdDistrictResponse>> getDetailsByAttribute(@RequestHeader(REQUEST_ID) UUID requestId,
                                                                 @RequestHeader(TIMESTAMP) String timestamp,
                                                                 @RequestParam(value = PIN_CODE, required = false) String pinCode,
                                                                 @RequestParam(value = VIEW, required = false) String district);

    @GetMapping(URIConstant.FLGD_STATE_SEARCH_URI)
    public Mono<List<LgdDistrictResponse>> getDetailsByAttributeState(@RequestHeader(REQUEST_ID) UUID requestId,
                                                                      @RequestHeader(TIMESTAMP) String timestamp,
                                                                      @RequestParam(value = NAME, required = false) String name);



    @GetMapping(URIConstant.FLGD_BASE_URI)
    public Mono<List<LgdDistrictResponse>> getByDistrictCode(@RequestHeader(REQUEST_ID) UUID requestId,
                                                                 @RequestHeader(TIMESTAMP) String timestamp,
                                                                 @RequestParam(value = DISTRICT_CODE, required = false) String districtCode);

}