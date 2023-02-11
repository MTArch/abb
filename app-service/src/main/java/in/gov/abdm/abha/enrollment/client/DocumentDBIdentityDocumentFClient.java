package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.configuration.BeanConfiguration;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.entities.IdentityDocumentsDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name= AbhaConstants.   DOCUMENT_DB_IDENTITY_DOCUMENT_CLIENT, url="${enrollment.gateway.documentdb.baseuri}", configuration = BeanConfiguration.class)
public interface DocumentDBIdentityDocumentFClient {

    @PostMapping(URIConstant.IDENTITY_DOCUMENT_ADD)
    public Mono<IdentityDocumentsDto> addIdentityDocuments(@RequestBody IdentityDocumentsDto identityDocumentsDto);
}
