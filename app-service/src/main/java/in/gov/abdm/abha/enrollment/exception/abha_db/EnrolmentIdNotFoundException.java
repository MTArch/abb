package in.gov.abdm.abha.enrollment.exception.abha_db;

public class EnrolmentIdNotFoundException extends RuntimeException {


	private static final long serialVersionUID = 4151569448693017560L;

	public EnrolmentIdNotFoundException() {
		super();
	}

	public EnrolmentIdNotFoundException(String message) {
		super(message);
	}


}
