package in.gov.abdm.abha.enrollmentdb.domain.HidPhrAddress.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import in.gov.abdm.abha.enrollmentdb.domain.syncacknowledgement.SyncAcknowledgementService;
import in.gov.abdm.syncacknowledgement.SyncAcknowledgement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.*;

@Service
@Slf4j
public class PatientEventConsumer implements EventConsumer {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private SyncAcknowledgementService syncAcknowledgementService;

    @KafkaListener(topics = "${kafka.abha.patient.sync.ack.topic}", groupId = "${kafka.group.id}")
    @Override
    public void subscribe(@Payload String message, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String requestId) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            SyncAcknowledgement syncAcknowledgement = mapper.readValue(message, SyncAcknowledgement.class);
            log.info(MSG_ABHA_CONSUME_ACKNOWLEDGEMENT_FROM_HIECM + requestId);
//            syncAcknowledgementService.updatePatientAcknowledgement(requestId, Timestamp.valueOf(LocalDateTime.now()),syncAcknowledgement)
//                    .subscribe(syncAcknowledgementUpdated -> {
//                        log.info(MSG_ABHA_CONSUME_ACKNOWLEDGEMENT_SUCCESS_FROM_HIECM + syncAcknowledgementUpdated.getHidPhrAddress());
//                    });
        }
        catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }
}
