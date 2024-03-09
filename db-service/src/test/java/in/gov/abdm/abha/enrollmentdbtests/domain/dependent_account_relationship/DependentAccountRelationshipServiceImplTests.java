package in.gov.abdm.abha.enrollmentdbtests.domain.dependent_account_relationship;

import in.gov.abdm.abha.enrollmentdb.domain.dependent_account_relationship.DependentAccountRelationshipServiceImpl;
import in.gov.abdm.abha.enrollmentdb.domain.dependent_account_relationship.DependentAccountRelationshipSubscriber;
import in.gov.abdm.abha.enrollmentdb.model.account.AccountDto;
import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationship;
import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationshipDto;
import in.gov.abdm.abha.enrollmentdb.repository.DependentAccountRelationshipRepository;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(SpringExtension.class)
public class DependentAccountRelationshipServiceImplTests {
    @InjectMocks
    DependentAccountRelationshipServiceImpl dependentAccountRelationshipService;
    @Mock
    DependentAccountRelationshipRepository dependentAccountRelationshipRepository;
    @Mock
    private ModelMapper modelMapper;

    @Mock
    private DependentAccountRelationshipSubscriber dependentAccountRelationshipSubscriber;
    private DependentAccountRelationshipDto dependentAccountRelationshipDto;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        dependentAccountRelationshipDto=new DependentAccountRelationshipDto();
    }
    @AfterEach
    void teardown() {
        dependentAccountRelationshipDto=null;
    }
    @Test
    public void linkDependentAccountRelationshipsTests(){
        Mockito.when(modelMapper.map(any(Accounts.class),any())).thenReturn(new AccountDto());
        Mockito.when(modelMapper.map(any(DependentAccountRelationshipDto.class),any())).thenReturn(new DependentAccountRelationship());

        List<DependentAccountRelationshipDto> dependentAccountDtos=new ArrayList<>();
        dependentAccountDtos.add(dependentAccountRelationshipDto);
        Stream<DependentAccountRelationshipDto> s =dependentAccountDtos.stream();
        //        Mockito.when(dependentAccountDtos.parallelStream()).thenReturn(s);
        Mockito.when(dependentAccountRelationshipRepository.saveAll(any(Iterable.class))).thenReturn(Flux.just(new DependentAccountRelationship()));
        StepVerifier.create(dependentAccountRelationshipService.linkDependentAccountRelationships(dependentAccountDtos)).expectNextCount(0L).verifyComplete();
    }
    @Test
    public void getAllDependentAccountRelationshipTests(){
        Mockito.when(modelMapper.map(any(DependentAccountRelationship.class),any())).thenReturn(new DependentAccountRelationshipDto());
        Mockito.when(dependentAccountRelationshipRepository.findAll()).thenReturn(Flux.just(new DependentAccountRelationship()));
        StepVerifier.create(dependentAccountRelationshipService.getAllDependentAccountRelationship()).expectNext(new DependentAccountRelationshipDto()).verifyComplete();
    }
    @Test
    public void getDependentAccountRelationshipDetailByIdTests(){
        Mockito.when(modelMapper.map(any(DependentAccountRelationship.class),any())).thenReturn(new DependentAccountRelationshipDto());
        Mockito.when(dependentAccountRelationshipRepository.findById(anyLong())).thenReturn(Mono.just(new DependentAccountRelationship()));
        StepVerifier.create(dependentAccountRelationshipService.getDependentAccountRelationshipDetailById(1L)).expectNext(new DependentAccountRelationshipDto()).verifyComplete();
    }
    @Test
    public void updateDependentAccountRelationshipDetailByIdTests(){
        Mockito.when(modelMapper.map(any(DependentAccountRelationshipDto.class),any())).thenReturn(new DependentAccountRelationship());
        Mockito.when(dependentAccountRelationshipRepository.save(any())).thenReturn(Mono.just(new DependentAccountRelationship()));
        StepVerifier.create(dependentAccountRelationshipService.updateDependentAccountRelationshipDetailById(new DependentAccountRelationshipDto(),1L)).expectNext(new DependentAccountRelationship()).verifyComplete();
    }
    @Test
    public void deleteDependentAccountRelationshipDetailByIdTests(){
        Mockito.when(modelMapper.map(any(DependentAccountRelationshipDto.class),any())).thenReturn(new DependentAccountRelationship());
        Mockito.when(dependentAccountRelationshipRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        StepVerifier.create(dependentAccountRelationshipService.deleteDependentAccountRelationshipDetailById(new DependentAccountRelationshipDto(),1L)).expectNextCount(0L).verifyComplete();
    }



    }


