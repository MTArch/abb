package in.gov.abdm.abha.enrollmentdbtests.domain.hid_phr_address.event;

import in.gov.abdm.abha.enrollmentdb.domain.hid_phr_address.event.PHREventPublisher;
import in.gov.abdm.abha.enrollmentdb.model.account.AccountReattemptDto;
import in.gov.abdm.hiecm.userinitiatedlinking.Patient;
import in.gov.abdm.phr.enrollment.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.concurrent.ListenableFuture;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class PHREventPublisherTests {
    @InjectMocks
    PHREventPublisher phrEventPublisher;
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock
    private ListenableFuture<SendResult<String, Object>> sendResult;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

    }
    @AfterEach
    void tearDown() {

    }
    @Test
    public void publishTestsphr(){

        Mockito.when(kafkaTemplate.send(any(),any(),any())).thenReturn(sendResult);
        phrEventPublisher.publish(new User(),"test");
    }
    @Test
    public void publishTests2(){
        // Unused implementation of the method as ABHA does not publish a user object to
        // HIECM system.
        phrEventPublisher.publish(new Patient(),"test");
    }
    @Test
    public void publishTests3(){
        // Unused implementation of the method as ABHA does not publish a user object to HIECM system.
        phrEventPublisher.publish(new AccountReattemptDto(),"test");
    }
}
