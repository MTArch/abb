package in.gov.abdm.abha.enrollmentdbtests.domain.hid_phr_address;

import in.gov.abdm.abha.enrollmentdb.domain.account.AccountService;
import in.gov.abdm.abha.enrollmentdb.domain.hid_phr_address.HidPhrAddressServiceImpl;
import in.gov.abdm.abha.enrollmentdb.domain.kafka.KafkaService;
import in.gov.abdm.abha.enrollmentdb.model.account.AccountDto;
import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import in.gov.abdm.abha.enrollmentdb.model.hid_phr_address.HidPhrAddress;
import in.gov.abdm.abha.enrollmentdb.model.hid_phr_address.HidPhrAddressDto;
import in.gov.abdm.abha.enrollmentdb.repository.HidPhrAddressRepository;
import jdk.jfr.Enabled;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(SpringExtension.class)
public class HidPhrAddressServiceImplTests {
    @InjectMocks
    HidPhrAddressServiceImpl hidPhrAddressService;
    @Mock
    HidPhrAddressRepository hidPhrAddressRepository;
    @Mock
    private AccountService accountService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    KafkaService kafkaService;
    @BeforeEach
    void setup() {
            MockitoAnnotations.openMocks(this);
        HidPhrAddressDto h2 = new HidPhrAddressDto(1L,"","","",1,"", LocalDateTime.now(),"","",LocalDateTime.now(),1,1,true);

        HidPhrAddress h = new HidPhrAddress(1L,"","","",1,"", LocalDateTime.now(),"","",LocalDateTime.now(),1,1,true);
    }
    @AfterEach
    void teardown() {
    }
    @Test
    public void addHidPhrAddressTests(){
        Mockito.when(modelMapper.map(any(HidPhrAddressDto.class),any())).thenReturn(new HidPhrAddress());
        Mockito.when(modelMapper.map(any(HidPhrAddress.class),any())).thenReturn(new HidPhrAddressDto());
        Mockito.when(kafkaService.publishPhrUserPatientEvent(any())).thenReturn(Mono.empty());
        Mockito.when(hidPhrAddressRepository.save(any())).thenReturn(Mono.just(new HidPhrAddress()));
        StepVerifier.create(hidPhrAddressService.addHidPhrAddress(new HidPhrAddressDto())).expectNextCount(1L).verifyComplete();
    }
    @Test
    public void updateHidPhrAddressByIdTests(){
        Mockito.when(modelMapper.map(any(HidPhrAddressDto.class),any())).thenReturn(new HidPhrAddress());
        Mockito.when(modelMapper.map(any(HidPhrAddress.class),any())).thenReturn(new HidPhrAddressDto());
        Mockito.when(kafkaService.publishPhrUserPatientEvent(any())).thenReturn(Mono.empty());
        Mockito.when(hidPhrAddressRepository.save(any())).thenReturn(Mono.just(new HidPhrAddress()));
        StepVerifier.create(hidPhrAddressService.updateHidPhrAddressById(new HidPhrAddressDto(),1L)).expectNextCount(1L).verifyComplete();
    }
    @Test
    public void getHidPhrAddressByIdTests(){
        Mockito.when(modelMapper.map(any(HidPhrAddress.class),any())).thenReturn(new HidPhrAddressDto());
        Mockito.when(hidPhrAddressRepository.findById(anyLong())).thenReturn(Mono.just(new HidPhrAddress()));
        StepVerifier.create(hidPhrAddressService.getHidPhrAddressById(1L)).expectNextCount(1L).verifyComplete();
    }
    @Test
    public void deleteHidPhrAddressByIdTests(){
        Mockito.when(hidPhrAddressRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        StepVerifier.create(hidPhrAddressService.deleteHidPhrAddressById(1L)).expectNextCount(0L).verifyComplete();
    }
    @Test
    public void getHidPhrAddressByHealthIdNumbersAndPreferredInTests(){
        Mockito.when(modelMapper.map(any(HidPhrAddress.class),any())).thenReturn(new HidPhrAddressDto());
        Mockito.when(hidPhrAddressRepository.findByHealthIdNumberInAndPreferredIn(any(),any())).thenReturn(Flux.just(new HidPhrAddress()));
        StepVerifier.create(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(Arrays.asList("123"),Arrays.asList(1))).expectNextCount(1L).verifyComplete();
    }
    @Test
    public void findByPhrAddressInTests(){
        Mockito.when(modelMapper.map(any(HidPhrAddress.class),any())).thenReturn(new HidPhrAddressDto());
        Mockito.when(hidPhrAddressRepository.findByPhrAddressIn(any())).thenReturn(Flux.just(new HidPhrAddress()));
        StepVerifier.create(hidPhrAddressService.findByPhrAddressIn(Arrays.asList("123"))).expectNextCount(1L).verifyComplete();
    }
    @Test
    public void getPhrAddressByPhrAddressTests(){
        Mockito.when(modelMapper.map(any(HidPhrAddress.class),any())).thenReturn(new HidPhrAddressDto());
        Mockito.when(hidPhrAddressRepository.getPhrAddressByPhrAddress(any())).thenReturn(Mono.just(new HidPhrAddress()));
        StepVerifier.create(hidPhrAddressService.getPhrAddressByPhrAddress("Test")).expectNextCount(1L).verifyComplete();
    }
    @Test
    public void findByHealthIdNumberTests(){
        Mockito.when(modelMapper.map(any(HidPhrAddressDto.class),any())).thenReturn(new HidPhrAddressDto());
        //Mockito.when(modelMapper.map(any(HidPhrAddress.class),any())).thenReturn(new HidPhrAddressDto());
        Mockito.when(hidPhrAddressRepository.findByHealthIdNumber(any())).thenReturn(Mono.just(new HidPhrAddressDto()));
        StepVerifier.create(hidPhrAddressService.findByHealthIdNumber("Test")).expectNext(new HidPhrAddressDto()).verifyComplete();
    }





}
