package in.gov.abdm.abha.enrollmentdbtests.domain.hid_phr_address.event;

import in.gov.abdm.abha.enrollmentdb.domain.hid_phr_address.event.PHREventConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class PHREventConsumerTests {
    @InjectMocks
    PHREventConsumer phrEventConsumer;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

    }
    @AfterEach
    void tearDown() {

    }
    @Test
    public void subscribeTests(){
        phrEventConsumer.subscribe("Test","test");
    }
}
