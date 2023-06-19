package in.gov.abdm.abha.enrollment.model.notification;

import com.fasterxml.jackson.annotation.JsonCreator;
import in.gov.abdm.abha.profile.enums.request.Scopes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum NotificationType {
    SMS("sms"),
    EMAIL("email"),
    WRONG("wrong");
    private String value;
    @JsonCreator
    public static NotificationType fromText(String text) {
        for (NotificationType notificationType : NotificationType.values()) {
            if (notificationType.getValue().equals(text)) {
                return notificationType;
            }
        }
        return NotificationType.WRONG;
    }
}
