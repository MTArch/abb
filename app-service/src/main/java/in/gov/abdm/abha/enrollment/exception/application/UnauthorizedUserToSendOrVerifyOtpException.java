package in.gov.abdm.abha.enrollment.exception.application;

public class UnauthorizedUserToSendOrVerifyOtpException extends RuntimeException {

    public UnauthorizedUserToSendOrVerifyOtpException(String message) {
        super(message);
    }
}
