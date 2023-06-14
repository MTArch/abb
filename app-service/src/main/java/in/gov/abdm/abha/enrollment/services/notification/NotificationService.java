package in.gov.abdm.abha.enrollment.services.notification;

import in.gov.abdm.abha.enrollment.client.NotificationAppFClient;
import in.gov.abdm.abha.enrollment.exception.notification.NotificationGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.notification.*;
import in.gov.abdm.abha.enrollment.utilities.Common;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.*;

@Service
@Slf4j
public class NotificationService {

    public static final String ORIGIN = "abha";
    public static final String SENDER = "NHASMS";
    public static final String MOBILE_KEY = "mobile";
    public static final String TEMPLATE_ID = "templateId";
    public static final String SUBJECT = "subject";
    public static final String CONTENT = "content";

    public static final String EMAIL_KEY = "emailId";

    private static final String OTP_SUBJECT = "mobile verification";
    private static final String SMS_SUBJECT = "account creation";

    private static final String ABHA_URL= "https://healthid.abdm.gov.in";

    private static final String NOTIFICATION_ERROR_MESSAGE = "Notification service error {}";

    @Autowired
    NotificationAppFClient notificationAppFClient;

    @Autowired
    TemplatesHelper templatesHelper;

    public Mono<NotificationResponseDto> sendRegistrationOtp(String phoneNumber, String otp){
        return sendSMS(NotificationType.SMS,
                NotificationContentType.OTP,
                phoneNumber,
                OTP_SUBJECT,
                REGISTRATION_OTP_TEMPLATE_ID,
                templatesHelper.prepareRegistrationOtpMessage(REGISTRATION_OTP_TEMPLATE_ID, otp))
                .onErrorResume((throwable -> {
                    log.error(NOTIFICATION_ERROR_MESSAGE, throwable.getMessage());
                    return Mono.error(new NotificationGatewayUnavailableException());
                }));
    }
    public Mono<NotificationResponseDto> sendABHACreationSMS(String phoneNumber, String name, String abhaNumber){
        return sendSMS(NotificationType.SMS,
                NotificationContentType.OTP,
                phoneNumber,
                SMS_SUBJECT,
                ABHA_CREATED_TEMPLATE_ID,
                templatesHelper.prepareSMSMessage(ABHA_CREATED_TEMPLATE_ID, name, abhaNumber))
                .onErrorResume((throwable -> {
                    log.error(NOTIFICATION_ERROR_MESSAGE, throwable.getMessage());
                    return Mono.error(new NotificationGatewayUnavailableException());
                }));
    }

    public Mono<NotificationResponseDto> sendEnrollCreationSMS(String phoneNumber,String name,String abhaNumber){
        return sendSMS(NotificationType.SMS,
                NotificationContentType.OTP,
                phoneNumber,
                SMS_SUBJECT,
                ENROLL_CREATED_TEMPLATE_ID,
                templatesHelper.prepareSMSMessage(ENROLL_CREATED_TEMPLATE_ID, name, abhaNumber))
                .onErrorResume((throwable -> {
                    log.error(NOTIFICATION_ERROR_MESSAGE, throwable.getMessage());
                    return Mono.error(new NotificationGatewayUnavailableException());
                }));
    }

    public Mono<NotificationResponseDto> sendSMS(NotificationType notificationType, NotificationContentType notificationContentType , String phoneNumber, String subject,Long templateId,String message) {
        return notificationAppFClient.sendOtp(
                prepareNotificationRequest(notificationType,
                        notificationContentType.getValue(),
                        phoneNumber,
                        subject,
                        templateId,
                        message), UUID.randomUUID().toString(), Common.timeStampWithT())
                .onErrorResume((throwable->{
                    log.error(NOTIFICATION_ERROR_MESSAGE, throwable.getMessage());
                    return Mono.error(new NotificationGatewayUnavailableException());
                }));
    }

    private NotificationRequestDto prepareNotificationRequest(NotificationType notificationType, String contentType, String phoneNumber, String subject, Long templateId ,String message) {
        NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
        notificationRequestDto.setOrigin(ORIGIN);
        notificationRequestDto.setType(Collections.singletonList(notificationType.getValue()));
        notificationRequestDto.setContentType(contentType);
        notificationRequestDto.setSender(SENDER);
        notificationRequestDto.setReceiver(Collections.singletonList(new KeyValue(MOBILE_KEY, phoneNumber)));
        List<KeyValue> notification = new LinkedList<>();
        notification.add(new KeyValue(TEMPLATE_ID, String.valueOf(templateId)));
        notification.add(new KeyValue(SUBJECT, subject));
        notification.add(new KeyValue(CONTENT, message));
        notificationRequestDto.setNotification(notification);
        return notificationRequestDto;
    }

    public Mono<NotificationResponseDto> sendEmailOtp(String email, String subject, String message) {
        return notificationAppFClient.sendOtp(
                prepareEmailNotificationRequest(NotificationType.EMAIL,
                        NotificationContentType.OTP.getValue(),
                        email,
                        subject,
                        message),UUID.randomUUID().toString(), Common.timeStampWithT())
                .onErrorResume((throwable->{
                    log.error(NOTIFICATION_ERROR_MESSAGE, throwable.getMessage());
                    return Mono.error(new NotificationGatewayUnavailableException());
                }));
    }

    private NotificationRequestDto prepareEmailNotificationRequest(NotificationType notificationType, String contentType, String email, String subject, String message) {
        NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
        notificationRequestDto.setOrigin(ORIGIN);
        notificationRequestDto.setType(Collections.singletonList(notificationType.getValue()));
        notificationRequestDto.setContentType(contentType);
        notificationRequestDto.setSender(SENDER);
        notificationRequestDto.setReceiver(Collections.singletonList(new KeyValue(EMAIL_KEY, email)));
        List<KeyValue> notification = new LinkedList<>();
        notification.add(new KeyValue(TEMPLATE_ID, "1007164181681962323"));
        notification.add(new KeyValue(SUBJECT, subject));
        notification.add(new KeyValue(CONTENT, message));
        notificationRequestDto.setNotification(notification);
        return notificationRequestDto;
    }
}
