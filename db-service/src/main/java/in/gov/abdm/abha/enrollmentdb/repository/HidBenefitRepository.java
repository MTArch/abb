package in.gov.abdm.abha.enrollmentdb.repository;
import in.gov.abdm.abha.enrollmentdb.model.hidbenefit.HidBenefit;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface HidBenefitRepository extends R2dbcRepository<HidBenefit, String> {
}
