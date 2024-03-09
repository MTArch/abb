package in.gov.abdm.abha.enrollmentdbtests.domain.dependent_account_relationship;

import in.gov.abdm.abha.enrollmentdb.domain.dependent_account_relationship.DependentAccountRelationshipSubscriberImpl;
import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivestreams.Subscription;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class DependentAccountRelationshipSubscriberImplTests {
    @InjectMocks
    DependentAccountRelationshipSubscriberImpl dependentAccountRelationshipSubscriber;

    @Mock
    Subscription s;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void onSubscribeTest(){
        //Mockito.when(s.request(any())).thenReturn(Mono.empty());
        dependentAccountRelationshipSubscriber.onSubscribe(s);
    }
    @Test
    public void onNextTest(){
        //Mockito.when(s.request(any())).thenReturn(Mono.empty());
        dependentAccountRelationshipSubscriber.onNext(new DependentAccountRelationship());
    }
    @Test
    public void onErrorTest(){
        //Mockito.when(s.request(any())).thenReturn(Mono.empty());
        dependentAccountRelationshipSubscriber.onError(new Throwable());
    }
    @Test
    public void onCompleteTest(){
        //Mockito.when(s.request(any())).thenReturn(Mono.empty());
        dependentAccountRelationshipSubscriber.onComplete();
    }

}
