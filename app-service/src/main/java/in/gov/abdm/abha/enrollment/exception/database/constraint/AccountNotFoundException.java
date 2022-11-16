package in.gov.abdm.abha.enrollment.exception.database.constraint;

public class AccountNotFoundException extends RuntimeException{

    private String message="";
    public AccountNotFoundException()
    {
        super();
    }
    public AccountNotFoundException(String message) {
        super(message);
    }
}
