package in.gov.abdm.abha.enrollmentdb.domain.integrated_program;

import in.gov.abdm.abha.enrollmentdb.model.integrated_program.IntegratedProgram;
import in.gov.abdm.abha.enrollmentdb.repository.IntegratedProgramRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class IntegratedProgramServiceImpl {

    @Autowired
    IntegratedProgramRepository integratedProgramRepository;

    public Flux<IntegratedProgram> getIntegratedPrograms() {
        return integratedProgramRepository.findAll();
    }

    public Flux getIntegratedProgramByBenefitName(String benefitName) {
        return integratedProgramRepository.findByBenefitNameIgnoreCase(benefitName)
                .doOnError(throwable -> log.error(throwable.getMessage()));
    }
}
