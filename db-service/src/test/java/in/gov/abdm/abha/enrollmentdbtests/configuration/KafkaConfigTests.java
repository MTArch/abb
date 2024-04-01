package in.gov.abdm.abha.enrollmentdbtests.configuration;

import in.gov.abdm.abha.enrollmentdb.configuration.KafkaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.KAFKA_SERVER;

@ExtendWith(SpringExtension.class)
public class KafkaConfigTests {
    @InjectMocks
    KafkaConfig kafkaConfig;
    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void producerFactoryTest(){
        ReflectionTestUtils.setField(kafkaConfig,"kafkaServer","KAFKA_SERVER");
        kafkaConfig.producerFactory();
   //     StepVerifier.create(kafkaConfig.producerFactory()).expectNextCount(1L).verifyComplete();
    }
    @Test
    public void kafkaTemplate(){
        ReflectionTestUtils.setField(kafkaConfig,"kafkaServer","KAFKA_SERVER");
        Map<String, Object> config = new HashMap<>();
        Mockito.when(Mockito.mock(KafkaConfig.class).producerFactory()).thenReturn(new DefaultKafkaProducerFactory<>(config));
        kafkaConfig.kafkaTemplate();
        //     StepVerifier.create(kafkaConfig.producerFactory()).expectNextCount(1L).verifyComplete();
    }
}
