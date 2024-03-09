package in.gov.abdm.abha.enrollmentdbtests.configuration;

import in.gov.abdm.abha.enrollmentdb.configuration.ABHAEnrollmentDBConfiguration;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
public class ABHAEnrollmentDBConfigurationTests {
    @InjectMocks
    ABHAEnrollmentDBConfiguration abhaEnrollmentDBConfiguration;
    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void modelMapperTest(){
      abhaEnrollmentDBConfiguration.modelMapper();
     }
}
