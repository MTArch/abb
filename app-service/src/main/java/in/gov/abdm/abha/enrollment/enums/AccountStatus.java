package in.gov.abdm.abha.enrollment.enums;

public enum AccountStatus {
	ACTIVE("ACTIVE"), BLOCKED("BLOCKED"), LOCKED("LOCKED"), CONSENTPENDING("CONSENTPENDING"), 
	DEACTIVATED("DEACTIVATED"), DELETED("DELETED"), DELINKED("DELINKED"),
	PARENT_LINKING_PENDING("PARENT_LINKING_PENDING");

	public static boolean isValid(String status) {
		AccountStatus[] values = AccountStatus.values();
		for (AccountStatus auth : values) {
			if (auth.toString().equals(status)) {
				return true;
			}
		}
		return false;
	}

	private String value;

	private AccountStatus(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}
}
