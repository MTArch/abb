package in.gov.abdm.abha.enrollment.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * defines Enum values for LoginHint
 */
@Getter
@AllArgsConstructor
public enum LoginHint {
	
	ABHA_NUMBER("abha-number"),
	MOBILE("mobile"),

	ENROLLMENT("enrollment"),
	EMAIL("email"),
	PHR_ADDRESS("phr-address"),
	AADHAAR("aadhaar"),
	WRONG("wrong");
	
	private final String value;
	
	@JsonCreator
	public static LoginHint fromText(String text){
	    for(LoginHint loginHint : LoginHint.values()){
	        if(loginHint.getValue().equals(text)){
	            return loginHint;
	        }
	    }
	    return LoginHint.WRONG;
	}

}
