package in.gov.abdm.abha.enrollmentdb.domain.hid_phr_address.event;

import in.gov.abdm.abha.enrollmentdb.model.account.AccountReattemptDto;
import in.gov.abdm.hiecm.userinitiatedlinking.Patient;
import in.gov.abdm.phr.enrollment.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.*;

@Service
@Slf4j
public class PHREventPublisher implements EventPublisher {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.abha.phr.sync.topic}")
    private String userTopic;

    @Override
    public void publish(User user, String requestId) {
        String header;
        if(user.isNew()) {
            header = requestId + UNDERSCORE_NEW;
        }
        else {
            header = requestId;
        }
        try {
            log.info(MSG_ABHA_PUBLISH_USER_TO_PHR, user.getHealthIdNumber(), requestId);
            kafkaTemplate.send(userTopic, header, user);
            log.info(MSG_ABHA_PUBLISH_USER_SUCCESS_TO_PHR);
        }
        catch (Exception exception) {
            log.error(exception.getMessage(),exception);
        }
    }

    @Override
    public void publish(Patient patient, String requestId) {
        // Unused implementation of the method as ABHA does not publish a patient object to PHR system.
    }

	@Override
	public void publish(AccountReattemptDto patient, String requestId) {
		// TODO Auto-generated method stub
		
	}
}
