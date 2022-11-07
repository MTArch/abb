package in.gov.abdm.abha.enrollmentdb.domain.DependentAccountRelationship;

import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationshipDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Service layer to perform crud operations on DependentAccountRelationship Entity
 */
public interface DependentAccountRelationshipService {


    /**
     * to add new account
     *
     * @param dependentAccountRelationshipDto
     * @return
     */
    Mono addAccount(DependentAccountRelationshipDto dependentAccountRelationshipDto);



    /**
     * to fetch all dependent account relationship details
     * @return
     */
    Flux<DependentAccountRelationshipDto> getAllDependentAccountRelationship();

}
