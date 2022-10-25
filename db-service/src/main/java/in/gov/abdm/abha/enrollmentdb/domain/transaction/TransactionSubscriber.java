package in.gov.abdm.abha.enrollmentdb.domain.transaction;

import in.gov.abdm.abha.enrollmentdb.model.transaction.TransactionDto;
import org.reactivestreams.Subscriber;

/**
 * Subscriber class for Transaction
 */

public interface TransactionSubscriber extends Subscriber<TransactionDto> {

    
}
