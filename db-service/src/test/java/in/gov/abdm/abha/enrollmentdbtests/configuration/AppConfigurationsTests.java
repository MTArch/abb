package in.gov.abdm.abha.enrollmentdbtests.configuration;

import in.gov.abdm.abha.enrollmentdb.configuration.AppConfigurations;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
public class AppConfigurationsTests {
    @InjectMocks
    AppConfigurations appConfigurations;
    @Mock
    ServerCodecConfigurer serverCodecConfigurer;
    @Mock
    ServerCodecConfigurer.ServerDefaultCodecs serverDefaultCodecs;
    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void configureHttpMessageCodecsTest(){
        Mockito.when(serverCodecConfigurer.defaultCodecs()).thenReturn(serverDefaultCodecs);
        appConfigurations.configureHttpMessageCodecs(serverCodecConfigurer);
        }
}
