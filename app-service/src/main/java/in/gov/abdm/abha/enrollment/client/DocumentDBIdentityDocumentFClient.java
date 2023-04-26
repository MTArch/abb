package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.configuration.BeanConfiguration;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.entities.IdentityDocumentsDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import static in.gov.abdm.abha.enrollment.constants.PropertyConstants.ENROLLMENT_GATEWAY_DOCUMENTDB_BASEURI;

@ReactiveFeignClient(name= AbhaConstants.   DOCUMENT_DB_IDENTITY_DOCUMENT_CLIENT, url=ENROLLMENT_GATEWAY_DOCUMENTDB_BASEURI, configuration = BeanConfiguration.class)
public interface DocumentDBIdentityDocumentFClient {

    @PostMapping(URIConstant.IDENTITY_DOCUMENT_ADD)
    public Mono<IdentityDocumentsDto> addIdentityDocuments(@RequestBody IdentityDocumentsDto identityDocumentsDto);
    @GetMapping(URIConstant.IDENTITY_DOCUMENT_GET)
    Mono<IdentityDocumentsDto> getIdentityDocuments(@PathVariable("healthId") String healthIdNumber);

}
