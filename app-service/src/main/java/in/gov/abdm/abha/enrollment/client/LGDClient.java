package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.constants.PropertyConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.exception.lgd.LgdGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class LGDClient {

    public static final String SEARCH = "/search";
    public static final String PIN_CODE = "pinCode";
    public static final String VIEW = "view";
    public static final String DISTRICT = "district";
    public static final String FAILED_TO_COMMUNICATE_WITH_LGD_SERVICE = "Failed to Communicate with LGD service";
    @Autowired
    private WebClient.Builder webClient;

    @Value(PropertyConstants.ENROLLMENT_GATEWAY_LGD_BASEURI)
    private String LGD_SERVICE_BASE_URI;

    public Mono<List<LgdDistrictResponse>> getLgdDistrictDetails(String pinCode) {

        String uri = UriComponentsBuilder.fromPath(SEARCH)
                .queryParam(PIN_CODE,pinCode)
                .queryParam(VIEW, DISTRICT).toUriString();

    	return webClient.baseUrl(LGD_SERVICE_BASE_URI)
                .build()
                .get()
                .uri( URIConstant.LGD_BASE_URI+uri)
                .retrieve()
                .bodyToFlux(LgdDistrictResponse.class)
                .collectList()
                .onErrorResume(error -> {throw new LgdGatewayUnavailableException();});
    }
}
