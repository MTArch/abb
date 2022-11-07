package in.gov.abdm.abha.enrollment.exception.database.constraint;

public class DatabaseConstraintFailedException extends RuntimeException{

    private String message="";

    public DatabaseConstraintFailedException()
    {
        super();
    }

    public DatabaseConstraintFailedException(String message) {
        super(message);
    }
}
