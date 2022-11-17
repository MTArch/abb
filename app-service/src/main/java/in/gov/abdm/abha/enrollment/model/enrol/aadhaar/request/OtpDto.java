package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.validators.annotations.OtpValue;
import in.gov.abdm.abha.enrollment.validators.annotations.TimestampOtp;
import in.gov.abdm.abha.enrollment.validators.annotations.Uuid;
import lombok.Data;

/**
 * It is Data Transfer Object for Otp
 */
@TimestampOtp
@Uuid
@OtpValue
@Data
public class OtpDto {
    /**
     * It is date
     */
    @JsonProperty("timeStamp")
    private String timeStamp;

    /**
     * It is Transection Id for validation
     */
    @JsonProperty("txnId")
    private String txnId;

    /**
     * It is otpvalue
     */

    @JsonProperty("otpValue")
    private String otpValue;

    private String mobile;
}
