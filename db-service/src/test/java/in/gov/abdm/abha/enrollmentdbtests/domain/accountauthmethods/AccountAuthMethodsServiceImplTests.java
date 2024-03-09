package in.gov.abdm.abha.enrollmentdbtests.domain.accountauthmethods;

import in.gov.abdm.abha.enrollmentdb.domain.accountauthmethods.AccountAuthMethodsServiceImpl;
import in.gov.abdm.abha.enrollmentdb.domain.accountauthmethods.AccountAuthMethodsSubscriber;
import in.gov.abdm.abha.enrollmentdb.model.account.AccountDto;
import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import in.gov.abdm.abha.enrollmentdb.model.accountauthmethods.AccountAuthMethods;
import in.gov.abdm.abha.enrollmentdb.model.accountauthmethods.AccountAuthMethodsDto;
import in.gov.abdm.abha.enrollmentdb.repository.AccountAuthMethodsRepository;
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

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class AccountAuthMethodsServiceImplTests {
    @InjectMocks
    AccountAuthMethodsServiceImpl accountAuthMethodsService;
    @Mock
    AccountAuthMethodsRepository accountAuthMethodsRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AccountAuthMethodsSubscriber accountAuthMethodSubscriber;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void addAccountAuthMethodsTests(){
        Mockito.when(modelMapper.map(any(AccountAuthMethods.class),any())).thenReturn(new AccountAuthMethodsDto());
        Mockito.when(accountAuthMethodsRepository.saveIfNotExist(any())).thenReturn(Mono.just(new AccountAuthMethods()));
        StepVerifier.create(accountAuthMethodsService.addAccountAuthMethods(Arrays.asList(new AccountAuthMethods()))).expectNextCount(1L).verifyComplete();
    }
    @Test
    public void deleteAccountAuthMethodsByHealthIdTests(){
        Mockito.when(modelMapper.map(any(AccountAuthMethods.class),any())).thenReturn(new AccountAuthMethodsDto());
        Mockito.when(accountAuthMethodsRepository.deleteByHealthId(any())).thenReturn(Mono.empty());
        accountAuthMethodsService.deleteAccountAuthMethodsByHealthId("test");
    }

}
