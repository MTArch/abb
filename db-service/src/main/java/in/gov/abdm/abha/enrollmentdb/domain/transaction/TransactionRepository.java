package in.gov.abdm.abha.enrollmentdb.domain.transaction;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface TransactionRepository extends ReactiveCrudRepository<Transaction, BigInteger> {
}
