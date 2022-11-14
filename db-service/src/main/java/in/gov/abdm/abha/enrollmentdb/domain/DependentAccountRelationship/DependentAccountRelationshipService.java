package in.gov.abdm.abha.enrollmentdb.domain.DependentAccountRelationship;

import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationshipDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


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
    Mono addDependentAccountRelationship(DependentAccountRelationshipDto dependentAccountRelationshipDto);


    Flux linkDependentAccountRelationships(List<DependentAccountRelationshipDto> dependentAccountRelationshipDtoList);


    /**
     * to fetch all dependent account relationship details
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
    Mono updateDependentAccountRelationshipDetailById(DependentAccountRelationshipDto dependentAccountRelationshipDto, Long id);

    /**
     * to delete dependent account relationship details by Id
     * @param dependentAccountRelationshipDto
     * @param id
     * @return
     */
    Mono deleteDependentAccountRelationshipDetailById(DependentAccountRelationshipDto dependentAccountRelationshipDto, Long id);

}
