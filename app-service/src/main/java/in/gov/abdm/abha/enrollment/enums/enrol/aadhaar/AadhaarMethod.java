package in.gov.abdm.abha.enrollment.enums.enrol.aadhaar;

public enum AadhaarMethod {
	
	 AADHAAR_OTP("OTP"),
	 AADHAAR_FID("FID"),
	 AADHAAR_IIR("IIR"), 
	 AADHAAR_FMR("FMR"),
	 DL("DL"),
	 AADHAAR_DEMO("DEMO");

	private String code;

	AadhaarMethod(String code) {

		this.code = code;
	}

	public String code() {
		return code;
	}

}
