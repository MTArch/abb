package in.gov.abdm.abha.enrollment.services.redis;

import in.gov.abdm.abha.enrollment.client.IntegratedProgramDBFClient;
import in.gov.abdm.abha.enrollment.client.NotificationDbFClient;
import in.gov.abdm.abha.enrollment.exception.application.AbhaUnProcessableException;
import in.gov.abdm.abha.enrollment.model.entities.IntegratedProgramDto;
import in.gov.abdm.abha.enrollment.model.notification.template.Templates;
import in.gov.abdm.abha.enrollment.model.redis.otp.ReceiverOtpTracker;
import in.gov.abdm.abha.enrollment.model.redis.otp.RedisOtp;
import in.gov.abdm.abha.enrollment.services.redis.RedisService;
import liquibase.pro.packaged.M;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.data.redis.core.HashOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(SpringExtension.class)
public class RedisServiceTests {
    @InjectMocks
    RedisService redisService;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private NotificationDbFClient notificationDBClient;

    @Mock
    private IntegratedProgramDBFClient integratedProgramDBFClient;

    @Mock
    HashOperations hashOperations;
    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        RedisOtp redisOtp=new RedisOtp("","","","");
        redisOtp.setTxnId("");
        String s =redisOtp.getTxnId();
        ReceiverOtpTracker receiverOtpTracker= ReceiverOtpTracker.builder().build();
        receiverOtpTracker.setReceiver("");
        s = receiverOtpTracker.getReceiver();
    }
    @AfterEach
    void tearDown(){

    }
    @Test
    public void saveRedisOtpTests(){
        HashOperations<String, Object, Object> hash = hashOperations;
        Mockito.when(redisTemplate.opsForHash()).thenReturn(hash);
        redisService.saveRedisOtp("",new RedisOtp());
    }
    @Test
    public void saveReceiverOtpTrackerTests(){
        HashOperations<String, Object, Object> hash = hashOperations;
        Mockito.when(redisTemplate.opsForHash()).thenReturn(hash);
        redisService.saveReceiverOtpTracker("",new ReceiverOtpTracker());
    }
    @Test
    public void saveNotificationTemplateTests(){
        HashOperations<String, Object, Object> hash = hashOperations;
        Mockito.when(redisTemplate.opsForHash()).thenReturn(hash);
        redisService.saveNotificationTemplate(new Templates(1L,"tempName","msg","header"));
    }
    @Test
    public void saveIntegratedProgramTests(){
        HashOperations<String, Object, Object> hash = hashOperations;
        Mockito.when(redisTemplate.opsForHash()).thenReturn(hash);
        redisService.saveIntegratedProgram(new IntegratedProgramDto());
    }
    @Test
    public void deleteRedisOtpTests(){
        HashOperations<String, Object, Object> hash = hashOperations;
        Mockito.when(redisTemplate.opsForHash()).thenReturn(hash);
        redisService.deleteRedisOtp("test");
    }
    @Test
    public void deleteReceiverOtpTrackerTests(){
        HashOperations<String, Object, Object> hash = hashOperations;
        Mockito.when(redisTemplate.opsForHash()).thenReturn(hash);
        redisService.deleteReceiverOtpTracker("test");
    }
    @Test
    public void getReceiverOtpTrackerTests(){
        HashOperations<String, Object, Object> hash = hashOperations;
        Mockito.when(redisTemplate.opsForHash()).thenReturn(hash);
        redisService.getReceiverOtpTracker("test");
    }
    @Test
    public void getNotificationTemplateTests(){
        HashOperations<String, Object, Object> hash = hashOperations;
        Mockito.when(redisTemplate.opsForHash()).thenReturn(hash);
        //Mockito.when(redisTemplate.opsForHash().get(any(),any())).thenReturn(new Templates(1L,"tempName","msg","header"));

        Mockito.when(notificationDBClient.getById(any(),any(),any())).thenReturn(Mono.just(new Templates(1L,"tempName","msg","header")));
        StepVerifier.create(redisService.getNotificationTemplate("test")).expectNextCount(1L).verifyComplete();
    }
    @Test
    public void getNotificationTemplateTests2(){
        HashOperations<String, Object, Object> hash = hashOperations;
        Mockito.when(redisTemplate.opsForHash()).thenReturn(hash);
        Mockito.when(redisTemplate.opsForHash().get(any(),any())).thenReturn(new Templates(1L,"tempName","msg","header"));

        Mockito.when(notificationDBClient.getById(any(),any(),any())).thenReturn(Mono.just(new Templates(1L,"tempName","msg","header")));
        StepVerifier.create(redisService.getNotificationTemplate("test")).expectNextCount(1L).verifyComplete();
    }
    @Test
    public void getNotificationTemplateTestsOnError(){
        HashOperations<String, Object, Object> hash = hashOperations;
        Mockito.when(redisTemplate.opsForHash()).thenReturn(hash);
        Mockito.when(notificationDBClient.getById(any(),any(),any())).thenReturn(Mono.error(Exception::new));
        StepVerifier.create(redisService.getNotificationTemplate("test")).expectError(AbhaUnProcessableException.class).verify();

    }
    @Test
    public void getRedisOtpTests(){
        HashOperations<String, Object, Object> hash = hashOperations;
        Mockito.when(redisTemplate.opsForHash()).thenReturn(hash);
        RedisOtp res = redisService.getRedisOtp("test");
        //Assert.assertEquals(false,res);

    }
    @Test
    public void isRedisOtpAvailableTests(){
        HashOperations<String, Object, Object> hash = hashOperations;
        Mockito.when(redisTemplate.opsForHash()).thenReturn(hash);
        Boolean res = redisService.isRedisOtpAvailable("test");
        Assert.assertEquals(false,res);
    }
    @Test
    public void getIntegratedProgramsTests(){
        HashOperations<String, Object, Object> hash = hashOperations;
        Mockito.when(redisTemplate.opsForHash()).thenReturn(hash);
        List<IntegratedProgramDto> integratedPrograms = redisService.getIntegratedPrograms();

    }
    @Test
    public void getIntegratedProgramsTests2(){
        HashOperations<String, Object, Object> hash = hashOperations;
        Map<Object,Object> map =new HashMap<>();
        map.put("a",new IntegratedProgramDto());
        Mockito.when(redisTemplate.opsForHash()).thenReturn(hash);
        Mockito.when(redisTemplate.opsForHash().entries("INTEGRATED_PROGRAMS")).thenReturn(map);
        List<IntegratedProgramDto> integratedPrograms = redisService.getIntegratedPrograms();

    }
    @Test
    public void reloadAndGetIntegratedProgramsTests(){
        HashOperations<String, Object, Object> hash = hashOperations;
        Mockito.when(redisTemplate.opsForHash()).thenReturn(hash);
        Mockito.when(integratedProgramDBFClient.getAll(any(),any())).thenReturn(Flux.just(new IntegratedProgramDto()));
        StepVerifier.create(redisService.reloadAndGetIntegratedPrograms()).expectNextCount(1L).verifyComplete();

    }
    @Test
    public void reloadAndGetIntegratedProgramsTestsOnError(){
        HashOperations<String, Object, Object> hash = hashOperations;
        Mockito.when(redisTemplate.opsForHash()).thenReturn(hash);
        Mockito.when(integratedProgramDBFClient.getAll(any(),any())).thenReturn(Flux.error(Exception::new));
        StepVerifier.create(redisService.reloadAndGetIntegratedPrograms()).expectNextCount(1L).verifyComplete();

    }
    @Test
    public void isResendOtpAllowedTests(){
        HashOperations<String, Object, Object> hash = hashOperations;
        Mockito.when(redisTemplate.opsForHash()).thenReturn(hash);
        Mockito.when(redisTemplate.opsForHash().hasKey(anyString(),any())).thenReturn(true);
        Mockito.when(redisService.getReceiverOtpTracker("1")).thenReturn(new ReceiverOtpTracker("",1,1,true));
        Mockito.when(redisService.isReceiverOtpTrackerAvailable("1")).thenReturn(true);
        Mockito.when(integratedProgramDBFClient.getAll(any(),any())).thenReturn(Flux.just(new IntegratedProgramDto()));
        redisService.isResendOtpAllowed("1");
    }
    /*@Test
    public void isResendOtpAllowedTests2(){
        HashOperations<String, Object, Object> hash = hashOperations;
        Mockito.when(redisTemplate.opsForHash()).thenReturn(hash);
        Mockito.when(redisTemplate.opsForHash().hasKey(anyString(),any())).thenReturn(true);
        Mockito.when(redisService.isReceiverOtpTrackerAvailable("1")).thenReturn(false);
        //Mockito.when(redisService.getReceiverOtpTracker("1")).thenReturn(new ReceiverOtpTracker("",1,1,true));
        //Mockito.when(redisService.getReceiverOtpTracker("1").isBlocked()).thenReturn(true);
        Mockito.when(integratedProgramDBFClient.getAll(any(),any())).thenReturn(Flux.just(new IntegratedProgramDto()));
        redisService.isResendOtpAllowed("test");

    }*/
    @Test
    public void isMultipleOtpVerificationAllowedTests(){
        HashOperations<String, Object, Object> hash = hashOperations;
        Mockito.when(redisTemplate.opsForHash()).thenReturn(hash);
        Mockito.when(integratedProgramDBFClient.getAll(any(),any())).thenReturn(Flux.just(new IntegratedProgramDto()));
        redisService.isMultipleOtpVerificationAllowed("test");

    }



}
