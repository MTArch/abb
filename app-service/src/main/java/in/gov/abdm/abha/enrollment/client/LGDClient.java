package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import in.gov.abdm.abha.enrollment.model.notification.NotificationResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Component
public class LGDClient {

    public static final String SEARCH = "/search";
    public static final String PIN_CODE = "pinCode";
    public static final String VIEW = "view";
    public static final String DISTRICT = "district";
    @Autowired
    private WebClient.Builder webClient;

    @Value("${enrollment.gateway.lgd.baseuri}")
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
                .collectList();
    }
}
