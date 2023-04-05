package in.gov.abdm.abha.enrollmentdb.domain.HidPhrAddress;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.ENROLLMENT_LOG_PREFIX;

import org.reactivestreams.Subscription;
import org.springframework.stereotype.Component;

import in.gov.abdm.abha.enrollmentdb.model.HidPhrAddress.HidPhrAddress;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@Component
@Slf4j
public class HidPhrAddressSubscriberImpl implements HidPhrAddressSubscriber{

    /**
     * number of rows to be inserted.
     */
    public static final Long ROW_COUNT = 1L;

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
     * @param hidPhrAddressDto the element signaled
     */

    @Override
    public void onNext(HidPhrAddress hidPhrAddressDto) {

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

        log.info(ENROLLMENT_LOG_PREFIX + "HidPhrAddress completed");
    }
}
