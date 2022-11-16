package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.OtpDto;
import lombok.Data;

import java.util.ArrayList;

@Data
public class AuthData {

    @JsonProperty("authMethods")
    ArrayList<AuthMethods> authMethods;

    @JsonProperty("otp")
    private OtpDto otp;
}
