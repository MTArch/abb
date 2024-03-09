package in.gov.abdm.abha.enrollmentdbtests.domain.hid_phr_address;

import in.gov.abdm.abha.enrollmentdb.domain.hid_phr_address.HidPhrAddressSubscriberImpl;
import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import in.gov.abdm.abha.enrollmentdb.model.hid_phr_address.HidPhrAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivestreams.Subscription;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class HidPhrAddressSubscriberImplTests {
    @InjectMocks
    HidPhrAddressSubscriberImpl hidPhrAddressSubscriber;
    @Mock
    Subscription s;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void onSubscribeTest(){
        //Mockito.when(s.request(any())).thenReturn(Mono.empty());
        hidPhrAddressSubscriber.onSubscribe(s);
    }
    @Test
    public void onNextTest(){
        //Mockito.when(s.request(any())).thenReturn(Mono.empty());
        hidPhrAddressSubscriber.onNext(new HidPhrAddress());
    }
    @Test
    public void onErrorTest(){
        //Mockito.when(s.request(any())).thenReturn(Mono.empty());
        hidPhrAddressSubscriber.onError(new Throwable());
    }
    @Test
    public void onCompleteTest(){
        //Mockito.when(s.request(any())).thenReturn(Mono.empty());
        hidPhrAddressSubscriber.onComplete();
    }

}
