package in.gov.abdm.abha.enrollment.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountStatus {
	ACTIVE("ACTIVE"),IN_ACTIVE("IN_ACTIVE"), BLOCKED("BLOCKED"), LOCKED("LOCKED"), CONSENT_PENDING("CONSENT_PENDING"), 
	DEACTIVATED("DEACTIVATED"), DELETED("DELETED"), DELINKED("DELINKED"),
	PARENT_LINKING_PENDING("PARENT_LINKING_PENDING");

	private final String value;
	
	public static boolean isValid(String status) {
		AccountStatus[] values = AccountStatus.values();
		for (AccountStatus auth : values) {
			if (auth.toString().equals(status)) {
				return true;
			}
		}
		return false;
	}
	
	@JsonCreator
	public static AccountStatus fromText(String text){
	    for(AccountStatus accountStatus : AccountStatus.values()){
	        if(accountStatus.getValue().equals(text)){
	            return accountStatus;
	        }
	    }
	    return AccountStatus.IN_ACTIVE;
	}
}
