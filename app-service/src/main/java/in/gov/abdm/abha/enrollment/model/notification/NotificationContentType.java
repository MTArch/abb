package in.gov.abdm.abha.enrollment.model.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum NotificationContentType {
    OTP("otp");
    private String value;
}
