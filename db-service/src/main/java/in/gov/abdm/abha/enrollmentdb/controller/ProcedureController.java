package in.gov.abdm.abha.enrollmentdb.controller;

import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.domain.procedure.ProcedureService;
import in.gov.abdm.abha.enrollmentdb.model.procedure.SaveAllDataRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.*;

@RequestMapping(ABHAEnrollmentDBConstant.ACCOUNT_ENDPOINT)
@Slf4j
@RestController
public class ProcedureController {

    @Autowired
    ProcedureService procedureService;
    @PostMapping(value = "/saveAll")
    public ResponseEntity<?> createAccount(@RequestBody SaveAllDataRequest saveAllDataRequest) {
        log.info(ENROLLMENT_DB_LOG_MSG+"save data"+ENROLLMENT_DB_PROCEDURE_CALL);
        return ResponseEntity.ok(procedureService.saveAllData(saveAllDataRequest));
    }
}
