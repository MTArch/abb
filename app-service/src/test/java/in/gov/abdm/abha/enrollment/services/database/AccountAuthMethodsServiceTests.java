package in.gov.abdm.abha.enrollment.services.database;

import in.gov.abdm.abha.enrollment.client.AbhaDBAccountActionFClient;
import in.gov.abdm.abha.enrollment.client.AbhaDBAccountAuthMethodsFClient;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.model.entities.AccountActionDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountAuthMethodsDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.services.database.account_auth_methods.impl.AccountAuthMethodsServiceImpl;
import in.gov.abdm.abha.enrollment.services.database.accountaction.impl.AccountActionServiceImpl;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class AccountAuthMethodsServiceTests {
    @InjectMocks
    AccountAuthMethodsServiceImpl accountAuthMethodsService;

    @Mock
    AbhaDBAccountAuthMethodsFClient abhaDBAccountAuthMethodsFClient;

    @Mock
    AbhaDBAccountActionFClient abhaDBAccountActionFClient;

    @InjectMocks
    AccountActionServiceImpl accountActionService;
    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);


    }
    @Test
    public void addAccountAuthMethodsTest(){
        AccountAuthMethodsDto accountAuthMethodsDto =new AccountAuthMethodsDto();
        accountAuthMethodsDto.setAuthMethods("BIO");
        String s=accountAuthMethodsDto.getAuthMethods();
        accountAuthMethodsDto.setHealthIdNumber("12345");
        List<AccountAuthMethodsDto> listauthDto = new ArrayList<>();
        listauthDto.add(accountAuthMethodsDto);

        Mockito.when(abhaDBAccountAuthMethodsFClient.addAccountAuthMethods(any())).thenReturn(Mono.just(listauthDto));
        List<AccountAuthMethodsDto> result =  accountAuthMethodsService.addAccountAuthMethods(listauthDto).block();
        Assert.assertEquals("Failed to Validate",accountAuthMethodsDto.getHealthIdNumber(), result.get(0).getHealthIdNumber());
    }

    @Test
    public void deleteAccountAuthMethodByHealthId(){
        String healthIdNumber="12334";
        Mockito.when(abhaDBAccountAuthMethodsFClient.deleteAccountAuthMethodByHealthId(any())).thenReturn(Mono.empty());
        accountAuthMethodsService.deleteAccountAuthMethodByHealthId(healthIdNumber).block();
    }

    @Test
    public void createAccountActionEntityTests(){
        AccountActionDto accountActionDto =  new AccountActionDto();
        accountActionDto.setHealthIdNumber("12334");
        accountActionDto.setNewAccount(true);
        accountActionDto.setAction("action");
        accountActionDto.setField("Field");
        Mockito.when(abhaDBAccountActionFClient.postAccountAction(any())).thenReturn(Mono.just(accountActionDto));
        Mockito.when(accountActionService.createAccountActionEntity(accountActionDto)).thenReturn(Mono.just(accountActionDto));

        AccountActionDto result =  accountActionService.createAccountActionEntity(accountActionDto).block();

        Assert.assertEquals("Failed to Validate",accountActionDto.getHealthIdNumber(), result.getHealthIdNumber());
    }


}
