package in.gov.abdm.abha.enrollmentdb.controller;

import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.domain.procedure.ProcedureService;
import in.gov.abdm.abha.enrollmentdb.model.procedure.SaveAllDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(ABHAEnrollmentDBConstant.ACCOUNT_ENDPOINT)
@RestController
public class ProcedureController {

    @Autowired
    ProcedureService procedureService;
    @PostMapping(value = "/saveAll")
    public ResponseEntity<?> createAccount(@RequestBody SaveAllDataRequest saveAllDataRequest) {
        return ResponseEntity.ok(procedureService.saveAllData(saveAllDataRequest));
    }
}
