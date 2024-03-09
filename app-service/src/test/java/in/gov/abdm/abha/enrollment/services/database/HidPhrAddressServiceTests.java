package in.gov.abdm.abha.enrollment.services.database;

import in.gov.abdm.abha.enrollment.client.AbhaDBHidPhrAddressFClient;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.impl.HidPhrAddressServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class HidPhrAddressServiceTests {
    @InjectMocks
    HidPhrAddressServiceImpl hidPhrAddressService;

    @Mock
    AbhaDBHidPhrAddressFClient abhaDBHidPhrAddressFClient;
    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);


    }

    @Test
    public void createHidPhrAddressEntityTest(){
        HidPhrAddressDto hidPhrAddressDto = new HidPhrAddressDto();
        hidPhrAddressDto.setHealthIdNumber("1234");
        Mockito.when(abhaDBHidPhrAddressFClient.createHidPhrAddress(any())).thenReturn(Mono.just(hidPhrAddressDto));
        HidPhrAddressDto result =  hidPhrAddressService.createHidPhrAddressEntity(hidPhrAddressDto).block();

        Assert.assertEquals("Failed to Validate",hidPhrAddressDto.getHealthIdNumber(), result.getHealthIdNumber());

    }

    @Test
    public void prepareNewHidPhrAddressTest(){
        HidPhrAddressDto hidPhrAddressDto = new HidPhrAddressDto();
        AccountDto accountDto = new AccountDto();
        ABHAProfileDto abhaProfileDto = new ABHAProfileDto();
        String phrAddress ="address";
        List<String> phrAddressList = new ArrayList<>();
        phrAddressList.add(phrAddress);
        accountDto.setLstUpdatedBy("last");
        accountDto.setHealthId("123");
        accountDto.setHealthIdNumber("234");
        abhaProfileDto.setAbhaNumber("1234");
        abhaProfileDto.setPhrAddress(phrAddressList);
        hidPhrAddressDto.setHealthIdNumber("1234");

    //  Mockito.when(hidPhrAddressService.prepareNewHidPhrAddress(accountDto,abhaProfileDto)).thenReturn(hidPhrAddressDto);
       HidPhrAddressDto result =  hidPhrAddressService.prepareNewHidPhrAddress(accountDto,abhaProfileDto);
        HidPhrAddressDto result2 =  hidPhrAddressService.prepareNewHidPhrAddress(accountDto);

        Assert.assertEquals("Failed to Validate",hidPhrAddressDto.getHealthIdNumber(), abhaProfileDto.getAbhaNumber());

    }

    @Test
    public void getHidPhrAddressByHealthIdNumbersAndPreferredInTest(){
        HidPhrAddressDto hidPhrAddressDto = new HidPhrAddressDto();
        hidPhrAddressDto.setHealthIdNumber("1234");
        List<String> healthIdNumbers =new ArrayList<>();
        healthIdNumbers.add("123");
        healthIdNumbers.add("43123");
        List<Integer> preferred = new ArrayList<>();
        preferred.add(1);
        preferred.add(2);

        Mockito.when(abhaDBHidPhrAddressFClient.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        HidPhrAddressDto result =  hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(healthIdNumbers,preferred).blockFirst();

        Assert.assertEquals("Failed to Validate",hidPhrAddressDto.getHealthIdNumber(), result.getHealthIdNumber());

    }

    @Test
    public void findByPhrAddressInTests(){
        HidPhrAddressDto hidPhrAddressDto = new HidPhrAddressDto();
        hidPhrAddressDto.setHealthIdNumber("1234");
        List<String> phrAddress =new ArrayList<>();
        phrAddress.add("address1");
        phrAddress.add("address2");
        Mockito.when(abhaDBHidPhrAddressFClient.findByPhrAddressIn(any())).thenReturn(Flux.just(hidPhrAddressDto));
        HidPhrAddressDto result =  hidPhrAddressService.findByPhrAddressIn(phrAddress).blockFirst();

        Assert.assertEquals("Failed to Validate",hidPhrAddressDto.getHealthIdNumber(), result.getHealthIdNumber());

    }
    @Test
    public void getPhrAddressByPhrAddressTests(){
        HidPhrAddressDto hidPhrAddressDto = new HidPhrAddressDto();
        hidPhrAddressDto.setHealthIdNumber("1234");
        String phrAddress ="address";
        Mockito.when(abhaDBHidPhrAddressFClient.getPhrAddress(any())).thenReturn(Mono.just(hidPhrAddressDto));
        HidPhrAddressDto result =  hidPhrAddressService.getPhrAddressByPhrAddress(phrAddress).block();

        Assert.assertEquals("Failed to Validate",hidPhrAddressDto.getHealthIdNumber(), result.getHealthIdNumber());

    }
    @Test
    public void findByHealthIdNumberTests(){
        HidPhrAddressDto hidPhrAddressDto = new HidPhrAddressDto();
        hidPhrAddressDto.setHealthIdNumber("1234");
        String healthIdNumber ="address";
        Mockito.when(abhaDBHidPhrAddressFClient.findByByHealthIdNumber(any())).thenReturn(Mono.just(hidPhrAddressDto));
        HidPhrAddressDto result =  hidPhrAddressService.findByHealthIdNumber(healthIdNumber).block();

        Assert.assertEquals("Failed to Validate",hidPhrAddressDto.getHealthIdNumber(), result.getHealthIdNumber());

    }
    @Test
    public void updateHidPhrAddressByIdTests(){
        HidPhrAddressDto hidPhrAddressDto = new HidPhrAddressDto();
        hidPhrAddressDto.setHealthIdNumber("1234");
        Long hidPhrAddressId =1L;
        Mockito.when(abhaDBHidPhrAddressFClient.updateHidPhrAddress(any(),any())).thenReturn(Mono.just(hidPhrAddressDto));
        HidPhrAddressDto result =  hidPhrAddressService.updateHidPhrAddressById(hidPhrAddressDto,hidPhrAddressId).block();

        Assert.assertEquals("Failed to Validate",hidPhrAddressDto.getHealthIdNumber(), result.getHealthIdNumber());

    }


}
