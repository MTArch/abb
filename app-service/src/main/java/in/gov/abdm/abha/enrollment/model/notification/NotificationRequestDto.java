package in.gov.abdm.abha.enrollment.model.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * POJO To Accept SMS request body
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequestDto {

    /**
     * type of content otp/info/promo
     */
    private String origin;

    /**
     * notification type email/sms/both
     */
    private List<String> type;

    /**
     * type of content otp/info/promo
     */
    private String contentType;

    /**
     * sender details
     */

    private String sender;

    /**
     * This list will contain emails and mobiles numbers for receivers
     */
    private List<KeyValue> receiver;

    /**
     * This will contain message body, subject and template
     */
    private List<KeyValue> notification;

}




