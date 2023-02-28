package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.demographic.Demographic;
import in.gov.abdm.abha.enrollment.validators.annotations.AuthMethod;
import in.gov.abdm.abha.enrollment.validators.annotations.Otp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.ArrayList;

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


    /**
     * It is Demo
     */
    @JsonProperty("demo")
    @Valid
    private Demographic demographic;

    /**
     * It is Bio
     */
    @JsonProperty("bio")
    @Valid
    private BioDto bio;
}
