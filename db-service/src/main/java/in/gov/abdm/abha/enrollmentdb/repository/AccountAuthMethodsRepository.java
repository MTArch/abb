package in.gov.abdm.abha.enrollmentdb.repository;

import in.gov.abdm.abha.enrollmentdb.model.accountauthmethods.AccountAuthMethods;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AccountAuthMethodsRepository extends ReactiveCrudRepository<AccountAuthMethods, String> {
}

