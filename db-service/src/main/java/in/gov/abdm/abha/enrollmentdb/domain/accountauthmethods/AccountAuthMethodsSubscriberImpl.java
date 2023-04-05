package in.gov.abdm.abha.enrollmentdb.domain.accountauthmethods;


import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.model.accountauthmethods.AccountAuthMethodsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@Component
@Slf4j
public class AccountAuthMethodsSubscriberImpl implements AccountAuthMethodsSubscriber {

    /**
     * number of rows to be inserted.
     */
     private static final Long ROW_COUNT = 1L;

    /**
     * Invoked after calling Publisher.subscribe(Subscriber).
     *
     * @param s the {@link Subscription} that allows requesting data via
     *          {@link Subscription#request(long)}
     */

    @Override
    public void onSubscribe(Subscription s) {

        s.request(ROW_COUNT);
    }

    /**
     * Data notification sent by the Publisher in response to requests to
     * Subscription.request(long).
     *
     * @param accountAuthMethodsDto the element signaled
     */

    @Override
    public void onNext(AccountAuthMethodsDto accountAuthMethodsDto) {

        log.info(ABHAEnrollmentDBConstant.ENROLLMENT_LOG_PREFIX + "Saving next");
    }

    /**
     * Failed terminal state.
     *
     * @param t the throwable signaled
     */

    @Override
    public void onError(Throwable t) {

        log.error(ABHAEnrollmentDBConstant.ENROLLMENT_LOG_PREFIX + t.getMessage());
    }

    /**
     * Successful terminal state.
     */

    @Override
    public void onComplete() {

        log.info(ABHAEnrollmentDBConstant.ENROLLMENT_LOG_PREFIX + "AccountAuthMethods completed");
    }
}
