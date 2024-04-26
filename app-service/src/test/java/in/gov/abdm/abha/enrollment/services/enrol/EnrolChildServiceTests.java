package in.gov.abdm.abha.enrollment.services.enrol;

import in.gov.abdm.abha.enrollment.client.AbhaDBAccountFClient;
import in.gov.abdm.abha.enrollment.client.HidBenefitDBFClient;
import in.gov.abdm.abha.enrollment.configuration.XTokenContextHolder;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.exception.application.AbhaUnAuthorizedException;
import in.gov.abdm.abha.enrollment.exception.hidbenefit.BenefitNotFoundException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AuthData;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.ChildDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.ConsentDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.entities.*;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.model.notification.NotificationResponseDto;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.account_auth_methods.AccountAuthMethodService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.demographic.EnrolByDemographicService;
import in.gov.abdm.abha.enrollment.services.enrol.child.EnrolChildService;
import in.gov.abdm.abha.enrollment.services.notification.NotificationService;
import in.gov.abdm.abha.enrollment.services.redis.RedisService;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaAddressGenerator;
import in.gov.abdm.abha.enrollment.utilities.jwt.JWTUtil;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class EnrolChildServiceTests {
    @InjectMocks
    EnrolChildService enrolChildService;
    @Mock
    private AccountService accountService;
    @Mock
    HidBenefitDBFClient hidBenefitDBFClient;
    @Mock
    private EnrolByDemographicService validator;
    @Mock
    private AbhaDBAccountFClient abhaDBAccountFClient;
    @Mock
    private JWTUtil jwtUtil;
    @Mock
    private HidPhrAddressService hidPhrAddressService;
    @Mock
    private RSAUtil rsaUtil;
    @Mock
    private PasswordEncoder bcryptEncoder;
    @Mock
    private AccountAuthMethodService accountAuthMethodService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private RedisService redisService;
    @Mock
    private EnrolByDemographicService enrolByDemographicService;
    @Mock
    private AbhaAddressGenerator abhaAddressGenerator;
    private RequestHeaders requestHeaders;
    private AccountDto accountDto;
    private EnrolByAadhaarRequestDto enrolByAadhaarRequestDto;
    private AuthData authData;
    private ConsentDto consentDto;
    private HidPhrAddressDto hidPhrAddressDto;
    @BeforeEach
    void setup()
    {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(enrolChildService,"childAbhaAccountLimit",1);
        ReflectionTestUtils.setField(enrolChildService,"childAbhaAgeLimit",10);
        requestHeaders = new RequestHeaders(List.of("role1"),"1","benefitName",new HashMap<>(),new XTokenContextHolder("1","","","","","",""));
        enrolByAadhaarRequestDto=new EnrolByAadhaarRequestDto();
        authData=new AuthData();
        consentDto=new ConsentDto();
        hidPhrAddressDto=new HidPhrAddressDto(1L,"","add1","",1,"", LocalDateTime.now(),"","",LocalDateTime.now(),1,1,true);
        authData.setChildDto(new ChildDto("name","12","12","2021","F","password","photo","true"));
        enrolByAadhaarRequestDto.setConsent(consentDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        accountDto=new AccountDto();
        accountDto.setHealthIdNumber("1");
        accountDto.setDayOfBirth("12");
        accountDto.setMonthOfBirth("05");
        accountDto.setDayOfBirth("10");
        accountDto.setMonthOfBirth("02");
        accountDto.setYearOfBirth("2021");
        accountDto.setMobile("9827773382");
        accountDto.setYearOfBirth("2000");
        accountDto.setStateCode("1");
        accountDto.setDistrictCode("2");
        accountDto.setName("name");
        accountDto.setKycdob("12-12-2021");
        accountDto.setGender("F");
        accountDto.setStatus(AccountStatus.ACTIVE.getValue());
    }
    @AfterEach
    void tearDown(){

    }
    //@Test
    //TODO FIX
    public void enrolTest(){
        Mockito.when(enrolByDemographicService.isValidAge(any())).thenReturn(true);
        Mockito.when(abhaDBAccountFClient.getAccountsEntityByDocumentCode(any())).thenReturn(Flux.just(accountDto));
        Mockito.when(accountService.getAccountByHealthIdNumber(any())).thenReturn(Mono.just(accountDto));
        Mockito.when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        StepVerifier.create(enrolChildService.enrol(enrolByAadhaarRequestDto, requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    //@Test
    //TODO FIX
    public void enrolTest2(){
        Mockito.when(enrolByDemographicService.isValidAge(any())).thenReturn(true);
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("1");
        Mockito.when(abhaDBAccountFClient.getAccountsEntityByDocumentCode(any())).thenReturn(Flux.empty());
        Mockito.when(accountService.getAccountByHealthIdNumber(any())).thenReturn(Mono.just(accountDto));
        Mockito.when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        Mockito.when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        Mockito.when(hidPhrAddressService.prepareNewHidPhrAddress(any())).thenReturn(hidPhrAddressDto);
        Mockito.when(hidBenefitDBFClient.saveHidBenefit(any())).thenReturn(Mono.just(new HidBenefitDto()));
        Mockito.when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(List.of(new AccountAuthMethodsDto())));
        Mockito.when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        Mockito.when(notificationService.sendABHACreationSMS(any(),any(),any())).thenReturn(Mono.just(new NotificationResponseDto()));
        StepVerifier.create(enrolChildService.enrol(enrolByAadhaarRequestDto, requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    //@Test
    //TODO FIX
    public void enrolTest3(){
        authData.setChildDto(new ChildDto("name mid","12","12","2021","F","password","photo","true"));
        enrolByAadhaarRequestDto.setAuthData(authData);
        Mockito.when(enrolByDemographicService.isValidAge(any())).thenReturn(true);
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("1");
        Mockito.when(abhaDBAccountFClient.getAccountsEntityByDocumentCode(any())).thenReturn(Flux.empty());
        Mockito.when(accountService.getAccountByHealthIdNumber(any())).thenReturn(Mono.just(accountDto));
        Mockito.when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        Mockito.when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        Mockito.when(hidPhrAddressService.prepareNewHidPhrAddress(any())).thenReturn(hidPhrAddressDto);
        Mockito.when(hidBenefitDBFClient.saveHidBenefit(any())).thenReturn(Mono.just(new HidBenefitDto()));
        Mockito.when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(List.of(new AccountAuthMethodsDto())));
        Mockito.when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        Mockito.when(notificationService.sendABHACreationSMS(any(),any(),any())).thenReturn(Mono.just(new NotificationResponseDto()));
        StepVerifier.create(enrolChildService.enrol(enrolByAadhaarRequestDto, requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    //TODO FIX
    // @Test
    public void enrolTest4(){
        authData.setChildDto(new ChildDto("name mid lst","12","12","2021","F","password","photo","true"));
        enrolByAadhaarRequestDto.setAuthData(authData);
        Mockito.when(enrolByDemographicService.isValidAge(any())).thenReturn(true);
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("1");
        Mockito.when(abhaDBAccountFClient.getAccountsEntityByDocumentCode(any())).thenReturn(Flux.empty());
        Mockito.when(accountService.getAccountByHealthIdNumber(any())).thenReturn(Mono.just(accountDto));
        Mockito.when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        Mockito.when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        Mockito.when(hidPhrAddressService.prepareNewHidPhrAddress(any())).thenReturn(hidPhrAddressDto);
        Mockito.when(hidBenefitDBFClient.saveHidBenefit(any())).thenReturn(Mono.just(new HidBenefitDto()));
        Mockito.when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(List.of(new AccountAuthMethodsDto())));
        Mockito.when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        Mockito.when(notificationService.sendABHACreationSMS(any(),any(),any())).thenReturn(Mono.just(new NotificationResponseDto()));
        StepVerifier.create(enrolChildService.enrol(enrolByAadhaarRequestDto, requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    public void getChildrenTest(){
        Mockito.when(abhaDBAccountFClient.getAccountsEntityByDocumentCode(any())).thenReturn(Flux.just(accountDto));
        StepVerifier.create(enrolChildService.getChildren(requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    public void validateChildHeadersTest(){
        Mockito.when(abhaDBAccountFClient.getAccountsEntityByDocumentCode(any())).thenReturn(Flux.just(accountDto));
        Mockito.when( redisService.getIntegratedPrograms()).thenReturn(List.of(new IntegratedProgramDto("1","","","benefitName","1","","","",LocalDateTime.now(),LocalDateTime.now(),"","")));
        Mockito.when(redisService.reloadAndGetIntegratedPrograms()).thenReturn(Mono.just(List.of(new IntegratedProgramDto("1","","","benefitName","1","","","",LocalDateTime.now(),LocalDateTime.now(),"",""))));

        StepVerifier.create(enrolChildService.validateChildHeaders(requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    public void validateChildHeadersTest2(){
        Mockito.when(abhaDBAccountFClient.getAccountsEntityByDocumentCode(any())).thenReturn(Flux.just(accountDto));
        Mockito.when( redisService.getIntegratedPrograms()).thenReturn(List.of(new IntegratedProgramDto("1","","","benefitName","12","","","",LocalDateTime.now(),LocalDateTime.now(),"","")));
        Mockito.when(redisService.reloadAndGetIntegratedPrograms()).thenReturn(Mono.just(List.of(new IntegratedProgramDto("1","","","benefitName","12","","","",LocalDateTime.now(),LocalDateTime.now(),"",""))));

        StepVerifier.create(enrolChildService.validateChildHeaders(requestHeaders))
                .expectError(BenefitNotFoundException.class).verify();
    }
    @Test
    public void validateChildHeadersTesterr1(){
         requestHeaders.setRoleList(null);
        Assert.assertThrows(AbhaUnAuthorizedException.class,()->enrolChildService.validateChildHeaders(requestHeaders));
    }
    @Test
    public void validateChildHeadersTesterr2(){
        requestHeaders.setXToken(null);
        Assert.assertThrows(AbhaUnAuthorizedException.class,()->enrolChildService.validateChildHeaders(requestHeaders));
    }
    @Test
    public void validateChildHeadersTest3(){
        Mockito.when(abhaDBAccountFClient.getAccountsEntityByDocumentCode(any())).thenReturn(Flux.just(accountDto));
        Mockito.when( redisService.getIntegratedPrograms()).thenReturn(List.of(new IntegratedProgramDto("1","","","benefitName","12","","","",LocalDateTime.now(),LocalDateTime.now(),"","")));
        Mockito.when(redisService.reloadAndGetIntegratedPrograms()).thenReturn(Mono.just(List.of(new IntegratedProgramDto("1","","","benefitName","12","","","",LocalDateTime.now(),LocalDateTime.now(),"",""))));
        requestHeaders.setBenefitName(null);
        StepVerifier.create(enrolChildService.validateChildHeaders(requestHeaders))
                .expectError(BenefitNotFoundException.class).verify();
    }
    @Test
    public void enrolTesterr(){
        Mockito.when(enrolByDemographicService.isValidAge(any())).thenReturn(false);
        Mockito.when(abhaDBAccountFClient.getAccountsEntityByDocumentCode(any())).thenReturn(Flux.just(accountDto));
        Mockito.when(accountService.getAccountByHealthIdNumber(any())).thenReturn(Mono.just(accountDto));
        Mockito.when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        StepVerifier.create(enrolChildService.enrol(enrolByAadhaarRequestDto, requestHeaders))
                .expectError().verify();
    }
    @Test
    public void enrolTestErr2(){
        accountDto.setMobile(null);
        authData.setChildDto(new ChildDto("name mid","12","12","2021","F","password","photo","true"));
        enrolByAadhaarRequestDto.setAuthData(authData);
        Mockito.when(enrolByDemographicService.isValidAge(any())).thenReturn(true);
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("1");
        Mockito.when(abhaDBAccountFClient.getAccountsEntityByDocumentCode(any())).thenReturn(Flux.empty());
        Mockito.when(accountService.getAccountByHealthIdNumber(any())).thenReturn(Mono.just(accountDto));
        Mockito.when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        Mockito.when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        Mockito.when(hidPhrAddressService.prepareNewHidPhrAddress(any())).thenReturn(hidPhrAddressDto);
        Mockito.when(hidBenefitDBFClient.saveHidBenefit(any())).thenReturn(Mono.just(new HidBenefitDto()));
        Mockito.when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(List.of(new AccountAuthMethodsDto())));
        Mockito.when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        Mockito.when(notificationService.sendABHACreationSMS(any(),any(),any())).thenReturn(Mono.just(new NotificationResponseDto()));
        StepVerifier.create(enrolChildService.enrol(enrolByAadhaarRequestDto, requestHeaders))
                .expectError().verify();
    }
    @Test
    public void enrolTestErr3(){
        ReflectionTestUtils.setField(enrolChildService,"childAbhaAccountLimit",-1);
        authData.setChildDto(new ChildDto("name mid","12","12","2021","F","password","photo","true"));
        enrolByAadhaarRequestDto.setAuthData(authData);
        Mockito.when(enrolByDemographicService.isValidAge(any())).thenReturn(true);
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("1");
        Mockito.when(abhaDBAccountFClient.getAccountsEntityByDocumentCode(any())).thenReturn(Flux.empty());
        Mockito.when(accountService.getAccountByHealthIdNumber(any())).thenReturn(Mono.just(accountDto));
        Mockito.when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        Mockito.when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        Mockito.when(hidPhrAddressService.prepareNewHidPhrAddress(any())).thenReturn(hidPhrAddressDto);
        Mockito.when(hidBenefitDBFClient.saveHidBenefit(any())).thenReturn(Mono.just(new HidBenefitDto()));
        Mockito.when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(List.of(new AccountAuthMethodsDto())));
        Mockito.when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        Mockito.when(notificationService.sendABHACreationSMS(any(),any(),any())).thenReturn(Mono.just(new NotificationResponseDto()));
        StepVerifier.create(enrolChildService.enrol(enrolByAadhaarRequestDto, requestHeaders))
                .expectError().verify();
    }

}
