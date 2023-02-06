package in.gov.abdm.abha.enrollment.services.notification;

import in.gov.abdm.abha.enrollment.client.NotificationClient;
import in.gov.abdm.abha.enrollment.model.notification.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
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

    private static final String ABHA_URL= "https://healthid.ndhm.gov.in";


    @Autowired
    NotificationClient notificationClient;

    @Autowired
    TemplatesHelper templatesHelper;

    public Mono<NotificationResponseDto> sendRegistrationOtp(String phoneNumber, String otp){
        return sendSMS(NotificationType.SMS,
                NotificationContentType.OTP,
                phoneNumber,
                OTP_SUBJECT,
                templatesHelper.prepareRegistrationOtpMessage(1007164181681962323L, otp));
    }

    public Mono<NotificationResponseDto> sendRegistrationSMS(String phoneNumber,String name,String abhaNumber){
        return sendSMS(NotificationType.SMS,
                NotificationContentType.INFO,
                phoneNumber,
                SMS_SUBJECT,
                templatesHelper.prepareRegistrationSMSMessage(1007164181688870515L, name,abhaNumber,ABHA_URL));
    }

    public Mono<NotificationResponseDto> sendSMS(NotificationType notificationType, NotificationContentType notificationContentType , String phoneNumber, String subject, String message) {
        return notificationClient.sendOtp(
                prepareNotificationRequest(notificationType,
                        notificationContentType.getValue(),
                        phoneNumber,
                        subject,
                        message));
    }

    private NotificationRequestDto prepareNotificationRequest(NotificationType notificationType, String contentType, String phoneNumber, String subject, String message) {
        NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
        notificationRequestDto.setOrigin(ORIGIN);
        notificationRequestDto.setType(Collections.singletonList(notificationType.getValue()));
        notificationRequestDto.setContentType(contentType);
        notificationRequestDto.setSender(SENDER);
        notificationRequestDto.setReceiver(Collections.singletonList(new KeyValue(MOBILE_KEY, phoneNumber)));
        List<KeyValue> notification = new LinkedList<>();
        notification.add(new KeyValue(TEMPLATE_ID, message));
        notification.add(new KeyValue(SUBJECT, subject));
        notification.add(new KeyValue(CONTENT, message));
        notificationRequestDto.setNotification(notification);
        return notificationRequestDto;
    }

    public Mono<NotificationResponseDto> sendEmailOtp(String email, String subject, String message) {
        return notificationClient.sendOtp(
                prepareEmailNotificationRequest(NotificationType.EMAIL,
                        NotificationContentType.OTP.getValue(),
                        email,
                        subject,
                        message));
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
