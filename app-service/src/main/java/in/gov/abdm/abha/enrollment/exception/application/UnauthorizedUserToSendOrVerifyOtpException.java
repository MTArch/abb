package in.gov.abdm.abha.enrollment.exception.application;

public class UnauthorizedUserToSendOrVerifyOtpException extends RuntimeException {

    public UnauthorizedUserToSendOrVerifyOtpException(String errorCode, String errorMessage) {
        super(errorCode+errorMessage);
    }
}
