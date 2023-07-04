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
    DEMO("demo"),
    PI("pi"),
    FACE("face"),
    BIO("bio"),
    IRIS("iris"),
    WRONG("wrong");
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
