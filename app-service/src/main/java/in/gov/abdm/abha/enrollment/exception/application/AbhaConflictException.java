package in.gov.abdm.abha.enrollment.exception.application;

public class AbhaConflictException extends RuntimeException{
    public AbhaConflictException(String errorCode, String errorMessage){
        super(errorCode+errorMessage);
    }
}
