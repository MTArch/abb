package in.gov.abdm.abha.enrollmentdbtests.controller;

import in.gov.abdm.abha.enrollmentdb.controller.IntegratedProgramController;
import in.gov.abdm.abha.enrollmentdb.domain.integrated_program.IntegratedProgramServiceImpl;
import in.gov.abdm.abha.enrollmentdb.model.integrated_program.IntegratedProgram;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class IntegratedProgramControllerTests {
    @InjectMocks
    IntegratedProgramController integratedProgramController;
    @Mock
    IntegratedProgramServiceImpl integratedProgramService;
    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void getIntegratedProgramsTests(){
        Mockito.when(integratedProgramService.getIntegratedPrograms()).thenReturn(Flux.just(new IntegratedProgram()));
        ResponseEntity<Flux<IntegratedProgram>> response= integratedProgramController.getIntegratedPrograms();
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void getIntegratedProgramByBenefitNameTests(){
        Mockito.when(integratedProgramService.getIntegratedProgramByBenefitName(any())).thenReturn(Flux.just(new IntegratedProgram()));
        ResponseEntity<Flux<IntegratedProgram>> response= integratedProgramController.getIntegratedProgramByBenefitName("Test");
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
}
