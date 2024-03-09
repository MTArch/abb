package in.gov.abdm.abha.enrollmentdbtests.domain.accountation;

import in.gov.abdm.abha.enrollmentdb.domain.accountaction.AccountActionServiceImpl;
import in.gov.abdm.abha.enrollmentdb.domain.accountaction.AccountActionSubscriber;
import in.gov.abdm.abha.enrollmentdb.model.account.AccountDto;
import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import in.gov.abdm.abha.enrollmentdb.model.accountaction.AccountActionDto;
import in.gov.abdm.abha.enrollmentdb.model.accountaction.AccountActions;
import in.gov.abdm.abha.enrollmentdb.repository.AccountActionRepository;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class AccountActionServiceImplTests {
    @InjectMocks
    AccountActionServiceImpl accountActionService;
    @Mock
    AccountActionRepository accountActionRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AccountActionSubscriber accountSubscriber;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void teardown(){

    }
    @Test
    public void getAccountActionByHealthIdNumberTests(){
        /*Mockito.when(modelMapper.map(any(Accounts.class),any())).thenReturn(new AccountDto());
        Mockito.when(modelMapper.map(any(AccountDto.class),any())).thenReturn(new Accounts());*/
        Mockito.when(accountActionRepository.getAccountsByHealthIdNumber(any())).thenReturn(Mono.just(new AccountActionDto()));
        StepVerifier.create(accountActionService.getAccountActionByHealthIdNumber("Test")).expectNextCount(1L).verifyComplete();
    }
    @Test
    public void addAccountTests(){
        /*Mockito.when(modelMapper.map(any(Accounts.class),any())).thenReturn(new AccountDto());
        Mockito.when(modelMapper.map(any(AccountDto.class),any())).thenReturn(new Accounts());*/
        Mockito.when(accountActionRepository.save(any())).thenReturn(Mono.just(new AccountActions()));
        StepVerifier.create(accountActionService.addAccount(new AccountActions())).expectNextCount(1L).verifyComplete();
    }
}
