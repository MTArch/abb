package in.gov.abdm.abha.enrollmentdbtests.controller;

import in.gov.abdm.abha.enrollmentdb.controller.HidBenefitController;
import in.gov.abdm.abha.enrollmentdb.domain.hidbenefit.HidBenefitService;
import in.gov.abdm.abha.enrollmentdb.model.hidbenefit.HidBenefitDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class HidBenefitControllerTests {
    @InjectMocks
    HidBenefitController hidBenefitController;
    @Mock
    HidBenefitService hidBenefitService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void addHidBenefitTests(){
        Mockito.when(hidBenefitService.addHidBenefit(any())).thenReturn(Mono.just(new HidBenefitDto()));
        Mono<HidBenefitDto> response= hidBenefitController.addHidBenefit(new HidBenefitDto());
    }
    @Test
    public void existByHealthIdAndBenefitTests(){
        Mockito.when(hidBenefitService.existByHealthIdAndBenefit(any(),any())).thenReturn(Mono.just(true));
        Mono<Boolean> response= hidBenefitController.existByHealthIdAndBenefit("test","test");
    }

}
