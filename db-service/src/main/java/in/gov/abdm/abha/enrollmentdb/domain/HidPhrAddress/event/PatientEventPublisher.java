package in.gov.abdm.abha.enrollmentdb.domain.HidPhrAddress.event;

import in.gov.abdm.hiecm.userinitiatedlinking.Patient;
import in.gov.abdm.phr.enrollment.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.MSG_ABHA_PUBLISH_PATIENT_TO_HIECM;

@Service
@Slf4j
public class PatientEventPublisher implements EventPublisher {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.abha.patient.sync.topic}")
    private String patientTopic;

    @Override
    public void publish(User user, String requestId) {

    }

    @Override
    public void publish(Patient patient, String requestId) {
        try {
            kafkaTemplate.send(patientTopic, requestId, patient);
            log.info(MSG_ABHA_PUBLISH_PATIENT_TO_HIECM);
        }
        catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }
}
