package in.gov.abdm.abha.enrollmentdb.domain.DependentAccountRelationship;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.ENROLLMENT_LOG_PREFIX;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationship;
import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationshipDto;
import in.gov.abdm.abha.enrollmentdb.repository.DependentAccountRelationshipRepository;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * A class which implements Business logic.
 */
@Service
@Slf4j
public class DependentAccountRelationshipServiceImpl implements DependentAccountRelationshipService{

    /**
     * Here we are creating a ModelMapper object and putting into IOC
     * for implementing singleton, with the reference all its methods can be utilized.
     */
    @Autowired
    DependentAccountRelationshipRepository dependentAccountRelationshipRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private DependentAccountRelationshipSubscriber dependentAccountRelationshipSubscriber;

    @Override
    public Mono<DependentAccountRelationship> addDependentAccountRelationship(DependentAccountRelationshipDto dependentAccountRelationshipDto) {

        DependentAccountRelationship dependentAccountRelationship= modelMapper.map(dependentAccountRelationshipDto,DependentAccountRelationship.class);
        dependentAccountRelationship.setAsNew();
        Mono<Long> i = dependentAccountRelationshipRepository.getMaxId();
        return i.flatMap(k -> handle(dependentAccountRelationship, k));


    }
    private Mono<DependentAccountRelationship> handle(DependentAccountRelationship dependentAccountRelationship, Long id) {
        dependentAccountRelationship.setId(id + 1);
        return dependentAccountRelationshipRepository.save(dependentAccountRelationship);
    }

    @Override
    public Flux<DependentAccountRelationshipDto> getAllDependentAccountRelationship() {
        return dependentAccountRelationshipRepository.findAll().map(DependentAccountRelationship -> modelMapper.map(DependentAccountRelationship,DependentAccountRelationshipDto.class ));
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
    public Mono deleteDependentAccountRelationshipDetailById(DependentAccountRelationshipDto dependentAccountRelationshipDto, Long id) {
        log.info(ENROLLMENT_LOG_PREFIX + "Executing deleteDependentAccountRelationshipDetailById method.");
        DependentAccountRelationship dependentAccount =
                modelMapper.map(dependentAccountRelationshipDto, DependentAccountRelationship.class);
        return dependentAccountRelationshipRepository.deleteById(id);

    }
}
