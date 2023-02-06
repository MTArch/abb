package in.gov.abdm.abha.enrollmentdb.domain.HidPhrAddress.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.abdm.hiecm.userinitiatedlinking.Patient;
import in.gov.abdm.phr.enrollment.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.MSG_ABHA_PUBLISH_USER_SUCCESS_TO_PHR;
import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.MSG_ABHA_PUBLISH_USER_TO_PHR;

@Service
@Slf4j
public class PHREventPublisher implements EventPublisher {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.abha.phr.sync.topic}")
    private String userTopic;
    @Override

    public void publish(User user, String requestId) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            log.info(MSG_ABHA_PUBLISH_USER_TO_PHR);
            kafkaTemplate.send(userTopic, requestId, user);
            log.info(MSG_ABHA_PUBLISH_USER_SUCCESS_TO_PHR);
        }
        catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }

    @Override
    public void publish(Patient patient, String requestId) {

    }
}
