package in.gov.abdm.abha.enrollmentdb.controller;
import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.domain.DependentAccountRelationship.DependentAccountRelationshipService;
import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationshipDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.*;

@RequestMapping(ABHAEnrollmentDBConstant.DEPENDENT_ACCOUNT_RELATIONSHIP_ENDPOINT)
@Slf4j
@RestController
public class DependentAccountRelationshipController {

    @Autowired
    DependentAccountRelationshipService dependentAccountRelationshipService;

    @PostMapping
    public ResponseEntity<?>createDependentRelationships(@RequestBody List<DependentAccountRelationshipDto> dependentAccountRelationshipDtoList){
        log.info(ENROLLMENT_DB_LOG_MSG+"save data= "+ENROLLMENT_DB_DEPENDENT_ACCOUNT_RELATIONSHIP);
        return ResponseEntity.ok(dependentAccountRelationshipService.linkDependentAccountRelationships(dependentAccountRelationshipDtoList));
    }

    @GetMapping
    public ResponseEntity<?>getAllDependentAccountRelationship(){
        log.info(ENROLLMENT_DB_LOG_MSG+"get data "+ENROLLMENT_DB_DEPENDENT_ACCOUNT_RELATIONSHIP);
        return ResponseEntity.ok(dependentAccountRelationshipService.getAllDependentAccountRelationship());
    }
    
    @GetMapping(value = ABHAEnrollmentDBConstant.GET_DEPENDENT_ACCOUNT_RELATIONSHIP_BY_ID)
    public ResponseEntity<?> getDependentAccountRelationshipDetail(@PathVariable("id") Long id) {
        log.info(ENROLLMENT_DB_LOG_MSG+"get data based on id="+id+ENROLLMENT_DB_DEPENDENT_ACCOUNT_RELATIONSHIP);
        return ResponseEntity.ok(dependentAccountRelationshipService.getDependentAccountRelationshipDetailById(id));
    }

    @PatchMapping(value = ABHAEnrollmentDBConstant.UPDATE_DEPENDENT_ACCOUNT_RELATIONSHIP_BY_ID)
    public ResponseEntity<?> updateDependentAccountRelationshipDetail(@RequestBody DependentAccountRelationshipDto dependentAccountRelationshipDto,
                                           @PathVariable("id") Long id) {
        log.info(ENROLLMENT_DB_LOG_MSG+"update data based on id="+id+ENROLLMENT_DB_DEPENDENT_ACCOUNT_RELATIONSHIP);
        return ResponseEntity.ok(dependentAccountRelationshipService.updateDependentAccountRelationshipDetailById(dependentAccountRelationshipDto, id));
    }

    @DeleteMapping(value = ABHAEnrollmentDBConstant.DELETE_DEPENDENT_ACCOUNT_RELATIONSHIP_BY_ID)
    public ResponseEntity<?> deleteDependentAccountRelationshipDetail(@RequestBody DependentAccountRelationshipDto dependentAccountRelationshipDto,
                                                    @PathVariable("id") Long id) {
        log.info(ENROLLMENT_DB_LOG_MSG+"delete data based on id="+id+ENROLLMENT_DB_DEPENDENT_ACCOUNT_RELATIONSHIP);
        return ResponseEntity.ok(dependentAccountRelationshipService.deleteDependentAccountRelationshipDetailById(dependentAccountRelationshipDto, id));
    }
}
