package in.gov.abdm.abha.enrollmentdbtests.domain.syncacknowledgement;

import in.gov.abdm.abha.enrollmentdb.domain.syncacknowledgement.SyncAcknowledgementServiceImpl;
import in.gov.abdm.abha.enrollmentdb.domain.syncacknowledgement.SyncAcknowledgmentRepository;
import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import in.gov.abdm.syncacknowledgement.SyncAcknowledgement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.sql.Timestamp;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class SyncAcknowledgementServiceImplTests {
    @InjectMocks
    SyncAcknowledgementServiceImpl syncAcknowledgementService;
    @Mock
    private SyncAcknowledgmentRepository syncAcknowledgmentRepository;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void tearDown() {

    }
    @Test
    public void publishPhrUserPatientEventTests(){
        Mockito.when(syncAcknowledgmentRepository.save(any())).thenReturn(Mono.just(new SyncAcknowledgement()));
        StepVerifier.create(syncAcknowledgementService.addNewAcknowledgement("test",new Timestamp(1L), new SyncAcknowledgement())).expectNext(new SyncAcknowledgement()).verifyComplete();
    }
    @Test
    public void updatePatientAcknowledgementTests(){
        Mockito.when(syncAcknowledgmentRepository.updatePatientSyncAcknowledgment(any(),any(),any(),any())).thenReturn(Mono.just(new SyncAcknowledgement()));
        StepVerifier.create(syncAcknowledgementService.updatePatientAcknowledgement("test",new Timestamp(1L), new SyncAcknowledgement())).expectNext(new SyncAcknowledgement()).verifyComplete();
    }
    @Test
    public void updatePhrAcknowledgementTests(){
        Mockito.when(syncAcknowledgmentRepository.updatePhrSyncAcknowledgment(any(),any(),any(),any())).thenReturn(Mono.just(new SyncAcknowledgement()));
        StepVerifier.create(syncAcknowledgementService.updatePhrAcknowledgement("test",new Timestamp(1L), new SyncAcknowledgement())).expectNext(new SyncAcknowledgement()).verifyComplete();
    }
}
