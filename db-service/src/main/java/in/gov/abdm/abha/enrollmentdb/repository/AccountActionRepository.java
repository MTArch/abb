package in.gov.abdm.abha.enrollmentdb.repository;

import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import in.gov.abdm.abha.enrollmentdb.model.accountaction.AccountActionDto;
import in.gov.abdm.abha.enrollmentdb.model.accountaction.AccountActions;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@Repository
public interface AccountActionRepository extends R2dbcRepository<AccountActions, BigInteger> {
    @Query(value = "UPDATE public.account_actions SET action=:#{#accountAction.action}, health_id_number=:#{#accountAction.healthIdNumber}, new_value=:#{#accountAction.newValue}, previous_value=:#{#accountAction.previousValue}, reason=:#{#accountAction.reason}, reasons=:#{#accountAction.reasons} where health_id_number = :healthIdNumber")
    Mono<AccountActions> updateAccountAction(@Param("healthIdNumber") String healthIdNumber, @Param("accountAction") AccountActions accountAction);

    @Query(value = "SELECT id, action, created_date, field, health_id_number, new_value, previous_value, reactivation_date, reason, reasons FROM public.account_actions a where a.health_id_number = :healthIdNumber order by a.created_date desc limit 1")
    Mono<AccountActions> getAccountsByHealthIdNumber(@Param("healthIdNumber") String healthIdNumber);

    @Query(value = "INSERT INTO public.account_actions(action, created_date, field, health_id_number, new_value, previous_value, reactivation_date, reason, reasons) VALUES (:#{#accountAction.action}, :#{#accountAction.createdDate}, :#{#accountAction.field}, :#{#accountAction.healthIdNumber}, :#{#accountAction.newValue}, :#{#accountAction.previousValue}, :#{#accountAction.reactivationDate}, :#{#accountAction.reason}, :#{#accountAction.reasons})")
    Mono<AccountActionDto> saveAccounts(@Param("accountAction") AccountActions accountAction);
}
