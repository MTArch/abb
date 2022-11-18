package in.gov.abdm.abha.enrollment.model.authbyabdm;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.annotations.AuthMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;

/**
 * It is a Pojo class Authdata
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@AuthMethod
@in.gov.abdm.abha.enrollment.validators.annotations.Otp
public class AuthData {
    /**
     * It is authMethods
     */
    @JsonProperty("authMethods")
    @NotEmpty(message = AbhaConstants.VALIDATION_EMPTY_AUTHMETHOD)
    public ArrayList<String> authMethods;
    /**
     * It is otp
     */
    @JsonProperty("otp")
    @NotNull(message = AbhaConstants.VALIDATION_ERROR_OTP_OBJECT)
    public Otp otp;
}
