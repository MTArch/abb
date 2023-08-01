package in.gov.abdm.abha.enrollment.model.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendNotificationRequestDto {

    private String abhaNumber;
    private List<NotificationType> notificationType;
    private String type;
}