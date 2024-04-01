package in.gov.abdm.abha.enrollmentdbtests.domain.integrated_program;

import in.gov.abdm.abha.enrollmentdb.domain.integrated_program.IntegratedProgramServiceImpl;
import in.gov.abdm.abha.enrollmentdb.model.hid_phr_address.HidPhrAddress;
import in.gov.abdm.abha.enrollmentdb.model.hid_phr_address.HidPhrAddressDto;
import in.gov.abdm.abha.enrollmentdb.model.integrated_program.IntegratedProgram;
import in.gov.abdm.abha.enrollmentdb.repository.IntegratedProgramRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class IntegratedProgramServiceImplTests {
    @InjectMocks
    IntegratedProgramServiceImpl integratedProgramService;
    @Mock
    IntegratedProgramRepository integratedProgramRepository;

    @BeforeEach
    void setup() {
        IntegratedProgram integratedProgram=new IntegratedProgram("","","","","","","", LocalDateTime.now(),LocalDateTime.now(),"","",true);
        integratedProgram.getId();
        integratedProgram.isNew();
        integratedProgram.setAsNew();
        IntegratedProgram i = new IntegratedProgram();
        i.setIntegratedProgramId(integratedProgram.getIntegratedProgramId());
        i.setProgramName(integratedProgram.getProgramName());
        i.setBenefitName(integratedProgram.getBenefitName());
        i.setClientId(integratedProgram.getClientId());
        i.setEndPointUrlSearch(integratedProgram.getEndPointUrlSearch());
        i.setEndPointUrlLink(integratedProgram.getEndPointUrlLink());
        i.setDescription(integratedProgram.getDescription());
        i.setCreatedDate(integratedProgram.getCreatedDate());
        i.setCreatedBy(integratedProgram.getCreatedBy());
        i.setUpdatedDate(integratedProgram.getUpdatedDate());
        i.setUpdatedBy(integratedProgram.getUpdatedBy());
        i.setNewIntegratedProgram(integratedProgram.isNewIntegratedProgram());
        MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void teardown() {
    }
    @Test
    public void getIntegratedProgramsTests(){
         Mockito.when(integratedProgramRepository.findAll()).thenReturn(Flux.just(new IntegratedProgram()));
        StepVerifier.create(integratedProgramService.getIntegratedPrograms()).expectNextCount(1L).verifyComplete();
    }
    @Test
    public void getIntegratedProgramByBenefitNameTests(){
        Mockito.when(integratedProgramRepository.findByBenefitNameIgnoreCase(any())).thenReturn(Flux.just(new IntegratedProgram()));
        StepVerifier.create(integratedProgramService.getIntegratedProgramByBenefitName("Test")).expectNextCount(1L).verifyComplete();
    }

}
