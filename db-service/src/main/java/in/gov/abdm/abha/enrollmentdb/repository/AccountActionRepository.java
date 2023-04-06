package in.gov.abdm.abha.enrollmentdb.repository;

import java.math.BigInteger;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.gov.abdm.abha.enrollmentdb.model.accountaction.AccountActionDto;
import in.gov.abdm.abha.enrollmentdb.model.accountaction.AccountActions;
import reactor.core.publisher.Mono;

@Repository
public interface AccountActionRepository extends R2dbcRepository<AccountActions, BigInteger> {

    @Query(value = "SELECT  action, created_date, field, health_id_number, new_value, previous_value, reactivation_date, reason, reasons FROM account_actions a where a.health_id_number = :healthIdNumber order by a.created_date desc limit 1")
    Mono<AccountActionDto> getAccountsByHealthIdNumber(@Param("healthIdNumber") String healthIdNumber);

    @Query(value = "INSERT INTO account_actions(action, created_date, field, health_id_number, new_value, previous_value, reactivation_date, reason, reasons) VALUES (:#{#accountAction.action}, :#{#accountAction.createdDate}, :#{#accountAction.field}, :#{#accountAction.healthIdNumber}, :#{#accountAction.newValue}, :#{#accountAction.previousValue}, :#{#accountAction.reactivationDate}, :#{#accountAction.reason}, :#{#accountAction.reasons})")
    Mono<AccountActionDto> saveAccounts(@Param("accountAction") AccountActionDto accountAction);
}
