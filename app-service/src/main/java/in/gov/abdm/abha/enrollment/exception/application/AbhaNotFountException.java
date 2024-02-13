package in.gov.abdm.abha.enrollment.exception.application;

public class AbhaNotFountException extends RuntimeException{
    public AbhaNotFountException(String errorCode, String errorMessage){
        super(errorCode+errorMessage);
    }
}
