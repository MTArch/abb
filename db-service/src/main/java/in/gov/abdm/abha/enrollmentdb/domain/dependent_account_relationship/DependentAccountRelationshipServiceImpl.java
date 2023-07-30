package in.gov.abdm.abha.enrollmentdb.domain.dependent_account_relationship;

import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationship;
import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationshipDto;
import in.gov.abdm.abha.enrollmentdb.repository.DependentAccountRelationshipRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.ENROLLMENT_LOG_PREFIX;


/**
 * A class which implements Business logic.
 */
@Service
@Slf4j
public class DependentAccountRelationshipServiceImpl implements DependentAccountRelationshipService {

    @Autowired
    DependentAccountRelationshipRepository dependentAccountRelationshipRepository;

    /**
     * Here we are creating a ModelMapper object and putting into IOC
     * for implementing singleton, with the reference all its methods can be utilized.
     */
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private DependentAccountRelationshipSubscriber dependentAccountRelationshipSubscriber;

    @Override
    public Mono<DependentAccountRelationshipDto> linkDependentAccountRelationships(List<DependentAccountRelationshipDto> dependentAccountDtos) {
        dependentAccountRelationshipRepository.saveAll(dependentAccountDtos.parallelStream()
                .map(result -> modelMapper.map(result, DependentAccountRelationship.class).setAsNew())
                .collect(Collectors.toList())).subscribe(dependentAccountRelationshipSubscriber);
        return Mono.empty();
    }

    @Override
    public Flux<DependentAccountRelationshipDto> getAllDependentAccountRelationship() {
        return dependentAccountRelationshipRepository.findAll().map(dependentAccountRelationship -> modelMapper.map(dependentAccountRelationship, DependentAccountRelationshipDto.class));
    }

    @Override
    public Mono<DependentAccountRelationshipDto> getDependentAccountRelationshipDetailById(Long id) {
        log.info(ENROLLMENT_LOG_PREFIX + "Executing getDependentAccountRelationshipDetailById method.");
        return dependentAccountRelationshipRepository.findById(id)
                .map(dependentAccount -> modelMapper.map(dependentAccount, DependentAccountRelationshipDto.class));

    }

    @Override
    public Mono<DependentAccountRelationship> updateDependentAccountRelationshipDetailById(DependentAccountRelationshipDto dependentAccountRelationshipDto, Long id) {
        log.info(ENROLLMENT_LOG_PREFIX + "Executing updateDependentAccountRelationshipDetailById method.");
        DependentAccountRelationship dependentAccount = modelMapper.map(dependentAccountRelationshipDto, DependentAccountRelationship.class);
        return dependentAccountRelationshipRepository.save(dependentAccount);
    }

    @Override
    public Mono<Void> deleteDependentAccountRelationshipDetailById(DependentAccountRelationshipDto dependentAccountRelationshipDto, Long id) {
        log.info(ENROLLMENT_LOG_PREFIX + "Executing deleteDependentAccountRelationshipDetailById method.");
        modelMapper.map(dependentAccountRelationshipDto, DependentAccountRelationship.class);
        return dependentAccountRelationshipRepository.deleteById(id);

    }
}
