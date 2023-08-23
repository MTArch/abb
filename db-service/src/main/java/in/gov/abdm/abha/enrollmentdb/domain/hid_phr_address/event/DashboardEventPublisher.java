package in.gov.abdm.abha.enrollmentdb.domain.hid_phr_address.event;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.MSG_ABHA_PUBLISH_TO_DASHBOARD;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import in.gov.abdm.abha.enrollmentdb.model.account.AccountReattemptDto;
import in.gov.abdm.hiecm.userinitiatedlinking.Patient;
import in.gov.abdm.phr.enrollment.user.User;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DashboardEventPublisher implements EventPublisher {

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${kafka.abha.dashboard.sync.topic}")
	private String dashboardTopic;

	@Override
	public void publish(AccountReattemptDto aReattemptDto, String requestId) {

		try {
			kafkaTemplate.send(dashboardTopic, requestId, aReattemptDto);
			log.info(MSG_ABHA_PUBLISH_TO_DASHBOARD);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
		}
	}

	
	@Override
	public void publish(User user, String requestId) {
		 // Unused implementation of the method as ABHA does not publish a user object to PHR system.

	}

	@Override
	public void publish(Patient patient, String requestId) {
		 // Unused implementation of the method as ABHA does not publish a user object to HIECM system.

	}

}
