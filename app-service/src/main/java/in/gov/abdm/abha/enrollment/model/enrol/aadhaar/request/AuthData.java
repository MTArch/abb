package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.validators.annotations.Otp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Otp
public class AuthData {
    @JsonProperty("authMethods")
    @NotEmpty(message = AbhaConstants.VALIDATION_EMPTY_AUTHMETHOD)
    ArrayList<AuthMethods> authMethods;
    /**
     * It is token
     */
//    @NotNull(message = FIELD_BLANK_ERROR_MSG)
    @JsonProperty("token")
    private TokenDto token;
    /**
     * It is Demo
     */
//    @NotNull(message = FIELD_BLANK_ERROR_MSG)
    @JsonProperty("demo")
    private DemoDto demo;
    /**
     * It is Otp
     */
//    @NotNull(message = FIELD_BLANK_ERROR_MSG)
    @JsonProperty("otp")
    @Valid
    private OtpDto otp;
    /*
     * It is Bio
     */
    //@NotNull(message = FIELD_BLANK_ERROR_MSG)
    @JsonProperty("bio")
    private BioDto bio;
}
