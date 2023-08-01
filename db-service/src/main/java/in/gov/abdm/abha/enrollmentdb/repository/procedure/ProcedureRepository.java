package in.gov.abdm.abha.enrollmentdb.repository.procedure;

import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@Repository
public interface ProcedureRepository extends R2dbcRepository<Accounts, BigInteger> {

    @Query(value = "call sp_add_account_json(:accountsData,:hidPhrAddressData,:accountAuthMethods)")
    Mono<String> saveAllDataProcedure(String accountsData, String hidPhrAddressData, String accountAuthMethods);

}
