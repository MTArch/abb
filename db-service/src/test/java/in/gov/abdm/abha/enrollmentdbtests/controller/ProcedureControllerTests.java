package in.gov.abdm.abha.enrollmentdbtests.controller;

import in.gov.abdm.abha.enrollmentdb.controller.ProcedureController;
import in.gov.abdm.abha.enrollmentdb.domain.procedure.ProcedureService;
import in.gov.abdm.abha.enrollmentdb.model.procedure.SaveAllDataRequest;
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
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class ProcedureControllerTests {
    @InjectMocks
    ProcedureController procedureController;
    @Mock
    ProcedureService procedureService;
    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void createAccountTests(){
        Mockito.when(procedureService.saveAllData(any())).thenReturn(Mono.just("success"));
        ResponseEntity<Mono<String>> response= procedureController.createAccount(new SaveAllDataRequest());
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
}
