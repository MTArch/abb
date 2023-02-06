package in.gov.abdm.abha.enrollment.exception.application;

public class AbhaOkException extends RuntimeException{
    public AbhaOkException(String errorCode, String errorMessage){
        super(errorCode+errorMessage);
    }
}
