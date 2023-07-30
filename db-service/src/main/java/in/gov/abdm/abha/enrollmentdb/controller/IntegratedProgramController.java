package in.gov.abdm.abha.enrollmentdb.controller;

import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.domain.integrated_program.IntegratedProgramServiceImpl;
import in.gov.abdm.abha.enrollmentdb.model.integrated_program.IntegratedProgram;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.ENROLLMENT_DB_INTEGRATED_PROGRAM;
import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.ENROLLMENT_DB_LOG_MSG;

@RequestMapping(ABHAEnrollmentDBConstant.INTEGRATED_PROGRAM_ENDPOINT)
@Slf4j
@RestController
public class IntegratedProgramController {

    @Autowired
    IntegratedProgramServiceImpl integratedProgramService;

    @GetMapping
    public ResponseEntity<Flux<IntegratedProgram>> getIntegratedPrograms() {
        log.info(ENROLLMENT_DB_LOG_MSG+"get data"+ENROLLMENT_DB_INTEGRATED_PROGRAM);
        return ResponseEntity.ok(integratedProgramService.getIntegratedPrograms());
    }

    @GetMapping(ABHAEnrollmentDBConstant.GET_INTEGRATED_PROGRAM_BY_BENEFIT_NAME)
    public ResponseEntity<Flux<IntegratedProgram>> getIntegratedProgramByBenefitName(@PathVariable("benefitName") String benefitName) {
        log.info(ENROLLMENT_DB_LOG_MSG+"get data based on benefitName="+benefitName+ENROLLMENT_DB_INTEGRATED_PROGRAM);
        return ResponseEntity.ok(integratedProgramService.getIntegratedProgramByBenefitName(benefitName));
    }
}
