package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.validators.annotations.OtpValue;
import in.gov.abdm.abha.enrollment.validators.annotations.Timestamp;
import in.gov.abdm.abha.enrollment.validators.annotations.Uuid;
import lombok.Data;

/**
 * It is Data Transfer Object for Otp
 */
@Data
public class OtpDto {
    /**
     * It is date
     */
    @JsonProperty("timeStamp")
    @Timestamp
    private String timeStamp;
    /**
     * It is Transection Id for validation
     */

    @JsonProperty("txnId")
    @Uuid
    private String txnId;
    /**
     * It is otpvalue
     */

    @JsonProperty("otpValue")
    @OtpValue
    private String otpValue;
}
