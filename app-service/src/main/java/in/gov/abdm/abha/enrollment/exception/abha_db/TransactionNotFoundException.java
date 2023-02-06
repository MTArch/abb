package in.gov.abdm.abha.enrollment.exception.abha_db;

public class TransactionNotFoundException extends RuntimeException {

	
	private static final long serialVersionUID = 4151569448693017560L;

	public TransactionNotFoundException() {
		super();
	}

	public TransactionNotFoundException(String message) {
		super(message);
	}
}
