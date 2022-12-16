package in.gov.abdm.abha.enrollmentdb.repository;

import java.time.LocalDateTime;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import in.gov.abdm.abha.enrollmentdb.model.transaction.TransactionDto;
import in.gov.abdm.abha.enrollmentdb.model.transaction.Transection;
import reactor.core.publisher.Mono;

@Repository
public interface TransactionRepository extends ReactiveCrudRepository<Transection, Long> {

	@Query("SELECT * FROM transection t where t.txn_id = :txnId AND t.created_date BETWEEN :fromDateTime AND :toDateTime")
	public Mono<Transection> findByTxnId(@Param("txnId") String txnId, @Param("fromDateTime") LocalDateTime fromDateTime,
			@Param("toDateTime") LocalDateTime toDateTime);

	@Query(value = "UPDATE transection SET kyc_photo=lo_from_bytea(0, :kycPhoto) where id = :id")
	Mono<TransactionDto> updateKycPhoto(@Param("kycPhoto") byte[] kycPhoto, @Param("id") Long id);
	
	@Query(value = "SELECT encode(lo_get(kyc_photo), 'base64') FROM transection t where t.id = :id")
    Mono<String> getProfilePhoto(@Param("id") Long id);

}