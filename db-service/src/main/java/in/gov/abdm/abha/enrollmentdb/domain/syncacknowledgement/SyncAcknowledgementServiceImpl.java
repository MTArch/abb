package in.gov.abdm.abha.enrollmentdb.domain.syncacknowledgement;

import in.gov.abdm.process.ProcessMethod;
import in.gov.abdm.syncacknowledgement.SyncAcknowledgement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.sql.Timestamp;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.*;

@Service
@Slf4j
public class SyncAcknowledgementServiceImpl implements SyncAcknowledgementService{
    @Autowired
    private SyncAcknowledgmentRepository syncAcknowledgmentRepository;

    private final ProcessMethod<SyncAcknowledgement> processMethod = new ProcessMethod<>();

    @Override
    public Mono<SyncAcknowledgement> addNewAcknowledgement(String requestId, Timestamp timestamp, SyncAcknowledgement syncAcknowledgement) {
        Mono<SyncAcknowledgement> syncAcknowledgementMono = processMethod.processMono(
                requestId, timestamp, ENROLLMENT_LOG_PREFIX, syncAcknowledgmentRepository.save(syncAcknowledgement.setAsNew()))
                    .flatMap(syncAcknowledgementWithId -> {
                        log.info(MSG_SYNC_ACKNOWLEDGMENT_ADDED_ABHA + syncAcknowledgementWithId.getRequestID());
                        return Mono.just(syncAcknowledgementWithId);
                    });
        syncAcknowledgementMono.subscribe();
        return syncAcknowledgementMono;
    }

    @Override
    public Mono<SyncAcknowledgement> updatePatientAcknowledgement(String requestId, Timestamp timestamp, SyncAcknowledgement syncAcknowledgement) {
        return processMethod.processMono(
                requestId, timestamp, ENROLLMENT_LOG_PREFIX,
                syncAcknowledgmentRepository.updatePatientSyncAcknowledgment(syncAcknowledgement.isSyncedWithPatient(), syncAcknowledgement.getUpdateDate(), syncAcknowledgement.getRequestID(), syncAcknowledgement.getHealthIdNumber()));
    }

    @Override
    public Mono<SyncAcknowledgement> updatePhrAcknowledgement(String requestId, Timestamp timestamp, SyncAcknowledgement syncAcknowledgement) {
        return processMethod.processMono(
                requestId, timestamp, ENROLLMENT_LOG_PREFIX,
                syncAcknowledgmentRepository.updatePhrSyncAcknowledgment(syncAcknowledgement.isSyncedWithPhr(), syncAcknowledgement.getUpdateDate(), syncAcknowledgement.getRequestID(), syncAcknowledgement.getHealthIdNumber()));
    }
}
