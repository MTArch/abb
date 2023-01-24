package in.gov.abdm.abha.enrollmentdb.domain.HidPhrAddress.event;

public interface EventConsumer {
    void subscribe(String message, String requestId);
}
