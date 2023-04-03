package in.gov.abdm.abha.enrollmentdb.domain.DependentAccountRelationship;


import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.ENROLLMENT_LOG_PREFIX;

import org.reactivestreams.Subscription;
import org.springframework.stereotype.Component;

import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationship;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@Component
@Slf4j
public class DependentAccountRelationshipSubscriberImpl implements DependentAccountRelationshipSubscriber{


    /**
     * number of rows to be inserted.
     */
    static Long rowCount;

    /**
     * Invoked after calling Publisher.subscribe(Subscriber).
     *
     * @param s the {@link Subscription} that allows requesting data via
     *          {@link Subscription#request(long)}
     */

    @Override
    public void onSubscribe(Subscription s) {

        s.request(1L);
    }

    /**
     * Data notification sent by the Publisher in response to requests to
     * Subscription.request(long).
     *
     * @param dependentAccountRelationship the element signaled
     */
    @Override
    public void onNext(DependentAccountRelationship dependentAccountRelationship) {

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

        log.info(ENROLLMENT_LOG_PREFIX + "DependentAccountRelationship completed");

    }
}
