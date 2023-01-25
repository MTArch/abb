package in.gov.abdm.abha.enrollmentdb.domain.syncacknowledgement;

import in.gov.abdm.syncacknowledgement.SyncAcknowledgement;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;

public interface SyncAcknowledgementService {
    Mono<SyncAcknowledgement> addNewAcknowledgement(String requestId, Timestamp timestamp, SyncAcknowledgement syncAcknowledgement);
    Mono<SyncAcknowledgement> updatePatientAcknowledgement(String requestId, Timestamp timestamp, SyncAcknowledgement syncAcknowledgement);
    Mono<SyncAcknowledgement> updatePhrAcknowledgement(String requestId, Timestamp timestamp, SyncAcknowledgement syncAcknowledgement);
}
