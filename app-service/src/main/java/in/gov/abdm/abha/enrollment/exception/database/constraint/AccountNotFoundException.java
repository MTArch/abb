package in.gov.abdm.abha.enrollment.exception.database.constraint;

public class AccountNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 4151569448693017560L;

	public AccountNotFoundException() {
		super();
	}

	public AccountNotFoundException(String message) {
		super(message);
	}
}
