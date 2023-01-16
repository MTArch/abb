package in.gov.abdm.abha.enrollment.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.exception.database.constraint.DatabaseConstraintFailedException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AbhaDBClient<T> {

    @Autowired
    private WebClient.Builder webClient;

    @Value("${enrollment.gateway.enrollmentdb.baseuri}")
    private String ENROLLMENT_DB_BASE_URI;

    /**
     * To revert url to ->  http://abha2dev.abdm.gov.internal    after testing
     **/
    private Mono<T> GetMonoDatabase(Class<T> t, String uri) {
        return webClient.
                baseUrl(ENROLLMENT_DB_BASE_URI)
                .build()
                .get()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(t)
                .onErrorResume(error -> {
                    throw new DatabaseConstraintFailedException(((WebClientResponseException.BadRequest) error).getResponseBodyAsString());
                });
    }

    /**
     * To revert url after testing
     **/
    private Mono<T> monoPostDatabase(Class<T> t, String uri, T row) {
        return webClient.baseUrl(ENROLLMENT_DB_BASE_URI)
                .build()
                .post()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(row), t)
                .retrieve()
                .bodyToMono(t)
                .onErrorResume(error -> {
                    throw new DatabaseConstraintFailedException(((WebClientResponseException.BadRequest) error).getResponseBodyAsString());
                });
    }

    private Mono<T> fluxPostDatabase(Class<T> t, String uri, T row) {
        return webClient.baseUrl(ENROLLMENT_DB_BASE_URI)
                .build()
                .post()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(row), t)
                .retrieve()
                .bodyToMono(t)
                .onErrorResume(error -> {
                    throw new DatabaseConstraintFailedException(((WebClientResponseException.BadRequest) error).getResponseBodyAsString());
                });
    }

    private Mono<List<T>> fluxPostDatabase(Class<T> t, String uri, List<T> rows) {
        return webClient.baseUrl(ENROLLMENT_DB_BASE_URI)
                .build()
                .post()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(rows), t)
                .retrieve()
                .bodyToFlux(t)
                .collectList()
                .onErrorResume(error -> {
                    throw new DatabaseConstraintFailedException(((WebClientResponseException.BadRequest) error).getResponseBodyAsString());
                });
    }

    /**
     * To revert url after testing
     **/
    private Mono<T> monoPatchDatabase(Class<T> t, String uri, T row, String id) {
        return webClient.baseUrl(ENROLLMENT_DB_BASE_URI)
                .build()
                .patch()
                .uri(uri, id)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(row), t)
                .retrieve()
                .bodyToMono(t)
                .onErrorResume(error -> {
                    throw new DatabaseConstraintFailedException(((WebClientResponseException.BadRequest) error).getResponseBodyAsString());
                });
    }


    public Mono<T> getEntityById(Class<T> t, String id) {
        switch (t.getSimpleName()) {
            case "TransactionDto":
                return GetMonoDatabase(t, URIConstant.DB_GET_TRANSACTION_BY_TXN_ID + id);
            case "AccountDto":
                return GetMonoDatabase(t, URIConstant.DB_GET_ACCOUNT_BY_XML_UID + id);
            case "HidPhrAddressDto":
                return GetMonoDatabase(t, URIConstant.DB_GET_HID_PHR_ADDRESS_BY_HEALTH_ID_NUMBER + id);
        }
        return Mono.empty();
    }

    public Mono<T> getHidPhrAddressByPhrAddress(Class<T> t, String id) {
        switch (t.getSimpleName()) {
            case "HidPhrAddressDto":
                return GetMonoDatabase(t, URIConstant.DB_GET_HID_PHR_ADDRESS_BY_PHR_ADDRESS + id);
        }
        return Mono.empty();
    }

    public Mono<T> getAccountEntityById(Class<T> t, String id) {
        switch (t.getSimpleName()) {
            case "AccountDto":
                return GetMonoDatabase(t, URIConstant.DB_GET_ACCOUNT_BY_HEALTH_ID_NUMBER + id);
        }
        return Mono.empty();
    }

    public Mono<T> getAccountEntityByDocumentCode(Class<T> t, String documentCode) {
        return GetMonoDatabase(t, URIConstant.DB_GET_ACCOUNT_BY_DOCUMENT_CODE + documentCode);
    }

    public Mono<T> addEntity(Class<T> t, T row) {
        switch (t.getSimpleName()) {
            case "TransactionDto":
                return monoPostDatabase(t, URIConstant.DB_ADD_TRANSACTION_URI, row);
            case "AccountDto":
                return monoPostDatabase(t, URIConstant.DB_ADD_ACCOUNT_URI, row);
            case "DependentAccountRelationshipDto":
                return monoPostDatabase(t, URIConstant.DB_ADD_DEPENDENT_ACCOUNT_URI, row);
            case "HidPhrAddressDto":
                return monoPostDatabase(t, URIConstant.DB_ADD_HID_PHR_ADDRESS_URI, row);
        }
        return Mono.empty();
    }

    public Mono<T> updateEntity(Class<T> t, T row, String id) {
        switch (t.getSimpleName()) {
            case "TransactionDto":
                return monoPatchDatabase(t, URIConstant.DB_UPDATE_TRANSACTION_URI, row, id);
            case "AccountDto":
                return monoPatchDatabase(t, URIConstant.DB_UPDATE_ACCOUNT_URI, row, id);
            case "HidPhrAddressDto":
                return monoPatchDatabase(t,URIConstant.DB_UPDATE_HID_PHR_ADDRESS_BY_HID_PHR_ADDRESS_ID,row,id);
        }
        return Mono.empty();
    }

    public Mono<T> addFluxEntity(Class<T> t, T row) {
        switch (t.getSimpleName()) {
            case "DependentAccountRelationshipDto":
                return fluxPostDatabase(t, URIConstant.DB_ADD_DEPENDENT_ACCOUNT_URI, row);
        }
        return Mono.empty();
    }

    public Mono<List<T>> addFluxEntity(Class<T> t, List<T> rows) {
        return fluxPostDatabase(t, URIConstant.DB_ADD_ACCOUNT_AUTH_METHODS_ENDPOINT, rows);
    }

    public Mono<ResponseEntity<Void>> deleteDatabaseRow(Class<T> t, String uri) {
        return webClient.baseUrl(ENROLLMENT_DB_BASE_URI)
                .build()
                .delete()
                .uri(uri)
                .retrieve()
                .toBodilessEntity();
    }

    public Mono<ResponseEntity<Void>> deleteEntity(Class<T> t, String id) {
        switch (t.getSimpleName()) {
            case "TransactionDto":
                return deleteDatabaseRow(t, URIConstant.DB_DELETE_TRANSACTION_URI + id);
        }
        return Mono.empty();
    }


    public Flux<T> getFluxEntity(Class<T> t, List<String> list) {
        switch (t.getSimpleName()) {
            case "AccountDto":
                return GetFluxDatabase(t, URIConstant.DB_GET_ACCOUNTS_BY_HEALTH_ID_NUMBER_LIST + list);
        }
        return Flux.empty();
    }

    public Flux<T> GetFluxDatabase(Class<T> t, String uri) {
        return webClient.
                baseUrl(ENROLLMENT_DB_BASE_URI)
                .build()
                .get()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToFlux(t);
    }
}
