package in.gov.abdm.abha.enrollmentdb.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.gov.abdm.abha.enrollmentdb.model.accountauthmethods.AccountAuthMethods;
import reactor.core.publisher.Mono;

@Repository
public interface AccountAuthMethodsRepository extends R2dbcRepository<AccountAuthMethods, String> {

    @Query("INSERT INTO account_auth_methods(health_id_number, auth_methods) " +
            "VALUES (:#{#accountAuthMethods.healthIdNumber}, :#{#accountAuthMethods.authMethods}) ON CONFLICT DO NOTHING")
    Mono<AccountAuthMethods> saveIfNotExist(@Param("accountAuthMethods") AccountAuthMethods accountAuthMethods);

    @Query("DELETE FROM account_auth_methods WHERE health_id_number = :healthIdNumber")
    Mono<Void> deleteByHealthId(@Param("healthIdNumber") String healthIdNumber);
}

