package in.gov.abdm.abha.enrollmentdbtests.domain.account;

import in.gov.abdm.abha.enrollmentdb.domain.account.AccountServiceImpl;
import in.gov.abdm.abha.enrollmentdb.domain.kafka.KafkaService;
import in.gov.abdm.abha.enrollmentdb.model.account.AccountDto;
import in.gov.abdm.abha.enrollmentdb.model.account.AccountReattemptDto;
import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import in.gov.abdm.abha.enrollmentdb.model.de_duplication.DeDuplicationRequest;
import in.gov.abdm.abha.enrollmentdb.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(SpringExtension.class)
public class AccountServiceImplTests {
    @InjectMocks
    AccountServiceImpl accountService;
    @Mock
    AccountRepository accountRepository;
    @Mock
    KafkaService kafkaService;
    @Mock
    ModelMapper modelMapper;
    private Accounts accounts;
    private AccountDto accountDto;
    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        accounts=new Accounts();
        accounts.setHealthIdNumber("1234321");
        accounts.setKycPhoto("photo");
        accounts.setCompPhoto(new byte[12]);
        accounts.setProfilePhoto("profile.png");
        accountDto=new AccountDto();
        accountDto.setKycPhoto("");
        accountDto.setProfilePhoto("");
    }
    @AfterEach
    void teardown(){
        accountDto=null;
        accounts=null;
    }

    @Test
    public void addAccountTests(){
        Mockito.when(modelMapper.map(any(Accounts.class),any())).thenReturn(new AccountDto());
        Mockito.when(modelMapper.map(any(AccountDto.class),any())).thenReturn(new Accounts());
        Mockito.when(accountRepository.saveAccounts(any())).thenReturn(Mono.just(accounts));
        StepVerifier.create(accountService.addAccount(accountDto)).expectNextCount(1L).verifyComplete();
    }
    @Test
    public void getAccountByHealthIdNumberTests(){
        //Mockito.when(accounts.isProfilePhotoCompressed()).thenReturn(true);
        ReflectionTestUtils.setField(accounts,"profilePhotoCompressed",true);
        Mockito.when(modelMapper.map(any(),any())).thenReturn(new AccountDto());
        Mockito.when(accountRepository.getAccountsByHealthIdNumber(any())).thenReturn(Mono.just(accounts));
        Mockito.when(Mockito.mock(Accounts.class).isProfilePhotoCompressed()).thenReturn(true);
        StepVerifier.create(accountService.getAccountByHealthIdNumber("test")).expectNextCount(1L).verifyComplete();
    }
    @Test
    public void updateAccountByHealthIdNumberTests(){
        Mockito.when(modelMapper.map(any(Accounts.class),any())).thenReturn(new AccountDto());
        Mockito.when(modelMapper.map(any(AccountDto.class),any())).thenReturn(accounts);
        Mockito.when(accountRepository.updateAccounts(any(),any())).thenReturn(Mono.just(accounts));
       Mockito.when(kafkaService.publishPhrUserPatientEventByAccounts(any())).thenReturn(Mono.empty());
        StepVerifier.create(accountService.updateAccountByHealthIdNumber(new AccountDto(),"2343212")).expectNextCount(1L).verifyComplete();
    }
    @Test
    public void getAccountByXmlUidTests(){
        Mockito.when(modelMapper.map(any(Accounts.class),any())).thenReturn(new AccountDto());
        Mockito.when(modelMapper.map(any(AccountDto.class),any())).thenReturn(accounts);
        Mockito.when(accountRepository.getAccountsByXmluid(any())).thenReturn(Mono.just(accounts));
        StepVerifier.create(accountService.getAccountByXmlUid("test")).expectNextCount(1L).verifyComplete();
    }
    @Test
    public void getAccountsByHealthIdNumbersTests(){
        Mockito.when(modelMapper.map(any(Accounts.class),any())).thenReturn(new AccountDto());
        Mockito.when(accountRepository.getAccountsByHealthIdNumbers(any())).thenReturn(Flux.just(accounts));
        StepVerifier.create(accountService.getAccountsByHealthIdNumbers(Arrays.asList("test")))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    public void getAccountByDocumentCodeTests(){
        Mockito.when(modelMapper.map(any(Accounts.class),any())).thenReturn(new AccountDto());
        Mockito.when(accountRepository.getAccountsByDocumentCode(any())).thenReturn(Mono.just(accounts));
        StepVerifier.create(accountService.getAccountByDocumentCode("test"))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    public void getMobileLinkedAccountsCountTests(){
        Mockito.when(accountRepository.getAccountsCountByMobileNumber(any())).thenReturn(Mono.just(1));
        StepVerifier.create(accountService.getMobileLinkedAccountsCount("7432122332"))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    public void getEmailLinkedAccountsCountTests(){
        Mockito.when(accountRepository.getAccountsCountByEmailNumber(any())).thenReturn(Mono.just(1));
        StepVerifier.create(accountService.getEmailLinkedAccountsCount("testemail"))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    public void checkDeDuplicationTests(){
        Mockito.when(modelMapper.map(any(Accounts.class),any())).thenReturn(new AccountDto());
        Mockito.when(modelMapper.map(any(AccountDto.class),any())).thenReturn(accounts);
        Mockito.when(accountRepository.checkDeDuplication(any(),any(),any(),any(),any(),any())).thenReturn(Mono.just(accounts));
        StepVerifier.create(accountService.checkDeDuplication(new DeDuplicationRequest())).expectNextCount(1L).verifyComplete();
    }
    @Test
    public void sendAbhaToKafkaTests(){
        Mockito.when(kafkaService.publishDashBoardAbhaEventByAccounts(any())).thenReturn(Mono.empty());
        StepVerifier.create(accountService.sendAbhaToKafka(new AccountReattemptDto())).expectNextCount(0L).verifyComplete();
    }

}
