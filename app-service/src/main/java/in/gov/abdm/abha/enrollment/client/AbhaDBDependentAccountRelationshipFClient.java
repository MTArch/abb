package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.configuration.BeanConfiguration;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.entities.DependentAccountRelationshipDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import java.util.List;

@ReactiveFeignClient(name= AbhaConstants.ABHA_DB_DEPENDENT_ACCOUNT_RELATIONSHIP_CLIENT, url="${enrollment.gateway.enrollmentdb.baseuri}", configuration = BeanConfiguration.class)
public interface AbhaDBDependentAccountRelationshipFClient {

    @PostMapping(URIConstant.DB_ADD_DEPENDENT_ACCOUNT_URI)
    public Mono<DependentAccountRelationshipDto> createDependentRelationships(@RequestBody List<DependentAccountRelationshipDto> dependentAccountRelationshipDtoList);

    @PostMapping(URIConstant.DB_ADD_DEPENDENT_ACCOUNT_URI)
    public Mono<DependentAccountRelationshipDto> addFluxEntity(@RequestBody List<DependentAccountRelationshipDto> dependentAccountRelationshipDtoList);


}