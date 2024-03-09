package in.gov.abdm.abha.enrollmentdbtests.controller;

import in.gov.abdm.abha.enrollmentdb.controller.HidPhrAddressController;
import in.gov.abdm.abha.enrollmentdb.domain.hid_phr_address.HidPhrAddressService;
import in.gov.abdm.abha.enrollmentdb.model.hid_phr_address.HidPhrAddressDto;
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
public class HidPhrAddressControllerTests {
    @InjectMocks
    HidPhrAddressController hidPhrAddressController;
    @Mock
    HidPhrAddressService hidPhrAddressService;
    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void createHidPhrAddressTests(){
        Mockito.when(hidPhrAddressService.addHidPhrAddress(any())).thenReturn(Mono.just(new HidPhrAddressDto()));
        ResponseEntity<Mono<HidPhrAddressDto>> response= hidPhrAddressController.createHidPhrAddress(new HidPhrAddressDto());
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void updateHidPhrAddressTests(){
        Mockito.when(hidPhrAddressService.updateHidPhrAddressById(any(),any())).thenReturn(Mono.just(new HidPhrAddressDto()));
        ResponseEntity<Mono<HidPhrAddressDto>> response= hidPhrAddressController.updateHidPhrAddress(new HidPhrAddressDto(),1L);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void getHidPhrAddressTests(){
        Mockito.when(hidPhrAddressService.getHidPhrAddressById(any())).thenReturn(Mono.just(new HidPhrAddressDto()));
        ResponseEntity<Mono<HidPhrAddressDto>> response= hidPhrAddressController.getHidPhrAddress(1L);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void deleteHidPhrAddressTests(){
        Mockito.when(hidPhrAddressService.deleteHidPhrAddressById(any())).thenReturn(Mono.empty());
        ResponseEntity<Mono<Void>> response= hidPhrAddressController.deleteHidPhrAddress(1L);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void getHidPhrAddressByHealthIdNumbersAndPreferredInTests(){
        Mockito.when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(new HidPhrAddressDto()));
        ResponseEntity<Flux<HidPhrAddressDto>> response= hidPhrAddressController.getHidPhrAddressByHealthIdNumbersAndPreferredIn(Arrays.asList("test"),Arrays.asList(1));
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void findByPhrAddressInTests(){
        Mockito.when(hidPhrAddressService.findByPhrAddressIn(any())).thenReturn(Flux.just(new HidPhrAddressDto()));
        ResponseEntity<Flux<HidPhrAddressDto>> response= hidPhrAddressController.findByPhrAddressIn(Arrays.asList("test"));
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void getPhrAddressTests(){
        Mockito.when(hidPhrAddressService.getPhrAddressByPhrAddress(any())).thenReturn(Mono.just(new HidPhrAddressDto()));
        ResponseEntity<Mono<HidPhrAddressDto>> response= hidPhrAddressController.getPhrAddress("test");
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    public void findByHealthIdNumberTests(){
        Mockito.when(hidPhrAddressService.findByHealthIdNumber(any())).thenReturn(Mono.just(new HidPhrAddressDto()));
        ResponseEntity<Mono<HidPhrAddressDto>> response= hidPhrAddressController.findByHealthIdNumber("test");
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

}
