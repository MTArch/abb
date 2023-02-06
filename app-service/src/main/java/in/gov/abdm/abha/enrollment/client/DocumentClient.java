package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.exception.document.DocumentDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.document.DocumentGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.entities.IdentityDocumentsDto;
import in.gov.abdm.abha.enrollment.model.nepix.VerifyDLRequest;
import in.gov.abdm.abha.enrollment.model.nepix.VerifyDLResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class DocumentClient {

    public static final String SEARCH = "/search";
    public static final String PIN_CODE = "pinCode";
    public static final String VIEW = "view";
    public static final String DISTRICT = "district";
    @Autowired
    private WebClient.Builder webClient;

    @Value("${enrollment.gateway.document.baseuri}")
    private String DOCUMENT_SERVICE_BASE_URI;

    @Value("${enrollment.gateway.documentdb.baseuri}")
    private String DOCUMENT_DB_SERVICE_BASE_URI;

    /**
     * To revert url after testing
     **/
    private Mono<IdentityDocumentsDto> monoPostDatabase(String uri, IdentityDocumentsDto row) {
        return webClient.baseUrl(DOCUMENT_DB_SERVICE_BASE_URI)
                .build()
                .post()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(row))
                .retrieve()
                .bodyToMono(IdentityDocumentsDto.class)
                .onErrorResume(error -> {
                    throw new DocumentDBGatewayUnavailableException();
                });
    }

    private Mono<IdentityDocumentsDto> GetMonoDatabase(Class<IdentityDocumentsDto> t, String uri) {
        return webClient.
                baseUrl(DOCUMENT_DB_SERVICE_BASE_URI)
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

    public Mono<VerifyDLResponse> verify(VerifyDLRequest verifyDLRequest) {
        return webClient.baseUrl(DOCUMENT_SERVICE_BASE_URI)
                .build()
                .post()
                .uri(URIConstant.DOCUMENT_VERIFY)
                .body(BodyInserters.fromValue(verifyDLRequest))
                .retrieve()
                .bodyToMono(VerifyDLResponse.class)
                .onErrorResume(error -> {
                    throw new DocumentGatewayUnavailableException();
                });
    }

    public Mono<IdentityDocumentsDto> addIdentityDocuments(IdentityDocumentsDto row) {
        return monoPostDatabase(URIConstant.IDENTITY_DOCUMENT_ADD, row);
    }

    public Mono<IdentityDocumentsDto> getIdentityDocuments(String healthid) {
        return GetMonoDatabase(IdentityDocumentsDto.class,URIConstant.IDENTITY_DOCUMENT_GET+healthid);
    }
}
