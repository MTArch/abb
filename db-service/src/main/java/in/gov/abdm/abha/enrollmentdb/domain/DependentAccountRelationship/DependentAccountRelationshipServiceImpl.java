package in.gov.abdm.abha.enrollmentdb.domain.DependentAccountRelationship;

import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationship;
import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationshipDto;
import in.gov.abdm.abha.enrollmentdb.repository.DependentAccountRelationshipRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DependentAccountRelationshipServiceImpl implements DependentAccountRelationshipService{

    @Autowired
    DependentAccountRelationshipRepository dependentAccountRelationshipRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private DependentAccountRelationshipSubscriber dependentAccountRelationshipSubscriber;

    @Override
    public Mono<DependentAccountRelationship> addAccount(DependentAccountRelationshipDto dependentAccountRelationshipDto) {

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
}
