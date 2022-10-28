package in.gov.abdm.abha.enrollment.client;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import in.gov.abdm.abha.enrollment.constants.ABHAEnrollmentConstant;
import reactor.core.publisher.Mono;

@Component
public class ABHAEnrollmentDBClient<T> {

    /**
     *
     */
    @Autowired
    private WebClient.Builder webClient;


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
                baseUrl("http://abha2dev.abdm.gov.internal")
                .build()
                .get()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(t);
    }

    private Mono<T> monoPostDatabase(Class<T> t, String uri, T row) {
        return webClient.baseUrl("http://abha2dev.abdm.gov.internal")
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
