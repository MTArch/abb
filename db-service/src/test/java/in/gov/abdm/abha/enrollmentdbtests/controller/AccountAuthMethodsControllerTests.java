package in.gov.abdm.abha.enrollmentdbtests.controller;

import in.gov.abdm.abha.enrollmentdb.controller.AccountAuthMethodsController;
import in.gov.abdm.abha.enrollmentdb.domain.accountauthmethods.AccountAuthMethodsService;
import in.gov.abdm.abha.enrollmentdb.model.accountaction.AccountActionDto;
import in.gov.abdm.abha.enrollmentdb.model.accountauthmethods.AccountAuthMethods;
import in.gov.abdm.abha.enrollmentdb.model.accountauthmethods.AccountAuthMethodsDto;
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

@ExtendWith(SpringExtension.class)
public class AccountAuthMethodsControllerTests {
    @InjectMocks
    AccountAuthMethodsController accountAuthMethodsController;
    @Mock
    AccountAuthMethodsService accountAuthMethodsService;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void createAuthMethods(){
        Mockito.when(accountAuthMethodsService.addAccountAuthMethods(any())).thenReturn(Flux.just(new AccountAuthMethodsDto()));
        ResponseEntity<Flux<AccountAuthMethodsDto>> response= accountAuthMethodsController.createAuthMethods(Arrays.asList(new AccountAuthMethods()));
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void deleteAccountAuthByHealthId(){
        Mockito.when(accountAuthMethodsService.deleteAccountAuthMethodsByHealthId(any())).thenReturn(Mono.empty());
        Mono<Void> response= accountAuthMethodsController.deleteAccountAuthByHealthId("test");
       // Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
}
