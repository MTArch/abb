package in.gov.abdm.abha.enrollmentdb.repository;
import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationship;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface DependentAccountRelationshipRepository extends ReactiveCrudRepository<DependentAccountRelationship,Long> {

    @Query("DELETE FROM dependent_account_relationship u where u.id = :id")
    public Mono<Boolean> deleteByPk(@Param("id") Long id);

}
