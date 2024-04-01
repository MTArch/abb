package in.gov.abdm.abha.enrollmentdbtests.domain.accountauthmethods;

import in.gov.abdm.abha.enrollmentdb.domain.accountauthmethods.AccountAuthMethodsSubscriberImpl;
import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import in.gov.abdm.abha.enrollmentdb.model.accountauthmethods.AccountAuthMethodsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivestreams.Subscription;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class AccountAuthMethodsSubscriberImplTests {
    @InjectMocks
    AccountAuthMethodsSubscriberImpl accountAuthMethodsSubscriber;
    @Mock
    Subscription s;
    @BeforeEach
    void setup() {
        AccountAuthMethodsDto a=new AccountAuthMethodsDto("1","authMethod");
        AccountAuthMethodsDto accountAuthMethodsDto=new AccountAuthMethodsDto();
        accountAuthMethodsDto.setAuthMethods(a.getAuthMethods());
        accountAuthMethodsDto.setHealthIdNumber(a.getHealthIdNumber());
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void onSubscribeTest(){
        //Mockito.when(s.request(any())).thenReturn(Mono.empty());
        accountAuthMethodsSubscriber.onSubscribe(s);
    }
    @Test
    public void onNextTest(){
        //Mockito.when(s.request(any())).thenReturn(Mono.empty());
        accountAuthMethodsSubscriber.onNext(new AccountAuthMethodsDto());
    }
    @Test
    public void onErrorTest(){
        //Mockito.when(s.request(any())).thenReturn(Mono.empty());
        accountAuthMethodsSubscriber.onError(new Throwable());
    }
    @Test
    public void onCompleteTest(){
        //Mockito.when(s.request(any())).thenReturn(Mono.empty());
        accountAuthMethodsSubscriber.onComplete();
    }
}
