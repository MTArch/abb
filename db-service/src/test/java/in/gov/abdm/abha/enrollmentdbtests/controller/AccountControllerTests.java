package in.gov.abdm.abha.enrollmentdbtests.controller;

import in.gov.abdm.abha.enrollmentdb.controller.AccountController;
import in.gov.abdm.abha.enrollmentdb.domain.account.AccountService;
import in.gov.abdm.abha.enrollmentdb.model.account.AccountDto;
import in.gov.abdm.abha.enrollmentdb.model.account.AccountReattemptDto;
import in.gov.abdm.abha.enrollmentdb.model.de_duplication.DeDuplicationRequest;
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
public class AccountControllerTests {
    @InjectMocks
    AccountController accountController;
    @Mock
    AccountService accountService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void getAccountByHealthIdNumberTests(){
        Mockito.when(accountService.getAccountByHealthIdNumber(any())).thenReturn(Mono.just(new AccountDto()));
        ResponseEntity<Mono<AccountDto>> response= accountController.getAccountByHealthIdNumber("Test");
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void createAccountTests(){
        Mockito.when(accountService.addAccount(any())).thenReturn(Mono.just(new AccountDto()));
        ResponseEntity<Mono<AccountDto>> response= accountController.createAccount(new AccountDto());
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void updateAccountTests(){
        Mockito.when(accountService.updateAccountByHealthIdNumber(any(),any())).thenReturn(Mono.just(new AccountDto()));
        ResponseEntity<Mono<AccountDto>> response= accountController.updateAccount(new AccountDto(),"Test");
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void getAccountByXmlUidTests(){
        Mockito.when(accountService.getAccountByXmlUid(any())).thenReturn(Mono.just(new AccountDto()));
        ResponseEntity<Mono<AccountDto>> response= accountController.getAccountByXmlUid("Test");
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void getAccountsByHealthIdNumbersTests(){
        Mockito.when(accountService.getAccountsByHealthIdNumbers(any())).thenReturn(Flux.just(new AccountDto()));
        ResponseEntity<Flux<AccountDto>> response= accountController.getAccountsByHealthIdNumbers(Arrays.asList("Test"));
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void getAccountByDocumentCodeTests(){
        Mockito.when(accountService.getAccountByDocumentCode(any())).thenReturn(Mono.just(new AccountDto()));
        ResponseEntity<Mono<AccountDto>> response= accountController.getAccountByDocumentCode("Test");
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void getMobileLinkedAccountCountTests(){
        Mockito.when(accountService.getMobileLinkedAccountsCount(any())).thenReturn(Mono.just(1));
        ResponseEntity<Mono<Integer>> response= accountController.getMobileLinkedAccountCount("782924242");
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void getEmailLinkedAccountCountTests(){
        Mockito.when(accountService.getEmailLinkedAccountsCount(any())).thenReturn(Mono.just(1));
        ResponseEntity<Mono<Integer>> response= accountController.getEmailLinkedAccountCount("email");
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void checkDeDuplicationTests(){
        Mockito.when(accountService.checkDeDuplication(any())).thenReturn(Mono.just(new AccountDto()));
        ResponseEntity<Mono<AccountDto>> response= accountController.checkDeDuplication(new DeDuplicationRequest());
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void sendReattemptAbhaTests(){
        Mockito.when(accountService.sendAbhaToKafka(any())).thenReturn(Mono.empty());
        ResponseEntity<Mono<Void>> response= accountController.sendReattemptAbha(new AccountReattemptDto());
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }




}
