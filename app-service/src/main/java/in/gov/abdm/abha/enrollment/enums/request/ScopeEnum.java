package in.gov.abdm.abha.enrollment.enums.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * defines Enum values for ScopeEnum
 */

@Getter
@AllArgsConstructor
@ToString
public enum ScopeEnum {
    ABHA_ENROL("abha-enrol"),
    MOBILE_UPDATE("mobile-update"),
    MOBILE_VERIFY("mobile-verify"),
    EMAIL_UPDATE("email-update"),
    WRONG("wrong");

    private final String value;

    @JsonCreator
    public static ScopeEnum fromText(String text){
        for(ScopeEnum scopeEnum : ScopeEnum.values()){
            if(scopeEnum.getValue().equals(text)){
                return scopeEnum;
            }
        }
        return ScopeEnum.WRONG;
    }
}
