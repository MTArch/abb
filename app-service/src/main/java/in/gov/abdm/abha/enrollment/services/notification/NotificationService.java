package in.gov.abdm.abha.enrollment.services.notification;

import in.gov.abdm.abha.enrollment.client.NotificationFClient;
import in.gov.abdm.abha.enrollment.exception.notification.NotificationGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.notification.*;
import in.gov.abdm.abha.enrollment.utilities.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    public static final String ORIGIN = "abha";
    public static final String SENDER = "NHASMS";
    public static final String MOBILE_KEY = "mobile";
    public static final String TEMPLATE_ID = "templateId";
    public static final String SUBJECT = "subject";
    public static final String CONTENT = "content";

    public static final String EMAIL_KEY = "emailId";

    @Autowired
    NotificationFClient notificationFClient;

    public Mono<NotificationResponseDto> sendSMSOtp(String phoneNumber, String subject, String message) {
        return notificationFClient.sendOtp(
                prepareNotificationRequest(NotificationType.SMS,
                        NotificationContentType.OTP.getValue(),
                        phoneNumber,
                        subject,
                        message), UUID.randomUUID().toString(), Common.timeStampWithT())
                .doOnError((throwable->Mono.error(new NotificationGatewayUnavailableException())));
    }

    private NotificationRequestDto prepareNotificationRequest(NotificationType notificationType, String contentType, String phoneNumber, String subject, String message) {
        NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
        notificationRequestDto.setOrigin(ORIGIN);
        notificationRequestDto.setType(Collections.singletonList(notificationType.getValue()));
        notificationRequestDto.setContentType(contentType);
        notificationRequestDto.setSender(SENDER);
        notificationRequestDto.setReceiver(Collections.singletonList(new KeyValue(MOBILE_KEY, phoneNumber)));
        List<KeyValue> notification = new LinkedList<>();
        notification.add(new KeyValue(TEMPLATE_ID, "1007164181681962323"));
        notification.add(new KeyValue(SUBJECT, subject));
        notification.add(new KeyValue(CONTENT, message));
        notificationRequestDto.setNotification(notification);
        return notificationRequestDto;
    }

    public Mono<NotificationResponseDto> sendEmailOtp(String email, String subject, String message) {
        return notificationFClient.sendOtp(
                prepareEmailNotificationRequest(NotificationType.EMAIL,
                        NotificationContentType.OTP.getValue(),
                        email,
                        subject,
                        message),UUID.randomUUID().toString(), Common.timeStampWithT())
                .doOnError((throwable->Mono.error(new NotificationGatewayUnavailableException())));
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
