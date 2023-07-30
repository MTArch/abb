package in.gov.abdm.abha.enrollmentdb.domain.hid_phr_address.event;

public interface EventConsumer {
    void subscribe(String message, String requestId);
}
