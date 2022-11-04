package in.gov.abdm.abha.enrollmentdb.domain.DependentAccountRelationship;
import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationship;
import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationshipDto;
import in.gov.abdm.abha.enrollmentdb.repository.DependentAccountRelationshipRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.ENROLLMENT_LOG_PREFIX;


/**
 * A class which implements Business logic.
 */
@Service
@Slf4j
public class DependentAccountRelationshipServiceImpl implements DependentAccountRelationshipService{

    /**
     * Here we are creating a TemplateRepository object and putting into IOC
     * for implementing singleton, with the reference all its methods can be utilized.
     */
    @Autowired
    DependentAccountRelationshipRepository repo;

    /**
     * Here we are creating a ModelMapper object and putting into IOC
     * for implementing singleton, with the reference all its methods can be utilized.
     */
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Mono<DependentAccountRelationshipDto> getDependentAccountById(Long id) {
        log.info(ENROLLMENT_LOG_PREFIX + "Executing getDependentAccountById method.");
        return repo.findById(id)
                .map(dependentAccount -> modelMapper.map(dependentAccount, DependentAccountRelationshipDto.class));

    }

    @Override
    public Mono<DependentAccountRelationship> updateDependentAccountById(DependentAccountRelationshipDto dependentAccountRelationshipDto, Long id) {
        log.info(ENROLLMENT_LOG_PREFIX + "Executing updateDependentAccountById method.");
        DependentAccountRelationship dependentAccount = modelMapper.map(dependentAccountRelationshipDto, DependentAccountRelationship.class);
        return repo.save(dependentAccount);
    }

    @Override
    public Mono deleteDependentAccountById(DependentAccountRelationshipDto dependentAccountRelationshipDto, Long id) {
        log.info(ENROLLMENT_LOG_PREFIX + "Executing deleteDependentAccountById method.");
        DependentAccountRelationship dependentAccount =
                modelMapper.map(dependentAccountRelationshipDto, DependentAccountRelationship.class);
        return repo.deleteById(id);

    }
}
