package in.gov.abdm.abha.enrollmentdb.domain.syncacknowledgement;

import in.gov.abdm.syncacknowledgement.SyncAcknowledgement;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.sql.Timestamp;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.PATIENT_SYNC_ACKNOWLEDGEMENT_UPDATE_QUERY;
import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.PHR_SYNC_ACKNOWLEDGEMENT_UPDATE_QUERY;

@Repository
public interface SyncAcknowledgmentRepository extends R2dbcRepository<SyncAcknowledgement, BigInteger> {
    @Query(value = PATIENT_SYNC_ACKNOWLEDGEMENT_UPDATE_QUERY)
    Mono<SyncAcknowledgement> updatePatientSyncAcknowledgment(@Param("isSyncedWithPatient") Boolean isSyncedWithPatient, @Param("updatedDate") Timestamp updatedDate, @Param("requestId") String requestId, @Param("healthIdNumber") String healthIdNumber);
    @Query(value = PHR_SYNC_ACKNOWLEDGEMENT_UPDATE_QUERY)
    Mono<SyncAcknowledgement> updatePhrSyncAcknowledgment(@Param("isSyncedWithPhr") Boolean isSyncedWithPhr, @Param("updatedDate") Timestamp updatedDate, @Param("requestId") String requestId, @Param("healthIdNumber") String healthIdNumber);
}
