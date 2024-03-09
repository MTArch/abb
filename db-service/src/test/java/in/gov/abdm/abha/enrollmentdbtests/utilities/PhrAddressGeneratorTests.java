package in.gov.abdm.abha.enrollmentdbtests.utilities;

import in.gov.abdm.abha.enrollmentdb.utilities.PhrAddressGenerator;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
public class PhrAddressGeneratorTests {
    @InjectMocks
    PhrAddressGenerator phrAddressGenerator;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void tearDown() {
    }
    @Test
    public void generateDefaultPhrAddressTest(){
        PhrAddressGenerator.generateDefaultPhrAddress("Test");
    }
}
