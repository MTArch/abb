package in.gov.abdm.abha.enrollmentdb.controller;

import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.domain.DependentAccountRelationship.DependentAccountRelationshipService;
import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationshipDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(ABHAEnrollmentDBConstant.DEPENDENTACCOUNTRELATIONSHIP_ENDPOINT)
@RestController
public class DependentAccountRelationshipController {

    @Autowired
    DependentAccountRelationshipService dependentAccountRelationshipService;

    @PostMapping
    public ResponseEntity<?>createAccount(@RequestBody DependentAccountRelationshipDto dependentAccountRelationshipDto){
        return ResponseEntity.ok(dependentAccountRelationshipService.addAccount(dependentAccountRelationshipDto));
    }

    @GetMapping
    public ResponseEntity<?>getAllDependentAccountRelationship(){
        return ResponseEntity.ok(dependentAccountRelationshipService.getAllDependentAccountRelationship());
    }
}
