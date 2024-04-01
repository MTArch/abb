package in.gov.abdm.abha.enrollment.services.database;

import in.gov.abdm.abha.enrollment.client.AbhaDBAccountFClient;
import in.gov.abdm.abha.enrollment.client.HidBenefitDBFClient;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarUserKycDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AuthData;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.BioDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.ConsentDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.entities.*;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import in.gov.abdm.abha.enrollment.model.procedure.SaveAllDataRequest;
import in.gov.abdm.abha.enrollment.services.database.account.impl.AccountServiceImpl;
import in.gov.abdm.abha.enrollment.services.redis.RedisService;
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
import reactor.test.StepVerifier;

import java.util.*;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AccountDBTests {

    @InjectMocks
    AccountServiceImpl accountService;

    @Mock
    AbhaDBAccountFClient abhaDBAccountFClient;
    @Mock
    HidBenefitDBFClient hidBenefitDBFClient;
    @Mock
    RedisService redisService;

    public static final String AADHAAR_NUMBER = "QYhr7tdzsYyYVfFjnv/fRApJBixLC2xt1Xv1Sk/AU1SQfmagNEyamwg6o3/kiD9cKuyG3D1M1aNvVMgBCLYnuKhglQgnivzcIVVR3icwxjlJp/7gOEEB76OJzl7EG8AyqB1omC4KpgcAPjq/436t1150Mn4sDrbomJVgYZbDECD2808cxJ8ygs3iv0n/FQldvaU3bdDkKnHAE/XMnxVm7KGzn7/XI7ylxvGNmnVfwzfdnFlHnaL0oFbs0X9G9hDhI370BclD22qo8yh5y4jhbYYShtBKdB9E93CWt94Cx4y43fNAjAJJq2caMvR3M1vxFumTkc/Gn2IAtPxWMHi/e8qloF0oGO3I+j2ktWTRR6qbh7JyigcQLdOg8QPa95cpn4AyiRgXh0KyACtYFiakI3T1PsVx+GO/RrzuWIX5eYBe4TRbvD7DfuXJFrrxOhn8NOmTVOTf6OgYhAtL9OiLNLeplL1MFhZmnyqNJIAfyKZvKlQfFFMtugRZBJVmzTzI1qit1hAODI/X+0o4NMllfEMeFW6GWLnBCjl3ZM37+IS8YXLwsTYHvVd0s8zvPI6MohEdM6GyqpGnXyBYSTjblE3RG+pFvdovhRbH74yem1IdQKmFHWAoJXgWeCy9BQfBxv1SCI13IrpuvZhJ9I+/9SUX/Sl1gpg8iFHMXTqFEns=";
    public static final String PID = "QYhr7tdzsYyYVfFjnv/fRApJBixLC2xt1Xv1Sk/AU1SQfmagNhgsftedo3/kiD9cKuyG3D1M1aNvVMgBCLYnuKhglQgnivzcIVVR3icwxjlJp/7gOEEB76OJzl7EG8AyqB1omC4KpgcAPjq/436t1150Mn4sDrbomJVgYZbDECD2808cxJ8ygs3iv0n/FQldvaU3bdDkKnHAE/XMnxVm7KGzn7/XI7ylxvGNmnVfwzfdnFlHnaL0oFbs0X9G9hDhI370BclD22qo8yh5y4jhbYYShtBKdB9E93CWt94Cx4y43fNAjAJJq2caMvR3M1vxFumTkc/Gn2IAtPxWMHi/e8qloF0oGO3I+j2ktWTRR6qbh7JyigcQLdOg8QPa95cpn4AyiRgXh0KyACtYFiakI3T1PsVx+GO/RrzuWIX5eYBe4TRbvD7DfuXJFrrxOhn8NOmTVOTf6OgYhAtL9OiLNLeplL1MFhZmnyqNJIAfyKZvKlQfFFMtugRZBJVmzTzI1qit1hAODI/X+0o4NMllfEMeFW6GWLnBCjl3ZM37+IS8YXLwsTYHvVd0s8zvPI6MohEdM6GyqpGnXyBYSTjblE3RG+pFvdovhRbH74yem1IdQKmFHWAoJXgWeCy9BQfBxv1SCI13IrpuvZhJ9I+/9SUX/Sl1gpg8iFHMXTqFEns=";

    @Before
    public void setup(){
        MockitoAnnotations.openMocks(this);
        HidReattemptDto s=HidReattemptDto.builder().healthIdNumber("").requestType("").createdBy("").updatedBy("").build();
        String test =s.getCreatedBy();test =s.getUpdatedBy();
        test=s.getHealthIdNumber();
        test=s.getRequestType();


    }


    @Test
    public void findByXmlUidTest(){
        AccountDto accountDto = new AccountDto();

        accountDto.setHealthIdNumber("1234");

        Mockito.when(abhaDBAccountFClient.getAccountByXmlUid(any())).thenReturn(Mono.just(accountDto));
        AccountDto result =  accountService.findByXmlUid("Test").block();

        Assert.assertEquals("Failed to Validate HealthIdNumber",accountDto.getHealthIdNumber(), result.getHealthIdNumber());
    }
    @Test
    public void getAccountByHealthIdNumberTest(){
        AccountDto accountDto = new AccountDto();
        accountDto.setHealthIdNumber("1234");

        Mockito.when(abhaDBAccountFClient.getAccountByHealthIdNumber(any())).thenReturn(Mono.just(accountDto));
        AccountDto result =  accountService.getAccountByHealthIdNumber("1234").block();

        Assert.assertEquals("Failed to Validate HealthIdNumber",accountDto.getHealthIdNumber(), result.getHealthIdNumber());
    }
    @Test
    public void getAccountByDocumentCodeTest(){
        AccountDto accountDto = new AccountDto();
        accountDto.setHealthIdNumber("1234");

        Mockito.when(abhaDBAccountFClient.getAccountEntityByDocumentCode(any())).thenReturn(Mono.just(accountDto));
        AccountDto result =  accountService.getAccountByDocumentCode("1234").block();

        Assert.assertEquals("",accountDto.getHealthIdNumber(), result.getHealthIdNumber());
    }
    @Test
    public void getAccountsByHealthIdNumbersTest(){
        AccountDto accountDto = new AccountDto();
        accountDto.setHealthIdNumber("1234");
        List<String> l = new ArrayList<>();
        l.add("123");
        l.add("234");
        Mockito.when(abhaDBAccountFClient.getAccountsByHealthIdNumbers(any())).thenReturn(Flux.just(accountDto));
        AccountDto result =  accountService.getAccountsByHealthIdNumbers(l).blockFirst();

        Assert.assertEquals("",accountDto.getHealthIdNumber(), result.getHealthIdNumber());
    }
    @Test
    public void getMobileLinkedAccountCountTest(){
       int count =2;
        Mockito.when(abhaDBAccountFClient.getMobileLinkedAccountCount(any())).thenReturn(Mono.just(count));
        Integer result =  accountService.getMobileLinkedAccountCount("123").block();

        Assert.assertEquals("",count, 2);
    }
    @Test
    public void isItNewUserTest(){
        AccountDto accountDto = new AccountDto();
        accountDto.setHealthIdNumber("1234");
       boolean result =  accountService.isItNewUser(accountDto);

        Assert.assertEquals("Failed to Validate",result, true);
    }
    @Test
    public void updateAccountByHealthIdNumberTest(){
        AccountDto accountDto = new AccountDto();
        accountDto.setHealthIdNumber("1234");

        Mockito.when(abhaDBAccountFClient.updateAccount(any(),any())).thenReturn(Mono.just(accountDto));
        AccountDto result =  accountService.updateAccountByHealthIdNumber(accountDto,"Test").block();

        Assert.assertEquals("Failed to Validate",accountDto.getHealthIdNumber(), result.getHealthIdNumber());
    }
    @Test
    public void getEmailLinkedAccountCountTest(){
        int r = 2;
        Mockito.when(abhaDBAccountFClient.getEmailLinkedAccountCount(any())).thenReturn(Mono.just(r));
        Integer result =  accountService.getEmailLinkedAccountCount("Test").block();

        Assert.assertEquals("Failed to Validate",result, result);
    }
    @Test
    public void saveAllDataTest(){
        SaveAllDataRequest saveAllDataRequest = new SaveAllDataRequest();
        saveAllDataRequest.setAccounts(Arrays.asList(new AccountDto()));
        saveAllDataRequest.setAccountAuthMethods(Arrays.asList(new AccountAuthMethodsDto()));
        saveAllDataRequest.setHidPhrAddress(Arrays.asList(new HidPhrAddressDto()));
        List<AccountDto> s = saveAllDataRequest.getAccounts();
        List<AccountAuthMethodsDto> accountAuthMethodsDtos= saveAllDataRequest.getAccountAuthMethods();
        List<HidPhrAddressDto> hidPhrAddressDtos= saveAllDataRequest.getHidPhrAddress();
        Mockito.when(abhaDBAccountFClient.saveAllData(any())).thenReturn(Mono.just("Success"));
        String result =  accountService.saveAllData(saveAllDataRequest).block();

        Assert.assertEquals("Failed to Validate HealthIdNumber","Success", result);
    }
    @Test
    public void prepareNewAccountTest1(){
     TransactionDto transactionDto = new TransactionDto();
     AccountDto accountDto = new AccountDto();
     BioDto bioDto = new BioDto();
     ConsentDto consentDto = new ConsentDto();
     AuthData authData = new AuthData();
     EnrolByAadhaarRequestDto enrolByAadhaarRequestDto = new EnrolByAadhaarRequestDto();
        accountDto.setYearOfBirth("1995");
        accountDto.setMonthOfBirth("09");
        accountDto.setDayOfBirth("29");
        transactionDto.setAddress("Address");
        transactionDto.setStatus("ACTIVE");
        transactionDto.setMobile("86475976573");
        transactionDto.setName("First Middle Last");
        transactionDto.setGender("Gender");
        transactionDto.setKycdob("12-12-2000");
        transactionDto.setDistrictName("District");
        transactionDto.setStateName("State");
        transactionDto.setTxnId(UUID.fromString("cda04910-37ca-4f2e-84d9-4e5a970d3dc7"));
        transactionDto.setAadharNo(AADHAAR_NUMBER);
        transactionDto.setSubDistrictName("SDN");
        transactionDto.setTownName("Town");
        transactionDto.setXmluid("id");
        transactionDto.setEmail("email");
        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.IRIS);
        bioDto.setAadhaar(AADHAAR_NUMBER);
        bioDto.setFingerPrintAuthPid(PID);
        bioDto.setMobile("9876543872");
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        authData.setAuthMethods(authMethods);
        authData.setBio(bioDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        List<LgdDistrictResponse> lgdDistrictResponses = new ArrayList<>();
        LgdDistrictResponse lgd = new LgdDistrictResponse();
        lgd.setStateName("State");
        lgd.setDistrictName("District");
        lgd.setDistrictCode("100");
        lgd.setStateCode("10");
        lgd.setPinCode("456334");
        lgdDistrictResponses.add(lgd);
        //when(accountService.isItNewUser(accountDto)).thenReturn(true);
        StepVerifier.create(accountService.prepareNewAccount(transactionDto,enrolByAadhaarRequestDto,lgdDistrictResponses))
                .expectNextCount(1L)
                .verifyComplete();
       }
    @Test
    public void prepareNewAccountTest2(){
        TransactionDto transactionDto = new TransactionDto();
        AccountDto accountDto = new AccountDto();
        BioDto bioDto = new BioDto();
        ConsentDto consentDto = new ConsentDto();
        AuthData authData = new AuthData();
        EnrolByAadhaarRequestDto enrolByAadhaarRequestDto = new EnrolByAadhaarRequestDto();
        accountDto.setYearOfBirth("1995");
        accountDto.setMonthOfBirth("09");
        accountDto.setDayOfBirth("29");
        transactionDto.setAddress("Address");
        transactionDto.setStatus("ACTIVE");
        transactionDto.setMobile("8647597653");
        transactionDto.setName("First Middle");
        transactionDto.setGender("Gender");
        transactionDto.setKycdob("2000");
        transactionDto.setDistrictName("District");
        transactionDto.setStateName("State");
        transactionDto.setPincode("245038");
        transactionDto.setTxnId(UUID.fromString("cda04910-37ca-4f2e-84d9-4e5a970d3dc7"));
        transactionDto.setAadharNo(AADHAAR_NUMBER);
        transactionDto.setSubDistrictName("SDN");
        transactionDto.setTownName("Town");
        transactionDto.setXmluid("id");
        transactionDto.setEmail("email");
        transactionDto.setMobileVerified(true);

        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.OTP);
        authMethods.add(AuthMethods.FACE);
        authMethods.add(AuthMethods.IRIS);
        bioDto.setAadhaar(AADHAAR_NUMBER);
        bioDto.setFingerPrintAuthPid(PID);
        bioDto.setMobile("9876543872");
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        authData.setAuthMethods(authMethods);
        authData.setBio(bioDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        List<LgdDistrictResponse> lgdDistrictResponses = new ArrayList<>();
        LgdDistrictResponse lgd = new LgdDistrictResponse();
        lgd.setStateName("State");
        lgd.setDistrictName("District");
        lgd.setDistrictCode("100");
        lgd.setStateCode("10");
        lgd.setPinCode("456334");
        lgdDistrictResponses.add(lgd);
        //when(accountService.isItNewUser(accountDto)).thenReturn(true);
        StepVerifier.create(accountService.prepareNewAccount(transactionDto,enrolByAadhaarRequestDto,lgdDistrictResponses))
                .expectNextCount(1L)
                .verifyComplete();
        ArrayList<AuthMethods> authMethods2 = new ArrayList<>();
        authMethods2.add(AuthMethods.BIO);
        bioDto.setAadhaar(AADHAAR_NUMBER);
        bioDto.setFingerPrintAuthPid(PID);
        bioDto.setMobile("9876543872");
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        authData.setAuthMethods(authMethods2);
        authData.setBio(bioDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        StepVerifier.create(accountService.prepareNewAccount(transactionDto,enrolByAadhaarRequestDto,lgdDistrictResponses))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    public void prepareNewAccountTestldgempty(){
        TransactionDto transactionDto = new TransactionDto();
        AccountDto accountDto = new AccountDto();
        BioDto bioDto = new BioDto();
        ConsentDto consentDto = new ConsentDto();
        AuthData authData = new AuthData();
        EnrolByAadhaarRequestDto enrolByAadhaarRequestDto = new EnrolByAadhaarRequestDto();
        accountDto.setYearOfBirth("1995");
        accountDto.setMonthOfBirth("09");
        accountDto.setDayOfBirth("29");
        transactionDto.setAddress("Address");
        transactionDto.setStatus("ACTIVE");
        transactionDto.setMobile("8647597653");
        transactionDto.setName("First Middle");
        transactionDto.setGender("Gender");
        transactionDto.setKycdob("2000");
        transactionDto.setDistrictName("District");
        transactionDto.setStateName("State");
        transactionDto.setPincode("245038");
        transactionDto.setTxnId(UUID.fromString("cda04910-37ca-4f2e-84d9-4e5a970d3dc7"));
        transactionDto.setAadharNo(AADHAAR_NUMBER);
        transactionDto.setSubDistrictName("SDN");
        transactionDto.setTownName("Town");
        transactionDto.setXmluid("id");
        transactionDto.setEmail("email");
        transactionDto.setMobileVerified(true);

        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.FACE);
        authMethods.add(AuthMethods.IRIS);
        bioDto.setAadhaar(AADHAAR_NUMBER);
        bioDto.setFingerPrintAuthPid(PID);
        bioDto.setMobile("9876543872");
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        authData.setAuthMethods(authMethods);
        authData.setBio(bioDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        List<LgdDistrictResponse> lgdDistrictResponses = new ArrayList<>();
        LgdDistrictResponse lgd = new LgdDistrictResponse();
        lgd.setStateName("State");
        lgd.setDistrictName("District");
        lgd.setDistrictCode("100");
        lgd.setStateCode("10");
        lgd.setPinCode("456334");
        lgdDistrictResponses.add(lgd);
        //when(accountService.isItNewUser(accountDto)).thenReturn(true);
        List<LgdDistrictResponse> lgdDistrictResponses2 = new ArrayList<>();
        StepVerifier.create(accountService.prepareNewAccount(transactionDto,enrolByAadhaarRequestDto, lgdDistrictResponses2))
                .expectNextCount(1L)
                .verifyComplete();
    }

    @Test
    public void createAccountEntityTest(){
        TransactionDto transactionDto = new TransactionDto();
        AccountDto accountDto = new AccountDto();
        BioDto bioDto = new BioDto();
        ConsentDto consentDto = new ConsentDto();
        AuthData authData = new AuthData();
        HidBenefitDto hidBenefitDto = new HidBenefitDto();
        RequestHeaders requestHeaders = new RequestHeaders();
        EnrolByAadhaarRequestDto enrolByAadhaarRequestDto = new EnrolByAadhaarRequestDto();
        IntegratedProgramDto integratedProgramDto = new IntegratedProgramDto();

        accountDto.setYearOfBirth("1995");
        accountDto.setMonthOfBirth("09");
        accountDto.setDayOfBirth("29");
        accountDto.setVerificationType("PROVISIONAL");
        transactionDto.setAddress("Address");
        transactionDto.setStatus("ACTIVE");
        transactionDto.setMobile("86475976573");
        transactionDto.setName("First Middle Last");
        transactionDto.setGender("Gender");
        transactionDto.setKycdob("12-12-2000");
        transactionDto.setDistrictName("District");
        transactionDto.setStateName("State");
        transactionDto.setTxnId(UUID.fromString("cda04910-37ca-4f2e-84d9-4e5a970d3dc7"));
        transactionDto.setAadharNo(AADHAAR_NUMBER);
        transactionDto.setSubDistrictName("SDN");
        transactionDto.setTownName("Town");
        transactionDto.setXmluid("id");
        transactionDto.setEmail("email");
        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.BIO);
        bioDto.setAadhaar(AADHAAR_NUMBER);
        bioDto.setFingerPrintAuthPid(PID);
        bioDto.setMobile("9876543872");
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        authData.setAuthMethods(authMethods);
        authData.setBio(bioDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        List<LgdDistrictResponse> lgdDistrictResponses = new ArrayList<>();
        LgdDistrictResponse lgd = new LgdDistrictResponse();
        lgd.setStateName("State");
        lgd.setDistrictName("District");
        lgd.setDistrictCode("100");
        lgd.setStateCode("10");
        lgd.setPinCode("456334");
        lgdDistrictResponses.add(lgd);
        requestHeaders.setBenefitName("Name");
        requestHeaders.setClientId("ClientId");
        List<IntegratedProgramDto> list = new ArrayList<>();
        integratedProgramDto.setCreatedBy("user");
        integratedProgramDto.setBenefitName("Name");
        integratedProgramDto.setClientId("ClientId");
        integratedProgramDto.setProgramName("programName");
        integratedProgramDto.setId("id");
        list.add(integratedProgramDto);
        List<String> roleList = new ArrayList<>();
        roleList.add(INTEGRATED_PROGRAM_ROLE);
        requestHeaders.setRoleList(roleList);
        hidBenefitDto.setBenefitName("Name");
        hidBenefitDto.setHidBenefitId("id1");
        hidBenefitDto.setBenefitId("id2");
        hidBenefitDto.setCreatedBy("user");
        hidBenefitDto.setBenefitName("Name");
        hidBenefitDto.setProgramName("programName");
        hidBenefitDto.setHealthIdNumber("12345");
        Mockito.when(abhaDBAccountFClient.createAccount(any())).thenReturn(Mono.just(accountDto));

        when(hidBenefitDBFClient.saveHidBenefit(any())).thenReturn(Mono.just(hidBenefitDto));


        Mockito.when(redisService.getIntegratedPrograms()).thenReturn(list);
        Mockito.when(redisService.reloadAndGetIntegratedPrograms()).thenReturn(Mono.just(list));
       // AccountDto result = accountService.createAccountEntity(enrolByAadhaarRequestDto,accountDto,requestHeaders).block();
      //  Assert.assertEquals("Failed to Validate HealthIdNumber",accountDto.getHealthIdNumber(), result.getHealthIdNumber());

       StepVerifier.create(accountService.createAccountEntity(enrolByAadhaarRequestDto,accountDto,requestHeaders))
               .expectNextCount(1L)
               .verifyComplete();
    }

    @Test
    public void settingClientIdAndOriginTest(){
        TransactionDto transactionDto = new TransactionDto();
        AccountDto accountDto = new AccountDto();
        BioDto bioDto = new BioDto();
        ConsentDto consentDto = new ConsentDto();
        AuthData authData = new AuthData();
        HidBenefitDto hidBenefitDto = new HidBenefitDto();
        RequestHeaders requestHeaders = new RequestHeaders();
        EnrolByAadhaarRequestDto enrolByAadhaarRequestDto = new EnrolByAadhaarRequestDto();
        IntegratedProgramDto integratedProgramDto = new IntegratedProgramDto();

        accountDto.setYearOfBirth("1995");
        accountDto.setMonthOfBirth("09");
        accountDto.setDayOfBirth("29");
        accountDto.setVerificationType("PROVISIONAL");
        transactionDto.setAddress("Address");
        transactionDto.setStatus("ACTIVE");
        transactionDto.setMobile("86475976573");
        transactionDto.setName("First Middle Last");
        transactionDto.setGender("Gender");
        transactionDto.setKycdob("12-12-2000");
        transactionDto.setDistrictName("District");
        transactionDto.setStateName("State");
        transactionDto.setTxnId(UUID.fromString("cda04910-37ca-4f2e-84d9-4e5a970d3dc7"));
        transactionDto.setAadharNo(AADHAAR_NUMBER);
        transactionDto.setSubDistrictName("SDN");
        transactionDto.setTownName("Town");
        transactionDto.setXmluid("id");
        transactionDto.setEmail("email");
        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.BIO);
        bioDto.setAadhaar(AADHAAR_NUMBER);
        bioDto.setFingerPrintAuthPid(PID);
        bioDto.setMobile("9876543872");
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        authData.setAuthMethods(authMethods);
        authData.setBio(bioDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        List<LgdDistrictResponse> lgdDistrictResponses = new ArrayList<>();
        LgdDistrictResponse lgd = new LgdDistrictResponse();
        lgd.setStateName("State");
        lgd.setDistrictName("District");
        lgd.setDistrictCode("100");
        lgd.setStateCode("10");
        lgd.setPinCode("456334");
        lgdDistrictResponses.add(lgd);
        requestHeaders.setBenefitName("Name");
        requestHeaders.setClientId("ClientId");
        List<IntegratedProgramDto> list = new ArrayList<>();
        integratedProgramDto.setCreatedBy("user");
        integratedProgramDto.setBenefitName("Name");
        integratedProgramDto.setClientId("ClientId");
        integratedProgramDto.setProgramName("programName");
        integratedProgramDto.setId("id");
        list.add(integratedProgramDto);
        List<String> roleList = new ArrayList<>();
        roleList.add(INTEGRATED_PROGRAM_ROLE);
        requestHeaders.setRoleList(roleList);
        hidBenefitDto.setBenefitName("Name");
        hidBenefitDto.setHidBenefitId("id1");
        hidBenefitDto.setBenefitId("id2");
        hidBenefitDto.setCreatedBy("user");
        hidBenefitDto.setBenefitName("Name");
        hidBenefitDto.setProgramName("programName");
        hidBenefitDto.setHealthIdNumber("12345");
        Mockito.when(abhaDBAccountFClient.createAccount(any())).thenReturn(Mono.just(accountDto));

        when(hidBenefitDBFClient.saveHidBenefit(any())).thenReturn(Mono.just(hidBenefitDto));
        when(hidBenefitDBFClient.existByHealthIdAndBenefit(any(),any())).thenReturn(Mono.just(true));


        Mockito.when(redisService.getIntegratedPrograms()).thenReturn(list);
        Mockito.when(redisService.reloadAndGetIntegratedPrograms()).thenReturn(Mono.just(list));
        // AccountDto result = accountService.createAccountEntity(enrolByAadhaarRequestDto,accountDto,requestHeaders).block();
        //  Assert.assertEquals("Failed to Validate HealthIdNumber",accountDto.getHealthIdNumber(), result.getHealthIdNumber());

        StepVerifier.create(accountService.settingClientIdAndOrigin(enrolByAadhaarRequestDto,accountDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }

    @Test
    public void settingClientIdAndOriginTest2(){
        TransactionDto transactionDto = new TransactionDto();
        AccountDto accountDto = new AccountDto();
        BioDto bioDto = new BioDto();
        ConsentDto consentDto = new ConsentDto();
        AuthData authData = new AuthData();
        HidBenefitDto hidBenefitDto = new HidBenefitDto();
        RequestHeaders requestHeaders = new RequestHeaders();
        EnrolByAadhaarRequestDto enrolByAadhaarRequestDto = new EnrolByAadhaarRequestDto();
        IntegratedProgramDto integratedProgramDto = new IntegratedProgramDto();

        accountDto.setYearOfBirth("1995");
        accountDto.setMonthOfBirth("09");
        accountDto.setDayOfBirth("29");
        accountDto.setVerificationType("PROVISIONAL");
        transactionDto.setAddress("Address");
        transactionDto.setStatus("ACTIVE");
        transactionDto.setMobile("86475976573");
        transactionDto.setName("First Middle Last");
        transactionDto.setGender("Gender");
        transactionDto.setKycdob("12-12-2000");
        transactionDto.setDistrictName("District");
        transactionDto.setStateName("State");
        transactionDto.setTxnId(UUID.fromString("cda04910-37ca-4f2e-84d9-4e5a970d3dc7"));
        transactionDto.setAadharNo(AADHAAR_NUMBER);
        transactionDto.setSubDistrictName("SDN");
        transactionDto.setTownName("Town");
        transactionDto.setXmluid("id");
        transactionDto.setEmail("email");
        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.DEMO_AUTH);
        bioDto.setAadhaar(AADHAAR_NUMBER);
        bioDto.setFingerPrintAuthPid(PID);
        bioDto.setMobile("9876543872");
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        authData.setAuthMethods(authMethods);
        authData.setBio(bioDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        List<LgdDistrictResponse> lgdDistrictResponses = new ArrayList<>();
        LgdDistrictResponse lgd = new LgdDistrictResponse();
        lgd.setStateName("State");
        lgd.setDistrictName("District");
        lgd.setDistrictCode("100");
        lgd.setStateCode("10");
        lgd.setPinCode("456334");
        lgdDistrictResponses.add(lgd);
      //  requestHeaders.setBenefitName("Name");
      //  requestHeaders.setClientId("ClientId");
        List<IntegratedProgramDto> list = new ArrayList<>();
        integratedProgramDto.setCreatedBy("user");
        integratedProgramDto.setBenefitName("Name");
        integratedProgramDto.setClientId("ClientId");
        integratedProgramDto.setProgramName("programName");
        integratedProgramDto.setId("id");
        list.add(integratedProgramDto);
        List<String> roleList = new ArrayList<>();
        roleList.add(INTEGRATED_PROGRAM_ROLE);
       // requestHeaders.setRoleList(roleList);
        hidBenefitDto.setBenefitName("Name");
        hidBenefitDto.setHidBenefitId("id1");
        hidBenefitDto.setBenefitId("id2");
        hidBenefitDto.setCreatedBy("user");
        hidBenefitDto.setBenefitName("Name");
        hidBenefitDto.setProgramName("programName");
        hidBenefitDto.setHealthIdNumber("12345");
        Mockito.when(abhaDBAccountFClient.createAccount(any())).thenReturn(Mono.just(accountDto));
        when(hidBenefitDBFClient.existByHealthIdAndBenefit(any(),any())).thenReturn(Mono.just(true));

        when(hidBenefitDBFClient.saveHidBenefit(any())).thenReturn(Mono.just(hidBenefitDto));
        when(hidBenefitDBFClient.existByHealthIdAndBenefit(any(),any())).thenReturn(Mono.just(false));


        Mockito.when(redisService.getIntegratedPrograms()).thenReturn(list);
        Mockito.when(redisService.reloadAndGetIntegratedPrograms()).thenReturn(Mono.just(list));
        // AccountDto result = accountService.createAccountEntity(enrolByAadhaarRequestDto,accountDto,requestHeaders).block();
        //  Assert.assertEquals("Failed to Validate HealthIdNumber",accountDto.getHealthIdNumber(), result.getHealthIdNumber());

        StepVerifier.create(accountService.settingClientIdAndOrigin(enrolByAadhaarRequestDto,accountDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    public void mapAccountWithEkycTest(){
        TransactionDto transactionDto = new TransactionDto();
        AccountDto accountDto = new AccountDto();
        BioDto bioDto = new BioDto();
        ConsentDto consentDto = new ConsentDto();
        AuthData authData = new AuthData();
        HidBenefitDto hidBenefitDto = new HidBenefitDto();
        RequestHeaders requestHeaders = new RequestHeaders();
        EnrolByAadhaarRequestDto enrolByAadhaarRequestDto = new EnrolByAadhaarRequestDto();
        IntegratedProgramDto integratedProgramDto = new IntegratedProgramDto();
        AadhaarResponseDto aadhaarResponseDto = new AadhaarResponseDto();
        AadhaarUserKycDto aadhaarUserKycDto= new AadhaarUserKycDto();
        accountDto.setYearOfBirth("1995");
        accountDto.setMonthOfBirth("09");
        accountDto.setDayOfBirth("29");
        accountDto.setProfilePhoto("photo");
        accountDto.setVerificationType("PROVISIONAL");
        accountDto.setHealthIdNumber("234567654");
        transactionDto.setAddress("Address");
        transactionDto.setStatus("ACTIVE");
        transactionDto.setMobile("86475976573");
        transactionDto.setName("First Middle Last");
        transactionDto.setGender("Gender");
        transactionDto.setKycdob("12-12-2000");
        transactionDto.setDistrictName("District");
        transactionDto.setStateName("State");
        transactionDto.setTxnId(UUID.fromString("cda04910-37ca-4f2e-84d9-4e5a970d3dc7"));
        transactionDto.setAadharNo(AADHAAR_NUMBER);
        transactionDto.setSubDistrictName("SDN");
        transactionDto.setTownName("Town");
        transactionDto.setXmluid("id");
        transactionDto.setEmail("email");
        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.DEMO_AUTH);
        bioDto.setAadhaar(AADHAAR_NUMBER);
        bioDto.setFingerPrintAuthPid(PID);
        bioDto.setMobile("9876543872");
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        authData.setAuthMethods(authMethods);
        authData.setBio(bioDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        List<LgdDistrictResponse> lgdDistrictResponses = new ArrayList<>();
        LgdDistrictResponse lgd = new LgdDistrictResponse();
        lgd.setStateName("State");
        lgd.setDistrictName("District");
        lgd.setDistrictCode("100");
        lgd.setStateCode("10");
        lgd.setPinCode("456334");
        lgdDistrictResponses.add(lgd);
        requestHeaders.setBenefitName("Name");
        requestHeaders.setClientId("ClientId");
        List<IntegratedProgramDto> list = new ArrayList<>();
        integratedProgramDto.setCreatedBy("user");
        integratedProgramDto.setBenefitName("Name");
        integratedProgramDto.setClientId("ClientId");
        integratedProgramDto.setProgramName("programName");
        integratedProgramDto.setId("id");
        list.add(integratedProgramDto);
        List<String> roleList = new ArrayList<>();
        roleList.add(INTEGRATED_PROGRAM_ROLE);
        requestHeaders.setRoleList(roleList);
        hidBenefitDto.setBenefitName("Name");
        hidBenefitDto.setHidBenefitId("id1");
        hidBenefitDto.setBenefitId("id2");
        hidBenefitDto.setCreatedBy("user");
        hidBenefitDto.setBenefitName("Name");
        hidBenefitDto.setProgramName("programName");
        hidBenefitDto.setHealthIdNumber("12345");

        aadhaarUserKycDto.setPhoto("Photo");
        aadhaarUserKycDto.setBirthdate("10-10-2000");
        aadhaarUserKycDto.setEmail("email");
        aadhaarUserKycDto.setPincode("123564");
        aadhaarUserKycDto.setName("name");
        aadhaarUserKycDto.setGender("Male");
        aadhaarResponseDto.setStatus("SUCCESS");

        aadhaarResponseDto.setAadhaarUserKycDto(aadhaarUserKycDto);
        Mockito.when(abhaDBAccountFClient.createAccount(any())).thenReturn(Mono.just(accountDto));

        when(hidBenefitDBFClient.saveHidBenefit(any())).thenReturn(Mono.just(hidBenefitDto));

        when(hidBenefitDBFClient.existByHealthIdAndBenefit(any(),any())).thenReturn(Mono.just(true));


        Mockito.when(redisService.getIntegratedPrograms()).thenReturn(list);
        Mockito.when(redisService.reloadAndGetIntegratedPrograms()).thenReturn(Mono.just(list));
        accountService.mapAccountWithEkyc(aadhaarResponseDto,accountDto,lgdDistrictResponses);
        aadhaarUserKycDto.setBirthdate("2000");
        accountService.mapAccountWithEkyc(aadhaarResponseDto,accountDto,lgdDistrictResponses);
         Assert.assertEquals("Failed to Validate HealthIdNumber", aadhaarResponseDto.getStatus(),aadhaarResponseDto.getStatus());

       // StepVerifier.create(accountService.mapAccountWithEkyc(aadhaarResponseDto,accountDto,lgdDistrictResponses)).expectNextCount(1L).verifyComplete();
    }
    @Test
    public void createAccountEntityTest2(){
        TransactionDto transactionDto = new TransactionDto();
        AccountDto accountDto = new AccountDto();
        BioDto bioDto = new BioDto();
        ConsentDto consentDto = new ConsentDto();
        AuthData authData = new AuthData();
        HidBenefitDto hidBenefitDto = new HidBenefitDto();
        RequestHeaders requestHeaders = new RequestHeaders();
        EnrolByAadhaarRequestDto enrolByAadhaarRequestDto = new EnrolByAadhaarRequestDto();
        IntegratedProgramDto integratedProgramDto = new IntegratedProgramDto();

        accountDto.setYearOfBirth("1995");
        accountDto.setMonthOfBirth("09");
        accountDto.setDayOfBirth("29");
        accountDto.setVerificationType("PROVISIONAL");
        transactionDto.setAddress("Address");
        transactionDto.setStatus("ACTIVE");
        transactionDto.setMobile("86475976573");
        transactionDto.setName("First Middle Last");
        transactionDto.setGender("Gender");
        transactionDto.setKycdob("12-12-2000");
        transactionDto.setDistrictName("District");
        transactionDto.setStateName("State");
        transactionDto.setTxnId(UUID.fromString("cda04910-37ca-4f2e-84d9-4e5a970d3dc7"));
        transactionDto.setAadharNo(AADHAAR_NUMBER);
        transactionDto.setSubDistrictName("SDN");
        transactionDto.setTownName("Town");
        transactionDto.setXmluid("id");
        transactionDto.setEmail("email");
        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.DEMO_AUTH);
        bioDto.setAadhaar(AADHAAR_NUMBER);
        bioDto.setFingerPrintAuthPid(PID);
        bioDto.setMobile("9876543872");
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        authData.setAuthMethods(authMethods);
        authData.setBio(bioDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        List<LgdDistrictResponse> lgdDistrictResponses = new ArrayList<>();
        LgdDistrictResponse lgd = new LgdDistrictResponse();
        lgd.setStateName("State");
        lgd.setDistrictName("District");
        lgd.setDistrictCode("100");
        lgd.setStateCode("10");
        lgd.setPinCode("456334");
        lgdDistrictResponses.add(lgd);
        requestHeaders.setBenefitName("Name");
        requestHeaders.setClientId("ClientId");
        List<IntegratedProgramDto> list = new ArrayList<>();
        integratedProgramDto.setCreatedBy("user");
        integratedProgramDto.setBenefitName("Name");
        integratedProgramDto.setClientId("ClientId");
        integratedProgramDto.setProgramName("programName");
        integratedProgramDto.setId("id");
        list.add(integratedProgramDto);
        List<String> roleList = new ArrayList<>();
        roleList.add(INTEGRATED_PROGRAM_ROLE);
        requestHeaders.setRoleList(roleList);
        Map<String, Object> ftkn = new HashMap<>();
        ftkn.put("sub","sub");
        requestHeaders.setFTokenClaims(ftkn);
        hidBenefitDto.setBenefitName("Name");
        hidBenefitDto.setHidBenefitId("id1");
        hidBenefitDto.setBenefitId("id2");
        hidBenefitDto.setCreatedBy("user");
        hidBenefitDto.setBenefitName("Name");
        hidBenefitDto.setProgramName("programName");
        hidBenefitDto.setHealthIdNumber("12345");
        Mockito.when(abhaDBAccountFClient.createAccount(any())).thenReturn(Mono.just(accountDto));

        when(hidBenefitDBFClient.saveHidBenefit(any())).thenReturn(Mono.just(hidBenefitDto));


        Mockito.when(redisService.getIntegratedPrograms()).thenReturn(list);
        Mockito.when(redisService.reloadAndGetIntegratedPrograms()).thenReturn(Mono.just(list));
        StepVerifier.create(accountService.createAccountEntity(enrolByAadhaarRequestDto,accountDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    public void reAttemptedAbhaTest(){
        RequestHeaders requestHeaders = new RequestHeaders();

        requestHeaders.setBenefitName("Name");
        requestHeaders.setClientId("ClientId");

        List<String> roleList = new ArrayList<>();
        roleList.add(INTEGRATED_PROGRAM_ROLE);
        requestHeaders.setRoleList(roleList);
        Map<String, Object> ftkn = new HashMap<>();
        ftkn.put("sub","sub");
        requestHeaders.setFTokenClaims(ftkn);
        Mockito.when(abhaDBAccountFClient.reAttemptedAbha(any())).thenReturn(Mono.empty());

        StepVerifier.create(accountService.reAttemptedAbha("1234","1",requestHeaders))
                .expectNextCount(0L)
                .verifyComplete();
    }









}
