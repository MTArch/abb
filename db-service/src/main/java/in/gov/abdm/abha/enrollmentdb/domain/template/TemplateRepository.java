package in.gov.abdm.abha.enrollmentdb.domain.template;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface TemplateRepository extends ReactiveCrudRepository<Template, BigInteger> {
}
