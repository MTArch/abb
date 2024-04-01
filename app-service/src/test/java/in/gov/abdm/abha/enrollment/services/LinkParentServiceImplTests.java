package in.gov.abdm.abha.enrollment.services;

import in.gov.abdm.abha.enrollment.commontestdata.CommonTestData;
import in.gov.abdm.abha.enrollment.enums.childabha.AbhaType;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.ConsentDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.DependentAccountRelationshipDto;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.link.parent.request.ChildAbhaRequestDto;
import in.gov.abdm.abha.enrollment.model.link.parent.request.LinkParentRequestDto;
import in.gov.abdm.abha.enrollment.model.link.parent.request.ParentAbhaRequestDto;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.dependent_account_relationship.DependentAccountRelationshipService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.link.parent.impl.LinkParentServiceImpl;
import liquibase.pro.packaged.M;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static in.gov.abdm.abha.enrollment.commontestdata.CommonTestData.TRANSACTION_ID_VALID;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class LinkParentServiceImplTests {
    @InjectMocks
    LinkParentServiceImpl linkParentService;
    @Mock
    TransactionService transactionService;
    @Mock
    AccountService accountService;
    @Mock
    DependentAccountRelationshipService dependentAccountRelationshipService;
    @Mock
    HidPhrAddressService hidPhrAddressService;
    private AccountDto accountDto;
    private LinkParentRequestDto linkParentRequestDto;
    private TransactionDto transactionDto;




    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        accountDto=new AccountDto();
        transactionDto=new TransactionDto();
        linkParentRequestDto=new LinkParentRequestDto();
        linkParentRequestDto.setTxnId(TRANSACTION_ID_VALID);

        accountDto.setNewAccount(true);
        accountDto.setOrigin("");
        accountDto.setFacilityId("");
        accountDto.setVerificationType("");
        accountDto.setHealthId("");
        accountDto.setProfilePhoto("");
        accountDto.setKycPhoto("");
        accountDto.setAddress("");
        accountDto.setSubDistrictName("");
        accountDto.setCreatedDate(LocalDateTime.now());
        accountDto.setHealthIdNumber("1");
        accountDto.setApiEndPoint("");
        accountDto.setKycdob("");
        accountDto.setApiVersion("");
        accountDto.setCmMigrated("");
        accountDto.setYearOfBirth("");
        accountDto.setXmluid("");
        accountDto.setWardName("");
        accountDto.setWardCode("");
        accountDto.setVillageName("");
        accountDto.setVillageCode("");
        accountDto.setVerificationStatus("");
        accountDto.setUpdateDate(LocalDateTime.now());
        accountDto.setType(AbhaType.CHILD);
        accountDto.setTownName("");
        accountDto.setTownCode("");
        accountDto.setKycdob("");
        accountDto.setKycVerified(true);
        accountDto.setOkycVerified(true);
        accountDto.setPassword("");
        accountDto.setSubDistrictCode("");

        transactionDto.setTxnId(UUID.randomUUID());
        transactionDto.setTxnResponse("success");
        DependentAccountRelationshipDto dependentAccountRelationshipDto=DependentAccountRelationshipDto.builder().build();
        dependentAccountRelationshipDto=new DependentAccountRelationshipDto(1L,"","","","","","",LocalDateTime.now(),LocalDateTime.now());
        DependentAccountRelationshipDto d= new DependentAccountRelationshipDto(dependentAccountRelationshipDto.getId(), dependentAccountRelationshipDto.getParentHealthIdNumber(), dependentAccountRelationshipDto.getDependentHealthIdNumber(),dependentAccountRelationshipDto.getRelatedAs(), dependentAccountRelationshipDto.getRelationshipProofDocumentLocation(), dependentAccountRelationshipDto.getCreatedBy(), dependentAccountRelationshipDto.getUpdatedBy(), dependentAccountRelationshipDto.getCreatedDate(), dependentAccountRelationshipDto.getUpdatedDate());
    }
    @AfterEach
    void tearDown(){

    }
    @Test
    void linkDependentAccountTest(){
        Mockito.when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(new TransactionDto()));
        Mockito.when(dependentAccountRelationshipService.createDependentAccountEntity(any())).thenReturn(Mono.just(new DependentAccountRelationshipDto()));
        Mockito.when(accountService.getAccountByHealthIdNumber(any())).thenReturn(Mono.just(accountDto));
        Mockito.when(accountService.updateAccountByHealthIdNumber(any(),any())).thenReturn(Mono.just(accountDto));
        Mockito.when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(new HidPhrAddressDto(1L,"","","",1,"",LocalDateTime.now(),"","",LocalDateTime.now(),1,1,true)));
        StepVerifier.create(linkParentService.linkDependentAccount(new LinkParentRequestDto(TRANSACTION_ID_VALID, Arrays.asList(Scopes.ABHA_ENROL),Arrays.asList(new ParentAbhaRequestDto()),new ChildAbhaRequestDto("a"),new ConsentDto()))).expectNextCount(1L).verifyComplete();
    }
    @Test
    void linkDependentAccountTest2(){
        Mockito.when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        Mockito.when(dependentAccountRelationshipService.createDependentAccountEntity(any())).thenReturn(Mono.just(new DependentAccountRelationshipDto()));
        Mockito.when(accountService.getAccountByHealthIdNumber(any())).thenReturn(Mono.just(accountDto));
        Mockito.when(accountService.updateAccountByHealthIdNumber(any(),any())).thenReturn(Mono.just(accountDto));
        Mockito.when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(new HidPhrAddressDto(1L,"","","",1,"",LocalDateTime.now(),"","",LocalDateTime.now(),1,1,true)));
        StepVerifier.create(linkParentService.linkDependentAccount(new LinkParentRequestDto(TRANSACTION_ID_VALID, Arrays.asList(Scopes.ABHA_ENROL),Arrays.asList(new ParentAbhaRequestDto()),new ChildAbhaRequestDto("a"),new ConsentDto())))
                .expectError().verify();
    }

}
