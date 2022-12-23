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
public enum Scopes {
    ABHA_ENROL("abha-enrol"),
    MOBILE_UPDATE("mobile-update"),
    MOBILE_VERIFY("mobile-verify"),
    EMAIL_UPDATE("email-update"),
    CHILD_ABHA_ENROL("child-abha-enrol"),
    PARENT_ABHA_LINK("parent-abha-link"),
    DL_FLOW("dl-flow"),
    WRONG("wrong");

    private final String value;

    @JsonCreator
    public static Scopes fromText(String text){
        for(Scopes scopes : Scopes.values()){
            if(scopes.getValue().equals(text)){
                return scopes;
            }
        }
        return Scopes.WRONG;
    }
}
