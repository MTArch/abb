package in.gov.abdm.abha.enrollmentdb.controller;

import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.domain.DependentAccountRelationship.DependentAccountRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(ABHAEnrollmentDBConstant.DEPENDENTACCOUNTRELATIONSHIP_ENDPOINT)
@RestController
public class DependentAccountRelationshipController {

    @Autowired
    DependentAccountRelationshipService dependentAccountRelationshipService;
}
