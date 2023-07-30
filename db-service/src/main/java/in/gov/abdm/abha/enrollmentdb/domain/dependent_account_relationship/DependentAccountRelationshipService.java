package in.gov.abdm.abha.enrollmentdb.domain.dependent_account_relationship;

import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationship;
import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationshipDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


/**
 * Service layer to perform crud operations on DependentAccountRelationship Entity
 */
public interface DependentAccountRelationshipService {


    Mono<DependentAccountRelationshipDto> linkDependentAccountRelationships(List<DependentAccountRelationshipDto> dependentAccountRelationshipDtoList);


    /**
     * to fetch all dependent account relationship details
     *
     * @return
     */
    Flux<DependentAccountRelationshipDto> getAllDependentAccountRelationship();

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
    Mono<DependentAccountRelationship> updateDependentAccountRelationshipDetailById(DependentAccountRelationshipDto dependentAccountRelationshipDto, Long id);

    /**
     * to delete dependent account relationship details by Id
     *
     * @param dependentAccountRelationshipDto
     * @param id
     * @return
     */
    Mono<Void> deleteDependentAccountRelationshipDetailById(DependentAccountRelationshipDto dependentAccountRelationshipDto, Long id);

}
