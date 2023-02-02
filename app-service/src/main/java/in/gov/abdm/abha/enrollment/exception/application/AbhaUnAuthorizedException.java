package in.gov.abdm.abha.enrollment.exception.application;

public class AbhaUnAuthorizedException extends RuntimeException{
    public AbhaUnAuthorizedException(String errorCode, String errorMessage){
        super(errorCode+errorMessage);
    }
}
