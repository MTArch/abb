package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.configuration.BeanConfiguration;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.entities.IdentityDocumentsDto;
import org.springframework.web.bind.annotation.PostMapping;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name="document-db-client", url="${enrollment.gateway.documentdb.baseuri}", configuration = BeanConfiguration.class)
public interface DocumentDBFClient {

    @PostMapping(URIConstant.IDENTITY_DOCUMENT_ADD)
    public Mono<IdentityDocumentsDto> addIdentityDocuments(IdentityDocumentsDto identityDocumentsDto);
}
