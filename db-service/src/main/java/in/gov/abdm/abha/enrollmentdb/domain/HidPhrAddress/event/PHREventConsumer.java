package in.gov.abdm.abha.enrollmentdb.domain.HidPhrAddress.event;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.MSG_ABHA_CONSUME_ACKNOWLEDGEMENT_FROM_PHR;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PHREventConsumer implements EventConsumer {

    @KafkaListener(topics = "${kafka.abha.phr.sync.ack.topic}", groupId = "${kafka.group.id}")
    @Override
    public void subscribe(@Payload String message, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String requestId) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            log.info(MSG_ABHA_CONSUME_ACKNOWLEDGEMENT_FROM_PHR + requestId);
        }
        catch (Exception exception) {
            log.error(exception.getMessage(),exception);
        }
    }
}
