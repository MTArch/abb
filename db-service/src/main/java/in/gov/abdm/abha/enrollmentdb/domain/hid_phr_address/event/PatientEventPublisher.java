package in.gov.abdm.abha.enrollmentdb.domain.hid_phr_address.event;

import in.gov.abdm.abha.enrollmentdb.model.account.AccountReattemptDto;
import in.gov.abdm.hiecm.userinitiatedlinking.Patient;
import in.gov.abdm.phr.enrollment.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.MSG_ABHA_PUBLISH_PATIENT_TO_HIECM;
import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.UNDERSCORE_NEW;

@Service
@Slf4j
public class PatientEventPublisher implements EventPublisher {
	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${kafka.abha.patient.sync.topic}")
	private String patientTopic;

	@Override
	public void publish(Patient patient, String requestId) {
		String header;
		if (patient.isNew()) {
			header = requestId + UNDERSCORE_NEW;
		} else {
			header = requestId;
		}
		try {
			kafkaTemplate.send(patientTopic, header, patient);
			log.info(MSG_ABHA_PUBLISH_PATIENT_TO_HIECM);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
		}
	}

	@Override
	public void publish(User user, String requestId) {
		// Unused implementation of the method as ABHA does not publish a user object to
		// HIECM system.
	}

	@Override
	public void publish(AccountReattemptDto patient, String requestId) {
		// TODO Auto-generated method stub

	}
}
