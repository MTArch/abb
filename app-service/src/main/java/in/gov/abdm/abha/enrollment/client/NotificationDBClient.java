package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.constants.URIConstant;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class NotificationDBClient<T> {
    @Autowired
    private WebClient.Builder webClient;

    private Flux<T> fluxGetDatabase(Class<T> t, String uri) {
        return webClient.baseUrl(URIConstant.NOTIFICATION_DB_ENDPOINT_URI)
                .build()
                .get()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToFlux(t);
    }

    public Flux<T> getAll(Class<T> t) {
        switch (t.getSimpleName()) {
            case "Template":
                return fluxGetDatabase(t, URIConstant.NOTIFICATION_DB_GET_ALL_TEMPLATES_URI);
        }
        return Flux.empty();
    }
}
