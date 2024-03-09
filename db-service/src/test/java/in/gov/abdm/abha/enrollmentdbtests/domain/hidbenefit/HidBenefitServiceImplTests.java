package in.gov.abdm.abha.enrollmentdbtests.domain.hidbenefit;

import in.gov.abdm.abha.enrollmentdb.domain.hidbenefit.HidBenefitServiceImpl;
import in.gov.abdm.abha.enrollmentdb.model.hid_phr_address.HidPhrAddress;
import in.gov.abdm.abha.enrollmentdb.model.hid_phr_address.HidPhrAddressDto;
import in.gov.abdm.abha.enrollmentdb.model.hidbenefit.HidBenefit;
import in.gov.abdm.abha.enrollmentdb.model.hidbenefit.HidBenefitDto;
import in.gov.abdm.abha.enrollmentdb.repository.HidBenefitRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class HidBenefitServiceImplTests {
    @InjectMocks
    HidBenefitServiceImpl hidBenefitService;
    @Mock
    HidBenefitRepository hidBenefitRepository;

    @Mock
    private ModelMapper modelMapper;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void teardown() {
    }
    @Test
    public void addHidBenefitTests(){
        Mockito.when(modelMapper.map(any(HidBenefitDto.class),any())).thenReturn(new HidBenefit());
        Mockito.when(modelMapper.map(any(HidBenefit.class),any())).thenReturn(new HidBenefitDto());
        Mockito.when(hidBenefitRepository.save(any())).thenReturn(Mono.just(new HidBenefit()));
        StepVerifier.create(hidBenefitService.addHidBenefit(new HidBenefitDto())).expectNextCount(1L).verifyComplete();
    }
    @Test
    public void existByHealthIdAndBenefitTests(){
        Mockito.when(hidBenefitRepository.existsByHealthIdNumberAndBenefitNameAllIgnoreCase(any(),any())).thenReturn(Mono.just(true));
        StepVerifier.create(hidBenefitService.existByHealthIdAndBenefit("Test","Test")).expectNextCount(1L).verifyComplete();
    }

}
