package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request;

import java.util.ArrayList;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.OtpDto;
import in.gov.abdm.abha.enrollment.validators.annotations.AuthMethod;
import lombok.Data;

@Data
@AuthMethod
public class AuthData {

    @JsonProperty("authMethods")
    @NotEmpty(message = AbhaConstants.VALIDATION_EMPTY_AUTHMETHOD)
    @NotNull(message = AbhaConstants.VALIDATION_EMPTY_AUTHMETHOD)
    ArrayList<AuthMethods> authMethods;

    @JsonProperty("otp")
    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_OTP_VALUE_FIELD)
    @NotNull(message = AbhaConstants.VALIDATION_ERROR_OTP_VALUE_FIELD)
    private OtpDto otp;
}
