package in.gov.abdm.abha.enrollmentdb.domain.accountaction;

import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import org.springframework.stereotype.Component;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.ENROLLMENT_LOG_PREFIX;

@Data
@AllArgsConstructor
@Component
@Slf4j
public class AccountActionSubscriberImpl implements AccountActionSubscriber {

	/**
	 * number of rows to be inserted.
	 */
	public static Long rowCount = 1L;

	/**
	 * Invoked after calling Publisher.subscribe(Subscriber).
	 * 
	 * @param s the {@link Subscription} that allows requesting data via
	 *          {@link Subscription#request(long)}
	 */
	@Override
	public void onSubscribe(Subscription s) {
		s.request(rowCount);
	}

	/**
	 * Data notification sent by the Publisher in response to requests to
	 * Subscription.request(long).
	 * 
	 * @param accounts the element signaled
	 */
	@Override
	public void onNext(Accounts accounts) {
		log.info(ENROLLMENT_LOG_PREFIX + "Saving next");
	}

	/**
	 * Failed terminal state.
	 * 
	 * @param t the throwable signaled
	 */
	@Override
	public void onError(Throwable t) {
		log.error(ENROLLMENT_LOG_PREFIX + t.getMessage());
	}

	/**
	 * Successful terminal state.
	 */
	@Override
	public void onComplete() {
		log.info(ENROLLMENT_LOG_PREFIX + "Transaction completed");
	}
}
