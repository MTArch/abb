package in.gov.abdm.abha.enrollment.model.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyValue {

    /**
     * key to identify weather email or mobile receiver
     */
    private String key;
    /**
     * value will contain email id or mobile number
     */
    private String value;

}