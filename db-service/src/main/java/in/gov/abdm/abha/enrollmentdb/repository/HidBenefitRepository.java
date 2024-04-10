package in.gov.abdm.abha.enrollmentdb.repository;
import in.gov.abdm.abha.enrollmentdb.model.hidbenefit.HidBenefit;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface HidBenefitRepository extends R2dbcRepository<HidBenefit, String> {
    @Query("SELECT EXISTS (select 1 from hid_benefit where health_id_number = :healthIdNumber and benefit_name = :benefitName)")
    Mono<Boolean> existsByHealthIdNumberAndBenefitNameAllIgnoreCase(String healthIdNumber, String benefitName);
}
