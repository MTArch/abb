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

import java.time.LocalDateTime;

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
        HidBenefitDto h =new HidBenefitDto("","","","","", LocalDateTime.now(),LocalDateTime.now(),"","","",1,LocalDateTime.now(),LocalDateTime.now(),"","");
        HidBenefitDto hidBenefitDto=new HidBenefitDto();
        hidBenefitDto.setHidBenefitId(h.getHidBenefitId());
        hidBenefitDto.setProgramName(h.getProgramName());
        hidBenefitDto.setBenefitName(h.getBenefitName());
        hidBenefitDto.setBenefitId(h.getBenefitId());
        hidBenefitDto.setStateCode(h.getStateCode());
        hidBenefitDto.setValidTill(h.getValidTill());
        hidBenefitDto.setLinkedDate(h.getLinkedDate());
        hidBenefitDto.setLinkedBy(h.getLinkedBy());
        hidBenefitDto.setHealthIdNumber(h.getHealthIdNumber());
        hidBenefitDto.setMobileNumber(h.getMobileNumber());
        hidBenefitDto.setStatus(h.getStatus());
        hidBenefitDto.setCreatedDate(h.getCreatedDate());
        hidBenefitDto.setUpdatedDate(h.getUpdatedDate());
        hidBenefitDto.setCreatedBy(h.getCreatedBy());
        hidBenefitDto.setUpdatedBy(h.getUpdatedBy());
        HidBenefit h2 =new HidBenefit("","","","","", LocalDateTime.now(),LocalDateTime.now(),"","","",1,LocalDateTime.now(),LocalDateTime.now(),"","",true);
        HidBenefit hidBenefit=new HidBenefit();
        hidBenefit.setHidBenefitId(h2.getHidBenefitId());
        hidBenefit.setProgramName(h2.getProgramName());
        hidBenefit.setBenefitName(h2.getBenefitName());
        hidBenefit.setBenefitId(h2.getBenefitId());
        hidBenefit.setStateCode(h2.getStateCode());
        hidBenefit.setValidTill(h2.getValidTill());
        hidBenefit.setLinkedDate(h2.getLinkedDate());
        hidBenefit.setLinkedBy(h2.getLinkedBy());
        hidBenefit.setHealthIdNumber(h2.getHealthIdNumber());
        hidBenefit.setMobileNumber(h2.getMobileNumber());
        hidBenefit.setStatus(h2.getStatus());
        hidBenefit.setCreatedDate(h2.getCreatedDate());
        hidBenefit.setUpdatedDate(h2.getUpdatedDate());
        hidBenefit.setCreatedBy(h2.getCreatedBy());
        hidBenefit.setUpdatedBy(h2.getUpdatedBy());
        hidBenefit.setNewHidBenefit(h2.isNewHidBenefit());
        String s =h2.getId();
        boolean newHid= h2.isNew();
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
