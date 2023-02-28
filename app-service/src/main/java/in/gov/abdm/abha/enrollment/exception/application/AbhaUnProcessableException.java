package in.gov.abdm.abha.enrollment.exception.application;

import in.gov.abdm.error.ABDMError;

public class AbhaUnProcessableException extends RuntimeException{
    public AbhaUnProcessableException(String errorCode, String errorMessage){
        super(errorCode+errorMessage);
    }
    public AbhaUnProcessableException(ABDMError abdmError){
        super(abdmError.getCode()+abdmError.getMessage());
    }
}
