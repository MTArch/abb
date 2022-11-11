package in.gov.abdm.abha.enrollment.enums.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * defines Enum values for OtpSystem
 */

@Getter
@AllArgsConstructor
@ToString
public enum OtpSystem {
    AADHAAR("aadhaar"),
    ABDM("abdm"),
    ABHA("abha"),
    WRONG("wrong");

    private final String value;

    public static boolean isValid(String value) {
        OtpSystem[] values = OtpSystem.values();
        for (OtpSystem otpSystem : values) {
            if (otpSystem.toString().equals(value)) {
                return true;
            }
        }
        return false;
    }

    @JsonCreator
    public static OtpSystem fromText(String text){
        for(OtpSystem otpSystem : OtpSystem.values()){
            if(otpSystem.getValue().equals(text)){
                return otpSystem;
            }
        }
        return OtpSystem.WRONG;
    }
}
