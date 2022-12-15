package in.gov.abdm.abha.enrollmentdb.domain.transaction;

import org.reactivestreams.Subscriber;

import in.gov.abdm.abha.enrollmentdb.model.transaction.Transection;

/**
 * Subscriber class for Transaction
 */

public interface TransactionSubscriber extends Subscriber<Transection> {

    
}
