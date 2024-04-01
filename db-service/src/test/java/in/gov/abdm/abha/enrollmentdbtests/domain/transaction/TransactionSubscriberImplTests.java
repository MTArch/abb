package in.gov.abdm.abha.enrollmentdbtests.domain.transaction;

import in.gov.abdm.abha.enrollmentdb.domain.transaction.TransactionSubscriberImpl;
import in.gov.abdm.abha.enrollmentdb.model.hid_phr_address.HidPhrAddress;
import in.gov.abdm.abha.enrollmentdb.model.transaction.Transection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivestreams.Subscription;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class TransactionSubscriberImplTests {
    @InjectMocks
    TransactionSubscriberImpl transactionSubscriber;
    @Mock
    Subscription s;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void onSubscribeTest(){
        //Mockito.when(s.request(any())).thenReturn(Mono.empty());
        transactionSubscriber.onSubscribe(s);
    }
    @Test
    public void onNextTest(){
        //Mockito.when(s.request(any())).thenReturn(Mono.empty());
        transactionSubscriber.onNext(new Transection());
    }
    @Test
    public void onErrorTest(){
        //Mockito.when(s.request(any())).thenReturn(Mono.empty());
        transactionSubscriber.onError(new Throwable());
    }
    @Test
    public void onCompleteTest(){
        //Mockito.when(s.request(any())).thenReturn(Mono.empty());
        transactionSubscriber.onComplete();
    }
}
