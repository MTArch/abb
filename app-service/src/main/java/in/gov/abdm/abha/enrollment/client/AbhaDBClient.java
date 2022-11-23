package in.gov.abdm.abha.enrollment.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import in.gov.abdm.abha.enrollment.constants.EnrollErrorConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.exception.database.constraint.DatabaseConstraintFailedException;
import reactor.core.publisher.Mono;

@Component
public class AbhaDBClient<T> {

    @Autowired
    private WebClient.Builder webClient;

    @Value("${enrollment.gateway.enrollmentdb.baseuri}")
    private String ENROLLMENT_DB_BASE_URI;

    /** To revert url to ->  http://abha2dev.abdm.gov.internal    after testing **/
    private Mono<T> GetMonoDatabase(Class<T> t, String uri) {
        return webClient.
                 baseUrl(ENROLLMENT_DB_BASE_URI)
                .build()
                .get()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(t);
    }

    /** To revert url after testing **/
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
                    throw new DatabaseConstraintFailedException(EnrollErrorConstants.EXCEPTION_OCCURRED_POSTGRES_DATABASE_CONSTRAINT_FAILED_WHILE_CREATE);
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
                    throw new DatabaseConstraintFailedException(EnrollErrorConstants.EXCEPTION_OCCURRED_POSTGRES_DATABASE_CONSTRAINT_FAILED_WHILE_CREATE);
                });
    }

    /** To revert url after testing **/
    private Mono<T> monoPatchDatabase(Class<T> t, String uri, T row, String id) {
        return webClient.baseUrl(ENROLLMENT_DB_BASE_URI)
                .build()
                .patch()
                .uri(uri,id)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(row), t)
                .retrieve()
                .bodyToMono(t)
                .onErrorResume(error -> {
                    throw new DatabaseConstraintFailedException(EnrollErrorConstants.EXCEPTION_OCCURRED_POSTGRES_DATABASE_CONSTRAINT_FAILED_WHILE_UPDATE);
                });
    }


    public Mono<T> getEntityById(Class<T> t, String id) {
        switch (t.getSimpleName()) {
            case "TransactionDto":
                return GetMonoDatabase(t, URIConstant.DB_GET_TRANSACTION_BY_TXN_ID + id);
            case "AccountDto":
                return GetMonoDatabase(t, URIConstant.DB_GET_ACCOUNT_BY_XML_UID + id);
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

    public Mono<T> addEntity(Class<T> t, T row) {
        switch (t.getSimpleName()) {
            case "TransactionDto":
                return monoPostDatabase(t, URIConstant.DB_ADD_TRANSACTION_URI, row);
            case "AccountDto":
                return monoPostDatabase(t, URIConstant.DB_ADD_ACCOUNT_URI, row);
            case "DependentAccountRelationshipDto":
                return monoPostDatabase(t, URIConstant.DB_ADD_DEPENDENT_ACCOUNT_URI,row);
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
        }
        return Mono.empty();
    }

    public Mono<T> addFluxEntity(Class<T> t, T row) {
        switch (t.getSimpleName()) {
            case "DependentAccountRelationshipDto":
                return fluxPostDatabase(t, URIConstant.DB_ADD_DEPENDENT_ACCOUNT_URI,row);
        }
        return Mono.empty();
    }
}
