package in.gov.abdm.abha.enrollment.exception.database.constraint;

public class ParentLinkingFailedException extends RuntimeException{

    private String message="";
    public ParentLinkingFailedException()
    {
        super();
    }
    public ParentLinkingFailedException(String message) {
        super(message);
    }
}
