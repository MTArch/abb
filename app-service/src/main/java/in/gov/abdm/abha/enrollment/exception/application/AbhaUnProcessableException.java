package in.gov.abdm.abha.enrollment.exception.application;

public class AbhaUnProcessableException extends RuntimeException{
    public AbhaUnProcessableException(String errorCode, String errorMessage){
        super(errorCode+errorMessage);
    }
}
