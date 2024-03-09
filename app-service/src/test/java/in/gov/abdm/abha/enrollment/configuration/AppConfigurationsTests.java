package in.gov.abdm.abha.enrollment.configuration;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.config.CorsRegistry;
import reactivefeign.ReactiveOptions;

import java.util.List;

@ExtendWith(SpringExtension.class)
public class AppConfigurationsTests {
    @InjectMocks
    AppConfigurations appConfigurations;
    @Mock
    ServerCodecConfigurer configurer;
    @Mock
    ServerCodecConfigurer.ServerDefaultCodecs serverDefaultCodecs;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

    }
    @AfterEach
    void teardown() {

    }
    @Test
    public void reactiveOptionsTests(){
        ReactiveOptions r =appConfigurations.reactiveOptions();
        Assert.assertEquals(r.isEmpty(), false);
    }
    @Test
    public void configureHttpMessageCodecsTests(){
        Mockito.when(configurer.defaultCodecs()).thenReturn(serverDefaultCodecs);
        appConfigurations.configureHttpMessageCodecs(configurer);
    }
    @Test
    public void addCorsMappingsTests(){
        ReflectionTestUtils.setField(appConfigurations,"uiUrls", new String[]{"/test","/test2"});
        appConfigurations.addCorsMappings(new CorsRegistry());
    }
}
