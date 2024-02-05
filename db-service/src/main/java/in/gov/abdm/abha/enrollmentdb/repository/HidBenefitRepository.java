package in.gov.abdm.abha.enrollmentdb.repository;
import in.gov.abdm.abha.enrollmentdb.model.hidbenefit.HidBenefit;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface HidBenefitRepository extends R2dbcRepository<HidBenefit, String> {

    Mono<HidBenefit> findFirst1ByHealthIdNumberAndBenefitNameAllIgnoreCase(String healthIdNumber, String benefitName);
    Mono<Boolean> existsByHealthIdNumberAndBenefitNameAllIgnoreCase(String healthIdNumber, String benefitName);
}
