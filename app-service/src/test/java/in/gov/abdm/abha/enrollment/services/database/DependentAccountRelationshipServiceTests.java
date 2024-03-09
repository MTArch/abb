package in.gov.abdm.abha.enrollment.services.database;

import in.gov.abdm.abha.enrollment.client.AbhaDBDependentAccountRelationshipFClient;
import in.gov.abdm.abha.enrollment.enums.link.parent.Relationship;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.DependentAccountRelationshipDto;
import in.gov.abdm.abha.enrollment.model.link.parent.request.ChildAbhaRequestDto;
import in.gov.abdm.abha.enrollment.model.link.parent.request.LinkParentRequestDto;
import in.gov.abdm.abha.enrollment.model.link.parent.request.ParentAbhaRequestDto;
import in.gov.abdm.abha.enrollment.services.database.dependent_account_relationship.impl.DependentAccountRelationshipServiceImpl;
import in.gov.abdm.abha.enrollment.validators.RelationshipValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
@ExtendWith(SpringExtension.class)
public class DependentAccountRelationshipServiceTests {
    @InjectMocks
    DependentAccountRelationshipServiceImpl dependentAccountRelationshipService;

    @Mock
    AbhaDBDependentAccountRelationshipFClient abhaDBDependentAccountRelationshipFClient;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);


    }

    @Test
    public void createDependentAccountEntityTests(){
        DependentAccountRelationshipDto dependentAccountRelationshipDto = new DependentAccountRelationshipDto();
        dependentAccountRelationshipDto.setId(1L);
        dependentAccountRelationshipDto.setCreatedBy("createdBy");
        dependentAccountRelationshipDto.setCreatedDate(LocalDateTime.now());
        List <DependentAccountRelationshipDto> list = new ArrayList<>();
        Mockito.when(abhaDBDependentAccountRelationshipFClient.createDependentRelationships(any())).thenReturn(Mono.just(dependentAccountRelationshipDto));
        DependentAccountRelationshipDto result =  dependentAccountRelationshipService.createDependentAccountEntity(list).block();

        Assert.assertEquals("Failed to Validate HealthIdNumber",dependentAccountRelationshipDto.getId(), result.getId());

    }

    @Test
    public void prepareDependentAccountTests(){
        DependentAccountRelationshipDto dependentAccountRelationshipDto = new DependentAccountRelationshipDto();
        LinkParentRequestDto linkParentRequestDto = new LinkParentRequestDto();
        ParentAbhaRequestDto parentAbhaRequestDto = new ParentAbhaRequestDto();
        ChildAbhaRequestDto childAbhaRequestDto = new ChildAbhaRequestDto();

        dependentAccountRelationshipDto.setId(1L);
        dependentAccountRelationshipDto.setCreatedBy("createdBy");
        dependentAccountRelationshipDto.setCreatedDate(LocalDateTime.now());
        List <DependentAccountRelationshipDto> list = new ArrayList<>();
        list.add(dependentAccountRelationshipDto);

        childAbhaRequestDto.setAbhaNumber("12345");
        parentAbhaRequestDto.setAbhaNumber("1234");
        parentAbhaRequestDto.setDocument("document");
        parentAbhaRequestDto.setRelationship(Relationship.FATHER);
        List<ParentAbhaRequestDto> parentList = new ArrayList<>();
        parentList.add(parentAbhaRequestDto);
        linkParentRequestDto.setTxnId("txnid");
        linkParentRequestDto.setChildAbhaRequestDto(childAbhaRequestDto);
        linkParentRequestDto.setParentAbhaRequestDtoList(parentList);

  //    Mockito.when(dependentAccountRelationshipService.prepareDependentAccount(any())).thenReturn(list);
        List<DependentAccountRelationshipDto> result =  dependentAccountRelationshipService.prepareDependentAccount(linkParentRequestDto);

        Assert.assertEquals("Failed to Validate HealthIdNumber","", "");

    }
}
