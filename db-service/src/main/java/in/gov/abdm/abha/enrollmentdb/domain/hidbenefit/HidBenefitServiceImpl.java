package in.gov.abdm.abha.enrollmentdb.domain.hidbenefit;
import in.gov.abdm.abha.enrollmentdb.model.hidbenefit.HidBenefit;
import in.gov.abdm.abha.enrollmentdb.model.hidbenefit.HidBenefitDto;
import in.gov.abdm.abha.enrollmentdb.repository.HidBenefitRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.UUID;


@Slf4j
@Service
public class HidBenefitServiceImpl implements HidBenefitService{
    @Autowired
    HidBenefitRepository hidBenefitRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Mono<HidBenefitDto> addHidBenefit(HidBenefitDto hidBenefitDto) {
        HidBenefit hidBenefit = modelMapper.map(hidBenefitDto, HidBenefit.class).setAsNew();
        hidBenefit.setCreatedDate(LocalDateTime.now());
        hidBenefit.setAsNew();
        hidBenefit.setHidBenefitId(UUID.randomUUID().toString().replace("\\-",""));
        return hidBenefitRepository.save(hidBenefit)
                .map(hidBenefit1 -> modelMapper.map(hidBenefit1, HidBenefitDto.class));
    }

    @Override
    public Mono<Boolean> existByHealthIdAndBenefit(String healthIdNumber, String benefitName) {
        log.info("Processing service method existByBenefitId ");
        return hidBenefitRepository.existsByHealthIdNumberAndBenefitNameAllIgnoreCase(healthIdNumber,benefitName);
    }

}
