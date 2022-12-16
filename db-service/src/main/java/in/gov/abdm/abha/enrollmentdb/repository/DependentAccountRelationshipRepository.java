package in.gov.abdm.abha.enrollmentdb.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationship;

@Repository
public interface DependentAccountRelationshipRepository
		extends ReactiveCrudRepository<DependentAccountRelationship, Long> {

}
