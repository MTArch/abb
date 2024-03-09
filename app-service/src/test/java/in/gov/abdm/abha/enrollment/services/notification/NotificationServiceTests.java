package in.gov.abdm.abha.enrollment.services.notification;

import in.gov.abdm.abha.enrollment.client.NotificationAppFClient;
import in.gov.abdm.abha.enrollment.exception.notification.NotificationGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.notification.*;
import in.gov.abdm.abha.enrollment.services.notification.NotificationService;
import in.gov.abdm.abha.enrollment.services.notification.TemplatesHelper;
import liquibase.pro.packaged.M;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(SpringExtension.class)
public class NotificationServiceTests {
    @InjectMocks
    NotificationService notificationService;
    @Mock
    NotificationAppFClient notificationAppFClient;
    @Mock
    TemplatesHelper templatesHelper;
    private NotificationResponseDto notificationResponseDto;
    private NotificationType notificationType;
    private NotificationContentType notificationContentType;


    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        KeyValue keyValue= new KeyValue();
        NotificationRequestDto notificationRequestDto=new NotificationRequestDto("", Arrays.asList(""),"","", List.of(keyValue),List.of(keyValue));
        String s = notificationRequestDto.getOrigin();
        List<String> sa = notificationRequestDto.getType();
        s = notificationRequestDto.getSender();
        s = notificationRequestDto.getContentType();
        List<KeyValue> receiver = notificationRequestDto.getReceiver();
        receiver = notificationRequestDto.getNotification();

        keyValue.setKey("");
        keyValue.setValue("");
        s = keyValue.getKey()+keyValue.getValue();
        notificationResponseDto=new NotificationResponseDto();

    }
    @AfterEach
    void tearDown(){
        notificationResponseDto=null;

    }
    @Test
    public void sendRegistrationOtpSuccess(){
        Mockito.when(templatesHelper.prepareSMSMessage(any(),any())).thenReturn(Mono.just("Success"));
        Mockito.when(notificationAppFClient.sendOtp(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        StepVerifier.create(notificationService.sendRegistrationOtp("7879932233","234321"))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    public void sendABHACreationSMSSuccess(){
        Mockito.when(templatesHelper.prepareSMSMessage(any(),any())).thenReturn(Mono.just("Success"));
        Mockito.when(notificationAppFClient.sendOtp(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        StepVerifier.create(notificationService.sendABHACreationSMS("7879932233","Snehal","123432"))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    public void sendEnrollCreationSMSSuccess(){
        Mockito.when(templatesHelper.prepareSMSMessage(any(),any())).thenReturn(Mono.just("Success"));
        Mockito.when(notificationAppFClient.sendOtp(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        StepVerifier.create(notificationService.sendEnrollCreationSMS("7879932233","Snehal","123432"))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    public void sendEmailOtpSuccess(){
        Mockito.when(notificationAppFClient.sendOtp(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        StepVerifier.create(notificationService.sendEmailOtp("email@gmail.com","OTP","OTP"))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    public void sendSmsAndEmailOtpSuccess(){
        Mockito.when(notificationAppFClient.sendOtp(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        StepVerifier.create(notificationService.sendSmsAndEmailOtp("email@gmail.com","789238392","OTP","OTP"))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void sendOtpFail(){
        //Mockito.when(templatesHelper.prepareSMSMessage(any(),any())).thenReturn(Mono.error(Exception::new));
        Mockito.when(notificationAppFClient.sendOtp(any(),any(),any())).thenReturn(Mono.error(Exception::new));
        StepVerifier.create(notificationService.sendSMS(NotificationType.SMS,NotificationContentType.OTP,"","",1L,"")).expectError().verify();
    }
    @Test
    void sendOtpFail2(){
        Mockito.when(templatesHelper.prepareSMSMessage(any(),any())).thenReturn(Mono.error(Exception::new));
        Mockito.when(notificationAppFClient.sendOtp(any(),any(),any())).thenReturn(Mono.error(Exception::new));
        StepVerifier.create(notificationService.sendRegistrationOtp("","")).expectError().verify();
    }

}
