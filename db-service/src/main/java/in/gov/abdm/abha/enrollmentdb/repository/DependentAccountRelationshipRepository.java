package in.gov.abdm.abha.enrollmentdb.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationship;

@Repository
public interface DependentAccountRelationshipRepository
		extends R2dbcRepository<DependentAccountRelationship, Long> {

}
