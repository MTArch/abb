package in.gov.abdm.abha.enrollment.database.account;

import in.gov.abdm.abha.enrollment.client.AbhaDBAccountFClient;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.services.database.account.impl.AccountServiceImpl;
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

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class AccountDBTests {

    @InjectMocks
    AccountServiceImpl accountService;

    @Mock
    AbhaDBAccountFClient abhaDBAccountFClient;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void findByXmlUidTest(){
        AccountDto accountDto = new AccountDto();
        accountDto.setHealthIdNumber("1234");

        Mockito.when(abhaDBAccountFClient.getAccountByXmlUid(any())).thenReturn(Mono.just(accountDto));
        AccountDto result =  accountService.findByXmlUid("Test").block();

        Assert.assertEquals("Failed to Validate HealthIdNumber",accountDto.getHealthIdNumber(), result.getHealthIdNumber());
    }
}
