package in.gov.abdm.abha.enrollment.services.deduplication;

import in.gov.abdm.abha.enrollment.client.AbhaDBAccountFClient;
import in.gov.abdm.abha.enrollment.model.de_duplication.DeDuplicationRequest;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import in.gov.abdm.abha.enrollment.services.de_duplication.DeDuplicationService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class DeDuplicationServiceTests {

    @InjectMocks
    DeDuplicationService deDuplicationService = new DeDuplicationService();

    @Mock
    AbhaDBAccountFClient abhaDBAccountFClient;

    @Test
    public void createHidPhrAddressEntityTest(){
        AccountDto accountDto = new AccountDto();
        accountDto.setHealthIdNumber("234");
        DeDuplicationRequest deDuplicationRequest = new DeDuplicationRequest();
        deDuplicationRequest.setFirstName("FirstName");
        deDuplicationRequest.setLastName("lastName");
        deDuplicationRequest.setGender("Female");
       // Mockito.when(abhaDBAccountFClient.checkDeDuplication(any())).thenReturn(Mono.just(accountDto));
        AccountDto result =  deDuplicationService.checkDeDuplication(deDuplicationRequest).block();

        Assert.assertEquals("Failed to Validate","", "");

    }
    @Test
    public void prepareRequestTest(){
        AccountDto accountDto = new AccountDto();
        accountDto.setFirstName("firstname");
        accountDto.setLastName("lastname");
        accountDto.setDayOfBirth("12");
        accountDto.setMonthOfBirth("12");
        accountDto.setYearOfBirth("2000");
        accountDto.setHealthIdNumber("234");
        accountDto.setGender("Female");
        DeDuplicationRequest deDuplicationRequest = new DeDuplicationRequest();
        deDuplicationRequest.setFirstName("FirstName");
        deDuplicationRequest.setLastName("lastName");
        deDuplicationRequest.setGender("Female");
        deDuplicationRequest.setDob(12);
        deDuplicationRequest.setMob(10);
        deDuplicationRequest.setYob(2000);
        DeDuplicationRequest deDuplicationRequest1 = new DeDuplicationRequest();
        deDuplicationRequest1.setLastName(deDuplicationRequest.getLastName());
        deDuplicationRequest1.setFirstName(deDuplicationRequest.getFirstName());
        deDuplicationRequest1.setYob(deDuplicationRequest.getYob());
        deDuplicationRequest1.setDob(deDuplicationRequest.getDob());
        deDuplicationRequest1.setMob(deDuplicationRequest.getMob());
        deDuplicationRequest1.setGender(deDuplicationRequest.getGender());

        // Mockito.when(abhaDBAccountFClient.checkDeDuplication(any())).thenReturn(Mono.just(accountDto));
        DeDuplicationRequest result =  deDuplicationService.prepareRequest(accountDto);
        Assert.assertEquals("Failed to Validate","", "");

    }

}
