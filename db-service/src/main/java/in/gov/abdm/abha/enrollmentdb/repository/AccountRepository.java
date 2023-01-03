package in.gov.abdm.abha.enrollmentdb.repository;

import java.util.List;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import in.gov.abdm.abha.enrollmentdb.model.account.AccountDto;
import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface AccountRepository extends ReactiveCrudRepository<Accounts, String> {
     Mono<Accounts> findByXmluid(@Param("xmluid") String xmluid);

     Flux<Accounts> findByHealthIdNumberIn(List<String> healthIdNumbers);

     @Query(value = "SELECT encode(lo_get(kyc_photo), 'base64') FROM accounts a where a.health_id_number = :healthIdNumber")
     Mono<String> getProfilePhoto(@Param("healthIdNumber") String healthIdNumber);

    Mono<Accounts> findByDocumentCode(@Param("documentCode") String documentCode);

    @Query(value = "UPDATE accounts SET kyc_photo=lo_from_bytea(0, :kycPhoto) where health_id_number = :healthIdNumber")
    Mono<AccountDto> updateKycPhoto(@Param("kycPhoto") byte[] kycPhoto, @Param("healthIdNumber") String healthIdNumber);
}