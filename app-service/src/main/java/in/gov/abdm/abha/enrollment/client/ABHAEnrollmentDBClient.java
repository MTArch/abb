package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.constants.ABHAEnrollmentConstant;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Component
public class ABHAEnrollmentDBClient<T> {

    /**
     *
     */
    @Autowired
    private DiscoveryClient discoveryClient;

    /**
     *
     */
    @Autowired
    private WebClient.Builder webClient;

    /**
     *
     */
    @Value("${abdm.abha.enrollment.db.service}")
    private String abhaEnrollmentDBService;

    /**
     * @return
     */
    private Optional<URI> getDBURI() {
        return discoveryClient
                .getInstances(abhaEnrollmentDBService)
                .stream()
                .findFirst()
                .map(ServiceInstance::getUri);
    }

//    private Flux<T> fluxGetDatabase(Class<T> t, String uri) {
//        return webClient.
//                baseUrl(
//                        this.getDBURI()
//                                .isPresent() ?
//                                this.getDBURI().get().toString() : "NA")
//                .build()
//                .get()
//                .uri(uri)
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .retrieve()
//                .bodyToFlux(t);
//    }

    private Mono<T> GetMonoDatabase(Class<T> t, String uri) {
        return webClient.
                baseUrl(
                        this.getDBURI()
                                .isPresent() ?
                                this.getDBURI().get().toString() : "NA")
                .build()
                .get()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(t);
    }

    private Mono<T> monoPostDatabase(Class<T> t, String uri, T row) {
        return webClient.baseUrl(
                        this.getDBURI().isPresent() ?
                                this.getDBURI().get().toString() : "NA")
                .build()
                .post()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(row), t)
                .retrieve()
                .bodyToMono(t);
    }


    public Mono<T> getEntityById(Class<T> t, String id) {
        switch (t.getSimpleName()) {
            case "TransactionDto":
                return GetMonoDatabase(t, ABHAEnrollmentConstant.DB_GET_TRANSACTION_BY_TXN_ID+id);
        }
        return Mono.empty();
    }

    public Mono<T> addEntity(Class<T> t, T row) {
        switch (t.getSimpleName()) {
            case "TransactionDto":
                return monoPostDatabase(t, ABHAEnrollmentConstant.DB_ADD_TRANSACTION_URI, row);
            case "AccountDto":
                return monoPostDatabase(t, ABHAEnrollmentConstant.DB_ADD_ACCOUNT_URI, row);
        }
        return Mono.empty();
    }
}
