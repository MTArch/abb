package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request;
import java.util.ArrayList;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.validators.annotations.AuthMethod;
import in.gov.abdm.abha.enrollment.validators.annotations.Otp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Otp
@AuthMethod
public class AuthData {

    /**
     * enum list for authmethods
     */
    @JsonProperty("authMethods")
    ArrayList<AuthMethods> authMethods;


    /**
     * It is Otp
     */
    @JsonProperty("otp")
    @Valid
    private OtpDto otp;

   
}
