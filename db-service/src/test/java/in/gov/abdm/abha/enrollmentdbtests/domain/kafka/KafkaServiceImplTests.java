package in.gov.abdm.abha.enrollmentdbtests.domain.kafka;

import in.gov.abdm.abha.enrollmentdb.domain.hid_phr_address.event.DashboardEventPublisher;
import in.gov.abdm.abha.enrollmentdb.domain.hid_phr_address.event.PHREventPublisher;
import in.gov.abdm.abha.enrollmentdb.domain.hid_phr_address.event.PatientEventPublisher;
import in.gov.abdm.abha.enrollmentdb.domain.kafka.KafkaServiceImpl;
import in.gov.abdm.abha.enrollmentdb.model.account.AccountDto;
import in.gov.abdm.abha.enrollmentdb.model.account.AccountReattemptDto;
import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import in.gov.abdm.abha.enrollmentdb.model.hid_phr_address.HidPhrAddress;
import in.gov.abdm.abha.enrollmentdb.model.hidbenefit.HidBenefit;
import in.gov.abdm.abha.enrollmentdb.model.hidbenefit.HidBenefitDto;
import in.gov.abdm.abha.enrollmentdb.repository.AccountRepository;
import in.gov.abdm.abha.enrollmentdb.repository.HidPhrAddressRepository;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class KafkaServiceImplTests {
    @InjectMocks
    KafkaServiceImpl kafkaService;
    @Mock
    HidPhrAddressRepository hidPhrAddressRepository;
    @Mock
    AccountRepository accountRepository;
    @Mock
    PHREventPublisher phrEventPublisher;
    @Mock
    PatientEventPublisher patientEventPublisher;
    @Mock
    ModelMapper modelMapper;
    @Mock
    DashboardEventPublisher dashboardEventPublisher;
    private HidPhrAddress hidPhrAddress;
    private Accounts accounts;
    private AccountDto accountDto;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        hidPhrAddress=new HidPhrAddress();
        hidPhrAddress.setHealthIdNumber("1232");
        hidPhrAddress.setPhrAddress("phrAddress");
        hidPhrAddress.setStatus("Success");
        accounts=new Accounts();
        accounts.setVerificationStatus("success");
        accounts.setHidPhrAddress(hidPhrAddress);
        accountDto=new AccountDto();
        accountDto.setVerificationStatus("VERIFIED");
        accountDto.setVerificationType("DRIVING_LICENCE");
        accountDto.setHidPhrAddress(hidPhrAddress);

        accountDto.setAddress("Address");
        accountDto.setDistrictCode("1232");
        accountDto.setDistrictName("distName");
        accountDto.setPincode("12321");
        accountDto.setStateCode("12321");
        accountDto.setStateName("stateName");
        accountDto.setSubDistrictCode("121w");
        accountDto.setSubDistrictName("subDistrictName");
        accountDto.setTownCode("432");
        accountDto.setTownName("townName");
        accountDto.setVillageCode("432");
        accountDto.setVillageName("villageANme");
        accountDto.setWardCode("2321");
        accountDto.setWardName("wardName");
        accountDto.setHealthIdNumber("243");
        accountDto.setDayOfBirth("12");
        accountDto.setEmail("firstname_lastname@gmail");
        accountDto.setFirstName("firstname");
        accountDto.setGender("male");
        accountDto.setKycPhoto("kycPhoto");
        accountDto.setLastName("lastname");
        accountDto.setMiddleName("middleName");
        accountDto.setMobile("987654321");
        accountDto.setMonthOfBirth("12");
        accountDto.setName("Name");
        accountDto.setPassword("password");
        accountDto.setHidPhrAddress(hidPhrAddress);
        accountDto.setStatus("SUCCESS");
        accountDto.setYearOfBirth("1998");
        accountDto.setDayOfBirth("12");
        accountDto.setMonthOfBirth("03");
        accountDto.setYearOfBirth("2000");
        accountDto.setLstUpdatedBy("lstUpdatedBy");
        accountDto.setHealthId("healthId");

    }
    @AfterEach
    void tearDown() {
        hidPhrAddress=null;
        accounts=null;
        accountDto=null;
    }
    @Test
    public void publishPhrUserPatientEventTests(){

        Mockito.when(modelMapper.map(any(Accounts.class),any())).thenReturn(accountDto);
        Mockito.when(accountRepository.getAccountsByHealthIdNumber(any())).thenReturn(Mono.just(accounts));
        Mockito.when(accountRepository.getAccountsByHealthIdNumber(any())).thenReturn(Mono.just(accounts));
        StepVerifier.create(kafkaService.publishPhrUserPatientEvent(hidPhrAddress)).expectNextCount(0L).verifyComplete();
        //For else block execution
        hidPhrAddress.setStatus("SYSTEM");
        accountDto.setHidPhrAddress(hidPhrAddress);
        StepVerifier.create(kafkaService.publishPhrUserPatientEvent(hidPhrAddress)).expectNextCount(0L).verifyComplete();
    }
    @Test
    public void publishPhrUserPatientEventByAccounts(){
        Mockito.when(modelMapper.map(any(Accounts.class),any())).thenReturn(accountDto);
        Mockito.when(accountRepository.getAccountsByHealthIdNumber(any())).thenReturn(Mono.just(accounts));
        Mockito.when(hidPhrAddressRepository.getPhrAddressByPhrAddress(any())).thenReturn(Mono.just(hidPhrAddress));
        StepVerifier.create(kafkaService.publishPhrUserPatientEventByAccounts(accountDto)).expectNextCount(0L).verifyComplete();
        //For else block execution
        accountDto.setVerificationType("OTP");
        StepVerifier.create(kafkaService.publishPhrUserPatientEventByAccounts(accountDto)).expectNextCount(0L).verifyComplete();

    }
    @Test
    public void publishDashBoardAbhaEventByAccountsTests(){
        /*Mockito.when(modelMapper.map(any(Accounts.class),any())).thenReturn(accountDto);
        Mockito.when(accountRepository.getAccountsByHealthIdNumber(any())).thenReturn(Mono.just(accounts));
        Mockito.when(hidPhrAddressRepository.getPhrAddressByPhrAddress(any())).thenReturn(Mono.just(hidPhrAddress));
        StepVerifier.create(kafkaService.publishPhrUserPatientEventByAccounts(accountDto)).expectNextCount(0L).verifyComplete();
        //For else block execution
        accountDto.setVerificationType("OTP");*/
        StepVerifier.create(kafkaService.publishDashBoardAbhaEventByAccounts(new AccountReattemptDto())).expectNextCount(0L).verifyComplete();

    }

}
