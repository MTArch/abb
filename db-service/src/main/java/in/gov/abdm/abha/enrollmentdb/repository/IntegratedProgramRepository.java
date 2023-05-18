package in.gov.abdm.abha.enrollmentdb.repository;

import in.gov.abdm.abha.enrollmentdb.model.integrated_program.IntegratedProgram;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;


@Repository
public interface IntegratedProgramRepository extends R2dbcRepository<IntegratedProgram, String> {
    Flux<IntegratedProgram> findAll();

    Flux<IntegratedProgram> findByBenefitNameIgnoreCase(String benefitName);
}