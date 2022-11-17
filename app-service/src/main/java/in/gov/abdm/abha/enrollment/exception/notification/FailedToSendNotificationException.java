package in.gov.abdm.abha.enrollment.exception.notification;

public class FailedToSendNotificationException extends RuntimeException{
    public FailedToSendNotificationException(String message){
        super(message);
    }
}
