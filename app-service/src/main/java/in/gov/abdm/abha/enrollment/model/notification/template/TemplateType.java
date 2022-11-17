package in.gov.abdm.abha.enrollment.model.notification.template;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * List of Template Types, whether it is sms notification or email Notification
 */
@Getter
@AllArgsConstructor
public enum TemplateType {
    EMAIL_NOTIFY(1), EMAIL_OTP(2), SMS_NOTIFY(3), SMS_OTP(4);
    private final int value;
}
