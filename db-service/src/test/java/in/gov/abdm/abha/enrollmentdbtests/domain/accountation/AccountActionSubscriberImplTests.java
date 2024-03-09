package in.gov.abdm.abha.enrollmentdbtests.domain.accountation;

import in.gov.abdm.abha.enrollmentdb.domain.accountaction.AccountActionSubscriberImpl;
import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivestreams.Subscription;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class AccountActionSubscriberImplTests {
    @InjectMocks
    AccountActionSubscriberImpl accountActionSubscriber;
    @Mock
    Subscription s;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void onSubscribeTest(){
        //Mockito.when(s.request(any())).thenReturn(Mono.empty());
        accountActionSubscriber.onSubscribe(s);
    }
    @Test
    public void onNextTest(){
        //Mockito.when(s.request(any())).thenReturn(Mono.empty());
        accountActionSubscriber.onNext(new Accounts());
    }
    @Test
    public void onErrorTest(){
        //Mockito.when(s.request(any())).thenReturn(Mono.empty());
        accountActionSubscriber.onError(new Throwable());
    }
    @Test
    public void onCompleteTest(){
        //Mockito.when(s.request(any())).thenReturn(Mono.empty());
        accountActionSubscriber.onComplete();
    }
}
