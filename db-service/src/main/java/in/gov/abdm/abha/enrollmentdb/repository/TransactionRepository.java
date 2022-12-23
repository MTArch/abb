package in.gov.abdm.abha.enrollmentdb.repository;

import in.gov.abdm.abha.enrollmentdb.model.transaction.Transection;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface TransactionRepository extends ReactiveCrudRepository<Transection, Long> {

//	public Mono updateTransactionById(Transaction transaction, Long id);

	@Query(value = "SELECT max(id) FROM transection")
	Mono<Long> getMaxTransactionId();

	@Query("SELECT * FROM transection t where t.txn_id = :txnId AND t.created_date BETWEEN :fromDateTime AND :toDateTime")
	public Mono<Transection> findByTxnId(@Param("txnId") String txnId, @Param("fromDateTime") LocalDateTime fromDateTime,
			@Param("toDateTime") LocalDateTime toDateTime);

	@Query("DELETE FROM transection WHERE transection.txn_id = :txnId")
	Mono<Void> deleteByTxnId(@Param("txnId") String txnId);
}