package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.annotations.OtpValue;
import in.gov.abdm.abha.enrollment.validators.annotations.TimestampOtp;
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
    @NotNull(message = AbhaConstants.VALIDATION_ERROR_TIMESTAMP_FIELD)
    @TimestampOtp
    private String timeStamp;

    /**
     * It is Transection Id for validation
     */
    @JsonProperty("txnId")
    @NotNull(message = AbhaConstants.VALIDATION_ERROR_TRANSACTION_FIELD)
    @Uuid
    private String txnId;

    /**
     * It is otpvalue
     */
    @JsonProperty("otpValue")
    @NotNull(message = AbhaConstants.VALIDATION_ERROR_OTP_VALUE_FIELD)
    @OtpValue
    private String otpValue;

}
