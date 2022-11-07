package in.gov.abdm.abha.enrollmentdb.repository;

import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationship;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface DependentAccountRelationshipRepository extends ReactiveCrudRepository<DependentAccountRelationship,Long> {
    @Query(value = "SELECT max(id) FROM dependent_account_relationship")
    Mono<Long> getMaxId();
}
