package in.gov.abdm.abha.enrollmentdb.repository;

import in.gov.abdm.abha.enrollmentdb.model.accountauthmethods.AccountAuthMethods;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountAuthMethodsRepository extends R2dbcRepository<AccountAuthMethods, String> {
}

