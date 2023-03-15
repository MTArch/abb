package in.gov.abdm.abha.enrollment.exception.application;

import in.gov.abdm.error.ABDMError;

public class AbhaBadRequestException extends RuntimeException {
    public AbhaBadRequestException(ABDMError abdmError) {
        super(abdmError.getCode() + abdmError.getMessage());
    }

    public AbhaBadRequestException(String errorCode, String errorMessage) {
        super(errorCode + errorMessage);
    }
}
