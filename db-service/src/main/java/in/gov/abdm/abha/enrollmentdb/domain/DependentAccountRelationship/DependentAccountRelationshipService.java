package in.gov.abdm.abha.enrollmentdb.domain.DependentAccountRelationship;


import in.gov.abdm.abha.enrollmentdb.model.account.AccountDto;
import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationshipDto;
import reactor.core.publisher.Mono;

/**
 * Service layer to perform crud operations on Dependent Account Relationship Entity
 */
public interface DependentAccountRelationshipService {

    /**
     * to fetch dependent account relationship details by id
     *
     * @param id
     * @return
     */

    Mono<DependentAccountRelationshipDto> getDependentAccountRelationshipDetailById(Long id);

    /**
     * to update dependent account relationship details by Id
     *
     * @param dependentAccountRelationshipDto
     * @param id
     * @return
     */
    Mono updateDependentAccountRelationshipDetailById(DependentAccountRelationshipDto dependentAccountRelationshipDto, Long id);

    /**
     * to delete dependent account relationship details by Id
     * @param dependentAccountRelationshipDto
     * @param id
     * @return
     */
    Mono deleteDependentAccountRelationshipDetailById(DependentAccountRelationshipDto dependentAccountRelationshipDto, Long id);

}
