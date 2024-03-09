package in.gov.abdm.abha.enrollmentdbtests.domain.account;

import in.gov.abdm.abha.enrollmentdb.domain.account.AccountSubscriberImpl;
import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.reactivestreams.Subscription;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class AccountSubscriberImplTests {
    @InjectMocks
    AccountSubscriberImpl accountSubscriber;
    @Mock
    Subscription s;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void onSubscribeTest(){
        //Mockito.when(s.request(any())).thenReturn(Mono.empty());
        accountSubscriber.onSubscribe(s);
    }
    @Test
    public void onNextTest(){
        //Mockito.when(s.request(any())).thenReturn(Mono.empty());
        accountSubscriber.onNext(new Accounts());
    }
    @Test
    public void onErrorTest(){
        //Mockito.when(s.request(any())).thenReturn(Mono.empty());
        accountSubscriber.onError(new Throwable());
    }
    @Test
    public void onCompleteTest(){
        //Mockito.when(s.request(any())).thenReturn(Mono.empty());
        accountSubscriber.onComplete();
    }

}
