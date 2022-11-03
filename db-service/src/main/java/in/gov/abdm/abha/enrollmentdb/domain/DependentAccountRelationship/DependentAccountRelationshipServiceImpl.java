package in.gov.abdm.abha.enrollmentdb.domain.DependentAccountRelationship;

import in.gov.abdm.abha.enrollmentdb.repository.DependentAccountRelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DependentAccountRelationshipServiceImpl implements DependentAccountRelationshipService{

    @Autowired
    DependentAccountRelationshipRepository dependentAccountRelationshipRepository;
}
