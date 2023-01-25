package in.gov.abdm.abha.enrollmentdb.domain.HidPhrAddress.event;

import in.gov.abdm.hiecm.userinitiatedlinking.Patient;
import in.gov.abdm.phr.enrollment.user.User;

public interface EventPublisher {
    void publish(User user, String requestId);
    void publish(Patient patient, String requestId);
}
