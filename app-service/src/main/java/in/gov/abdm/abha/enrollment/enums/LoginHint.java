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
	PHR_ADDRESS("phr-address"),
	AADHAAR("aadhaar"),
	EMPTY(""),
	WRONG("wrong");
	
	private final String value;
	
	public static boolean isValid(String value) {
	    LoginHint[] values = LoginHint.values();
	    for (LoginHint loginHint : values) {
	        if (loginHint.toString().equals(value)) {
	            return true;
	        }
	    }
	    return false;
	}

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
