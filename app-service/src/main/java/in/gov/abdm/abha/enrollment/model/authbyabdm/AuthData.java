package in.gov.abdm.abha.enrollment.model.authbyabdm;

import java.util.ArrayList;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.validators.annotations.AuthMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * It is a Pojo class Authdata
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@AuthMethod
@in.gov.abdm.abha.enrollment.validators.annotations.Otp
public class AuthData {
	
    /**
     * It is authMethods
     */
    @JsonProperty("authMethods")
    @NotEmpty(message = AbhaConstants.VALIDATION_EMPTY_AUTHMETHOD)
    private ArrayList<AuthMethods> authMethods;
    
    /**
     * It is otp
     */
    @JsonProperty("otp")
    @NotNull(message = AbhaConstants.VALIDATION_ERROR_OTP_OBJECT)
    public Otp otp;
}