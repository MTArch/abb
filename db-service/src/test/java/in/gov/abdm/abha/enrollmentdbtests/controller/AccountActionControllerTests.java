package in.gov.abdm.abha.enrollmentdbtests.controller;

import in.gov.abdm.abha.enrollmentdb.controller.AccountActionController;
import in.gov.abdm.abha.enrollmentdb.domain.accountaction.AccountActionService;
import in.gov.abdm.abha.enrollmentdb.model.accountaction.AccountActionDto;
import in.gov.abdm.abha.enrollmentdb.model.accountaction.AccountActions;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
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
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class AccountActionControllerTests {
    @InjectMocks
    AccountActionController accountActionController;
    @Mock
    AccountActionService accountActionService;

    private AccountActionDto accountActionDto;
    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        accountActionDto=new AccountActionDto();
    }
    @AfterEach
    void teardown(){
        accountActionDto=null;
    }

    @Test
    public void getAccountByHealthIdNumberTests(){
        Mockito.when(accountActionService.getAccountActionByHealthIdNumber(any())).thenReturn(Mono.just(accountActionDto));
        ResponseEntity<Mono<AccountActionDto>> response= accountActionController.getAccountByHealthIdNumber("Test");
        Assert.assertEquals(response.getStatusCode(),HttpStatus.OK);
    }
    @Test
    public void saveAccountActionTests(){
        Mockito.when(accountActionService.addAccount(any())).thenReturn(Mono.just(new AccountActions()));
        ResponseEntity<Mono<AccountActions>> response= accountActionController.saveAccountAction(new AccountActions());
        Assert.assertEquals(response.getStatusCode(),HttpStatus.OK);
    }

}
