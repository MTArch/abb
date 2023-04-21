package in.gov.abdm.abha.enrollmentdb.controller;

import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.domain.integrated_program.IntegratedProgramServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(ABHAEnrollmentDBConstant.INTEGRATED_PROGRAM_ENDPOINT)
@RestController
public class IntegratedProgramController {

    @Autowired
    IntegratedProgramServiceImpl integratedProgramService;

    @GetMapping
    public ResponseEntity<?> getIntegratedPrograms() {
        return ResponseEntity.ok(integratedProgramService.getIntegratedPrograms());
    }

    @GetMapping(ABHAEnrollmentDBConstant.GET_INTEGRATED_PROGRAM_BY_BENEFIT_NAME)
    public ResponseEntity<?> getIntegratedProgramByBenefitName(@PathVariable("benefitName") String benefitName) {
        return ResponseEntity.ok(integratedProgramService.getIntegratedProgramByBenefitName(benefitName));
    }
}
