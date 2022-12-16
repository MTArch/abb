package in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.annotations.Uuid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * It is an Pojo class IdpVerifyOtpRequest
 */
public class IdpVerifyOtpRequest {

    /**
     * transaction id
     */
    @JsonProperty("txnId")
    @NotNull(message = AbhaConstants.VALIDATION_ERROR_TRANSACTION_FIELD)
    @Uuid
    private String txnId;

    /**
     * otp for verification
     */
    @JsonProperty("otp")
    @NotNull(message = AbhaConstants.VALIDATION_ERROR_OTP_VALUE_FIELD)
    private String otp;
}
