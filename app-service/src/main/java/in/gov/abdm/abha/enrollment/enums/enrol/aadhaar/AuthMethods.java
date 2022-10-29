package in.gov.abdm.abha.enrollment.enums.enrol.aadhaar;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * define enum values for AuthMthods
 */
@Getter
@AllArgsConstructor
@ToString
public enum AuthMethods {
    OTP("otp"),
    PI("pi"),
    WRONG("wrong"),
    EMPTY("");
    private final String value;

    @JsonCreator
    public static AuthMethods fromText(String text){
        for(AuthMethods r : AuthMethods.values()){
            if(r.getValue().equals(text)){
                return r;
            }
        }
        return AuthMethods.WRONG;
    }
}
