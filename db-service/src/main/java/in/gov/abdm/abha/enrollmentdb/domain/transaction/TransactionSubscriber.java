package in.gov.abdm.abha.enrollmentdb.domain.transaction;

import in.gov.abdm.abha.enrollmentdb.model.transaction.Transection;
import org.reactivestreams.Subscriber;

/**
 * Subscriber class for Transaction
 */

public interface TransactionSubscriber extends Subscriber<Transection> {

    
}
