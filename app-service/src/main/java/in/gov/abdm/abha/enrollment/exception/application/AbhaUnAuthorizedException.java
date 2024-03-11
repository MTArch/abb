package in.gov.abdm.abha.enrollment.exception.application;

import in.gov.abdm.error.ABDMError;

public class AbhaUnAuthorizedException extends RuntimeException {
    public AbhaUnAuthorizedException(ABDMError abdmError) {
        super(abdmError.getCode() + abdmError.getMessage());
    }
}
