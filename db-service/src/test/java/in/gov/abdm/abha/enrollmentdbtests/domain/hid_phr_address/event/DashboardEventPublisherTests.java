package in.gov.abdm.abha.enrollmentdbtests.domain.hid_phr_address.event;

import in.gov.abdm.abha.enrollmentdb.domain.hid_phr_address.event.DashboardEventPublisher;
import in.gov.abdm.abha.enrollmentdb.model.account.AccountReattemptDto;
import in.gov.abdm.abha.enrollmentdb.model.transaction.TransactionDto;
import in.gov.abdm.hiecm.userinitiatedlinking.Patient;
import in.gov.abdm.phr.enrollment.user.User;
import org.junit.Assert;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(SpringExtension.class)
public class DashboardEventPublisherTests {
    @InjectMocks
    DashboardEventPublisher dashboardEventPublisher;
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
    public void publishTests(){

        Mockito.when(kafkaTemplate.send(any(),any(),any())).thenReturn(sendResult);
        dashboardEventPublisher.publish(new AccountReattemptDto(),"test");
    }
    @Test
    public void publishTestsPHR(){
        // Unused implementation of the method as ABHA does not publish a user object to PHR system.
        dashboardEventPublisher.publish(new User(),"test");
    }
    @Test
    public void publishTestsHIECM(){
        // Unused implementation of the method as ABHA does not publish a user object to HIECM system.
        dashboardEventPublisher.publish(new Patient(),"test");
    }

}
