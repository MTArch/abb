package in.gov.abdm.abha.enrollment.services.notification;

import in.gov.abdm.abha.enrollment.client.NotificationDbFClient;
import in.gov.abdm.abha.enrollment.model.notification.template.Templates;
import in.gov.abdm.abha.enrollment.services.notification.TemplatesHelper;
import in.gov.abdm.abha.enrollment.services.redis.RedisService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class TemplatesHelperTests {
    @InjectMocks
    TemplatesHelper templatesHelper;

    @Mock
    RedisService redisService;
    @Mock
    NotificationDbFClient notificationDbFClient;

    private Templates templates;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        templates=new Templates();
        templates.setName("");
        templates.setHeader("");
        String s = templates.getHeader();
        s=templates.getName();
    }
    @AfterEach
    void tearDown(){
        templates=null;
    }
    @Test
    public void prepareSMSMessageTests(){
        templates.setMessage("123");
        Mockito.when(redisService.getNotificationTemplate(any())).thenReturn(Mono.just(templates));
        StepVerifier.create(templatesHelper.prepareSMSMessage(1L, "Test"))
                .expectNextCount(1L)
                .verifyComplete();

    }


    }
