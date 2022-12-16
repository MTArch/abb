package in.gov.abdm.abha.enrollment.exception.database.constraint;

public class InvalidRequestException extends RuntimeException {

	
	private static final long serialVersionUID = 4151569448693017560L;

	public InvalidRequestException() {
		super();
	}

	public InvalidRequestException(String message) {
		super(message);
	}
}
