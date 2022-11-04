package in.gov.abdm.abha.enrollmentdb.controller;
import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.domain.DependentAccountRelationship.DependentAccountRelationshipService;
import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationshipDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(ABHAEnrollmentDBConstant.DEPENDENT_ACCOUNT_RELATIONSHIP_ENDPOINT)
@RestController
public class DependentAccountRelationshipController {

    @Autowired
    DependentAccountRelationshipService dependentAccountRelationshipService;

    @GetMapping(value = ABHAEnrollmentDBConstant.GET_DEPENDENT_ACCOUNT_BY_ID)
    public ResponseEntity<?> getDependentAccountById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(dependentAccountRelationshipService.getDependentAccountById(id));
    }

    @PatchMapping(value = ABHAEnrollmentDBConstant.UPDATE_RELATIONSHIP_ACCOUNT_BY_ID)
    public ResponseEntity<?> updateDependentAccount(@RequestBody DependentAccountRelationshipDto dependentAccountRelationshipDto,
                                           @PathVariable("id") Long id) {
        return ResponseEntity.ok(dependentAccountRelationshipService.updateDependentAccountById(dependentAccountRelationshipDto, id));
    }

    @DeleteMapping(value = ABHAEnrollmentDBConstant.DELETE_RELATIONSHIP_ACCOUNT_BY_ID)
    public ResponseEntity<?> deleteDependentAccount(@RequestBody DependentAccountRelationshipDto dependentAccountRelationshipDto,
                                                    @PathVariable("id") Long id) {
        return ResponseEntity.ok(dependentAccountRelationshipService.deleteDependentAccountById(dependentAccountRelationshipDto, id));
    }
}
