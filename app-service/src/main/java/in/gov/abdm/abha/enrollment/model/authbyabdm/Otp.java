package in.gov.abdm.abha.enrollment.model.authbyabdm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
/**
 * It is Otp class
 */
public class Otp {
    /**
     * It is timeStamp
     */
    @JsonProperty("timeStamp")
    public Date timeStamp;
    /**
     * It is txnId
     */
    @JsonProperty("txnId")
    public String txnId;
    /**
     * It is otpValue
     */
    @JsonProperty("otpValue")
    public String otpValue;
}
