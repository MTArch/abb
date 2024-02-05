package in.gov.abdm.abha.enrollmentdb.domain.hidbenefit;
import in.gov.abdm.abha.enrollmentdb.model.hidbenefit.HidBenefit;
import in.gov.abdm.abha.enrollmentdb.model.hidbenefit.HidBenefitDto;
import reactor.core.publisher.Mono;

public interface HidBenefitService {

    Mono<HidBenefitDto> addHidBenefit(HidBenefitDto hidBenefit);

    Mono<Boolean> existByHealthIdAndBenefit(String healthIdNumber, String benefitName);

}
