package in.gov.abdm.abha.enrollment.exception.application;

import in.gov.abdm.error.ABDMError;

public class AbhaNotFountException extends RuntimeException {
    public AbhaNotFountException(String errorCode, String errorMessage) {
        super(errorCode + errorMessage);
    }

    public AbhaNotFountException(ABDMError errorCode) {
        super(errorCode.getCode() + errorCode.getMessage());
    }

    public AbhaNotFountException(String message){
        super(message);
    }
}
