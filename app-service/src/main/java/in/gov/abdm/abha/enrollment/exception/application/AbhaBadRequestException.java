package in.gov.abdm.abha.enrollment.exception.application;

public class AbhaBadRequestException extends RuntimeException{
    public AbhaBadRequestException(String errorCode, String errorMessage){
        super(errorCode+errorMessage);
    }
}
