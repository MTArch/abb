package in.gov.abdm.abha.enrollmentdbtests.controller;

import in.gov.abdm.abha.enrollmentdb.controller.DependentAccountRelationshipController;
import in.gov.abdm.abha.enrollmentdb.domain.dependent_account_relationship.DependentAccountRelationshipService;
import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationship;
import in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship.DependentAccountRelationshipDto;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(SpringExtension.class)
public class DependentAccountRelationshipControllerTests {
@InjectMocks
DependentAccountRelationshipController dependentAccountRelationshipController;

@Mock
DependentAccountRelationshipService dependentAccountRelationshipService;
    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void createDependentRelationshipsTests(){
        Mockito.when(dependentAccountRelationshipService.linkDependentAccountRelationships(any())).thenReturn(Mono.just(new DependentAccountRelationshipDto()));
        ResponseEntity<Mono<DependentAccountRelationshipDto>> response= dependentAccountRelationshipController.createDependentRelationships(Arrays.asList(new DependentAccountRelationshipDto()));
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void getAllDependentAccountRelationshipTests(){
        Mockito.when(dependentAccountRelationshipService.getAllDependentAccountRelationship()).thenReturn(Flux.just(new DependentAccountRelationshipDto()));
        ResponseEntity<Flux<DependentAccountRelationshipDto>> response= dependentAccountRelationshipController.getAllDependentAccountRelationship();
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void getDependentAccountRelationshipDetailTests(){
        Mockito.when(dependentAccountRelationshipService.getDependentAccountRelationshipDetailById(any())).thenReturn(Mono.just(new DependentAccountRelationshipDto()));
        ResponseEntity<Mono<DependentAccountRelationshipDto>> response= dependentAccountRelationshipController.getDependentAccountRelationshipDetail(1L);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void updateDependentAccountRelationshipDetailTests(){
        Mockito.when(dependentAccountRelationshipService.updateDependentAccountRelationshipDetailById(any(),any())).thenReturn(Mono.just(new DependentAccountRelationship()));
        ResponseEntity<Mono<DependentAccountRelationship>> response= dependentAccountRelationshipController.updateDependentAccountRelationshipDetail(new DependentAccountRelationshipDto(),1L);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void deleteDependentAccountRelationshipDetailTests(){
        Mockito.when(dependentAccountRelationshipService.deleteDependentAccountRelationshipDetailById(any(),any())).thenReturn(Mono.empty());
        ResponseEntity<Mono<Void>> response= dependentAccountRelationshipController.deleteDependentAccountRelationshipDetail(new DependentAccountRelationshipDto(),1L);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }


}
