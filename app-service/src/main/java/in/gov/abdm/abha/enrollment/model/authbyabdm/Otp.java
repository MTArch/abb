package in.gov.abdm.abha.enrollment.model.authbyabdm;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.annotations.OtpValue;
import in.gov.abdm.abha.enrollment.validators.annotations.TimestampOtp;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidTransactionId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ValidTransactionId
@OtpValue
@TimestampOtp
/**
 * It is Otp class
 */
public class Otp {
    /**
     * It is timeStamp
     */
    @JsonProperty("timeStamp")
    @NotNull(message = AbhaConstants.VALIDATION_ERROR_TIMESTAMP_FIELD)
    public Date timeStamp;
    /**
     * It is txnId
     */
    @JsonProperty("txnId")
    @NotNull(message = AbhaConstants.VALIDATION_ERROR_TRANSACTION_FIELD)
    public String txnId;
    /**
     * It is otpValue
     */
    @JsonProperty("otpValue")
    @NotNull(message = AbhaConstants.VALIDATION_ERROR_OTP_VALUE_FIELD)
    public String otpValue;
}
