package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request;

import in.gov.abdm.abha.enrollment.validators.annotations.OtpValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.FIELD_BLANK_ERROR_MSG;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * It is Data Transfer Object for AuthMethod
 */
public class AuthMethodsDto {
    /**
     * 6 digit OTP
     */
    @NotNull(message = FIELD_BLANK_ERROR_MSG)
    private String otp;
    /**
     * It Is Pi
     */
    @NotNull(message = FIELD_BLANK_ERROR_MSG)
    private String pi;
}
