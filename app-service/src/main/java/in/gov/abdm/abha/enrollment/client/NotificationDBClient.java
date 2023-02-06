package in.gov.abdm.abha.enrollment.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import in.gov.abdm.abha.enrollment.constants.URIConstant;
import reactor.core.publisher.Flux;

@Component
public class NotificationDBClient<T> {
	
    @Autowired
    private WebClient.Builder webClient;
    
    @Value("${enrollment.gateway.notificationdb.baseuri}")
    private String NOTIFICATION_DB_BASE_URI;

    private Flux<T> fluxGetDatabase(Class<T> t, String uri) {
    	//http://global2dev.abdm.gov.internal
        return webClient.baseUrl(NOTIFICATION_DB_BASE_URI)
                .build()
                .get()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToFlux(t);
    }

    public Flux<T> getAll(Class<T> t) {
        switch (t.getSimpleName()) {
            case "Templates":
                return fluxGetDatabase(t, URIConstant.NOTIFICATION_DB_GET_ALL_TEMPLATES_URI);
        }
        return Flux.empty();
    }
}
