package in.gov.abdm.abha.enrollment.services.enrol;

import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarExceptions;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarAuthOtpDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarUserKycDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicResponse;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.*;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.entities.*;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import in.gov.abdm.abha.enrollment.model.redis.otp.ReceiverOtpTracker;
import in.gov.abdm.abha.enrollment.model.redis.otp.RedisOtp;
import in.gov.abdm.abha.enrollment.services.aadhaar.AadhaarAppService;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.account_auth_methods.AccountAuthMethodService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.iris.EnrolByIrisService;
import in.gov.abdm.abha.enrollment.services.redis.RedisService;
import in.gov.abdm.abha.enrollment.utilities.LgdUtility;
import in.gov.abdm.abha.enrollment.utilities.MapperUtils;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaAddressGenerator;
import in.gov.abdm.abha.enrollment.utilities.jwt.JWTUtil;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class EnrolByIrisServiceTests {
    @InjectMocks
    EnrolByIrisService enrolByIrisService;
    @Mock
    AccountService accountService;
    @Mock
    HidPhrAddressService hidPhrAddressService;
    @Mock
    TransactionService transactionService;
    @Mock
    private AccountAuthMethodService accountAuthMethodService;
    @Mock
    RedisService redisService;
    @Mock
    AbhaAddressGenerator abhaAddressGenerator;
    @Mock
    AadhaarAppService aadhaarAppService;
    @Mock
    JWTUtil jwtUtil;
    @Mock
    LgdUtility lgdUtility;
    @Mock
    RSAUtil rsaUtil;
    @Mock
    private RedisOtp redisOtp;
    private EnrolByAadhaarRequestDto enrolByAadhaarRequestDto;
    private RequestHeaders requestHeaders;
    private IrisDto irisDto;

    private ConsentDto consentDto;
    private AuthData authData;
    private AadhaarResponseDto aadhaarResponseDto;
    private AadhaarUserKycDto aadhaarUserKycDto;
    private AadhaarAuthOtpDto aadhaarAuthOtpDto;
    private TransactionDto transactionDto;
    private AccountDto accountDto;
    private HidPhrAddressDto hidPhrAddressDto;
    private ArrayList<AuthMethods> authMethods;
    private LgdDistrictResponse lgdDistrictResponse;
    private VerifyDemographicResponse verifyDemographicResponse;
    private AccountAuthMethodsDto accountAuthMethodsDto;
    private ReceiverOtpTracker receiverOtpTracker;

    public static final String AADHAAR_NUMBER = "QYhr7tdzsYyYVfFjnv/fRApJBixLC2xt1Xv1Sk/AU1SQfmagNEyamwg6o3/kiD9cKuyG3D1M1aNvVMgBCLYnuKhglQgnivzcIVVR3icwxjlJp/7gOEEB76OJzl7EG8AyqB1omC4KpgcAPjq/436t1150Mn4sDrbomJVgYZbDECD2808cxJ8ygs3iv0n/FQldvaU3bdDkKnHAE/XMnxVm7KGzn7/XI7ylxvGNmnVfwzfdnFlHnaL0oFbs0X9G9hDhI370BclD22qo8yh5y4jhbYYShtBKdB9E93CWt94Cx4y43fNAjAJJq2caMvR3M1vxFumTkc/Gn2IAtPxWMHi/e8qloF0oGO3I+j2ktWTRR6qbh7JyigcQLdOg8QPa95cpn4AyiRgXh0KyACtYFiakI3T1PsVx+GO/RrzuWIX5eYBe4TRbvD7DfuXJFrrxOhn8NOmTVOTf6OgYhAtL9OiLNLeplL1MFhZmnyqNJIAfyKZvKlQfFFMtugRZBJVmzTzI1qit1hAODI/X+0o4NMllfEMeFW6GWLnBCjl3ZM37+IS8YXLwsTYHvVd0s8zvPI6MohEdM6GyqpGnXyBYSTjblE3RG+pFvdovhRbH74yem1IdQKmFHWAoJXgWeCy9BQfBxv1SCI13IrpuvZhJ9I+/9SUX/Sl1gpg8iFHMXTqFEns=";
    public static final String PID = "QYhr7tdzsYyYVfFjnv/fRApJBixLC2xt1Xv1Sk/AU1SQfmagNhgsftedo3/kiD9cKuyG3D1M1aNvVMgBCLYnuKhglQgnivzcIVVR3icwxjlJp/7gOEEB76OJzl7EG8AyqB1omC4KpgcAPjq/436t1150Mn4sDrbomJVgYZbDECD2808cxJ8ygs3iv0n/FQldvaU3bdDkKnHAE/XMnxVm7KGzn7/XI7ylxvGNmnVfwzfdnFlHnaL0oFbs0X9G9hDhI370BclD22qo8yh5y4jhbYYShtBKdB9E93CWt94Cx4y43fNAjAJJq2caMvR3M1vxFumTkc/Gn2IAtPxWMHi/e8qloF0oGO3I+j2ktWTRR6qbh7JyigcQLdOg8QPa95cpn4AyiRgXh0KyACtYFiakI3T1PsVx+GO/RrzuWIX5eYBe4TRbvD7DfuXJFrrxOhn8NOmTVOTf6OgYhAtL9OiLNLeplL1MFhZmnyqNJIAfyKZvKlQfFFMtugRZBJVmzTzI1qit1hAODI/X+0o4NMllfEMeFW6GWLnBCjl3ZM37+IS8YXLwsTYHvVd0s8zvPI6MohEdM6GyqpGnXyBYSTjblE3RG+pFvdovhRbH74yem1IdQKmFHWAoJXgWeCy9BQfBxv1SCI13IrpuvZhJ9I+/9SUX/Sl1gpg8iFHMXTqFEns=";

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        enrolByAadhaarRequestDto=new EnrolByAadhaarRequestDto();
        requestHeaders=new RequestHeaders();
        aadhaarResponseDto = new AadhaarResponseDto();
        irisDto=new IrisDto();
        consentDto=new ConsentDto();
        authData=new AuthData();
        aadhaarUserKycDto=new AadhaarUserKycDto();
        aadhaarAuthOtpDto=new AadhaarAuthOtpDto();
        transactionDto=new TransactionDto();
        accountDto=new AccountDto();
        hidPhrAddressDto=new HidPhrAddressDto();
        lgdDistrictResponse=new LgdDistrictResponse();
        verifyDemographicResponse=new VerifyDemographicResponse(true,"12","12");
        accountAuthMethodsDto=new AccountAuthMethodsDto();
        redisOtp= new RedisOtp();
        accountAuthMethodsDto.setHealthIdNumber("12");
        accountAuthMethodsDto.setAuthMethods(AuthMethods.DEMO_AUTH.toString());
        hidPhrAddressDto.setPhrAddress("123");
        authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.DEMO_AUTH);
        irisDto.setAadhaar(AADHAAR_NUMBER);
        irisDto.setPid(PID);
        //irisDto.setMobile("9876543872");
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        authData.setAuthMethods(authMethods);
        authData.setIris(irisDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        aadhaarAuthOtpDto.setUidtkn("1");
        aadhaarUserKycDto.setPhone("43234321");
        aadhaarUserKycDto.setUidiaTxn("1");
        aadhaarUserKycDto.setSignature("sign");
        aadhaarUserKycDto.setBirthdate("12/11/2000");
        aadhaarResponseDto.setStatus("SUCCESS");
        aadhaarResponseDto.setAadhaarAuthOtpDto(aadhaarAuthOtpDto);
        aadhaarResponseDto.setAadhaarUserKycDto(aadhaarUserKycDto);
        transactionDto.setTxnId(UUID.randomUUID());
        transactionDto.setMobile("******3210");
        accountDto.setHealthIdNumber("12321");
        accountDto.setStatus("ACTIVE");
        accountDto.setDayOfBirth("12");
        accountDto.setMonthOfBirth("05");
        accountDto.setYearOfBirth("2000");
        accountDto.setStateCode("1");
        accountDto.setDistrictCode("2");
        Map<String, Object> fTokenClaims = new HashMap<>();
        List<String> roleList = new ArrayList<>();
        fTokenClaims.put("sub","1");
        roleList.add("role");
        requestHeaders.setFTokenClaims(fTokenClaims);
        requestHeaders.setBenefitName("name");
        requestHeaders.setClientId("clientId");
        requestHeaders.setRoleList(roleList);

        redisOtp.setOtpValue("1234");
        redisOtp.setReceiver("123");
        receiverOtpTracker=new ReceiverOtpTracker();
        receiverOtpTracker.setVerifyOtpCount(1);
    }
    @AfterEach
    void tearDown(){
        enrolByAadhaarRequestDto=null;
        requestHeaders=null;
        aadhaarResponseDto = null;
        irisDto=null;
        consentDto=null;
        authData=null;
        aadhaarUserKycDto=null;
        aadhaarAuthOtpDto=null;
        transactionDto=null;
        accountDto=null;
        hidPhrAddressDto=null;
        lgdDistrictResponse=null;
        verifyDemographicResponse=null;
        accountAuthMethodsDto=null;
        redisOtp=null;
    }

    @Test
    public void verifyIrisTests(){
        //when(modelMapper.map(any(Accounts.class),any())).thenReturn(new AccountDto());
        //when(modelMapper.map(any(AccountDto.class),any())).thenReturn(new Accounts());
        when(aadhaarAppService.verifyIris(any())).thenReturn(Mono.just(aadhaarResponseDto));
        when(rsaUtil.decrypt(any())).thenReturn("test");
        when(transactionService.createTransactionEntity(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());
        //when(MapperUtils.mapKycDetails(any(),any())).thenReturn(new ABHAProfileDto());
        StepVerifier.create(enrolByIrisService.verifyIris(enrolByAadhaarRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    public void verifyIrisTests2(){
        accountDto.setYearOfBirth("2022");
        List<LgdDistrictResponse> lgdDistrictResponses=new ArrayList<>();
        lgdDistrictResponse.setDistrictCode("123");
        lgdDistrictResponses.add(lgdDistrictResponse);
        ReflectionTestUtils.setField(enrolByIrisService,"maxMobileLinkingCount",1);
        ReflectionTestUtils.setField(enrolByIrisService,"isTransactionManagementEnable",true);
        irisDto.setMobile("9876543210");
        authData.setAuthMethods(authMethods);
        authData.setIris(irisDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        when(accountService.getMobileLinkedAccountCount(any())).thenReturn(Mono.just(0));
        when(aadhaarAppService.verifyIris(any())).thenReturn(Mono.just(aadhaarResponseDto));
        when(rsaUtil.decrypt(any())).thenReturn("test");
        when(transactionService.createTransactionEntity(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.empty());
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());
        //when(MapperUtils.mapKycDetails(any(),any())).thenReturn(new ABHAProfileDto());
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(lgdDistrictResponses));
        when(accountService.prepareNewAccount(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("success"));
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));
        StepVerifier.create(enrolByIrisService.verifyIris(enrolByAadhaarRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    public void verifyIrisTests3(){
        List<AccountAuthMethodsDto> accountAuthMethodsDtos = new ArrayList<>();
        accountAuthMethodsDtos.add(accountAuthMethodsDto);
        List<LgdDistrictResponse> lgdDistrictResponses=new ArrayList<>();
        lgdDistrictResponse.setDistrictCode("123");
        lgdDistrictResponses.add(lgdDistrictResponse);
        ReflectionTestUtils.setField(enrolByIrisService,"maxMobileLinkingCount",1);
        ReflectionTestUtils.setField(enrolByIrisService,"isTransactionManagementEnable",false);
        irisDto.setMobile("9876543220");
        authData.setAuthMethods(authMethods);
        authData.setIris(irisDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        when(accountService.getMobileLinkedAccountCount(any())).thenReturn(Mono.just(0));
        when(aadhaarAppService.verifyIris(any())).thenReturn(Mono.just(aadhaarResponseDto));
        when(rsaUtil.decrypt(any())).thenReturn("test");
        when(transactionService.createTransactionEntity(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.empty());
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());
        //when(MapperUtils.mapKycDetails(any(),any())).thenReturn(new ABHAProfileDto());
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(lgdDistrictResponses));
        when(accountService.prepareNewAccount(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("success"));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any())).thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(accountAuthMethodsDtos));
        StepVerifier.create(enrolByIrisService.verifyIris(enrolByAadhaarRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    public void verifyIrisTests4(){
        List<AccountAuthMethodsDto> accountAuthMethodsDtos = new ArrayList<>();
        accountAuthMethodsDtos.add(accountAuthMethodsDto);
        List<LgdDistrictResponse> lgdDistrictResponses=new ArrayList<>();
        lgdDistrictResponse.setDistrictCode("123");
        lgdDistrictResponses.add(lgdDistrictResponse);
        ReflectionTestUtils.setField(enrolByIrisService,"maxMobileLinkingCount",1);
        ReflectionTestUtils.setField(enrolByIrisService,"isTransactionManagementEnable",false);
        irisDto.setMobile("9876543210");
        authData.setAuthMethods(authMethods);
        authData.setIris(irisDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        when(accountService.getMobileLinkedAccountCount(any())).thenReturn(Mono.just(0));
        when(aadhaarAppService.verifyIris(any())).thenReturn(Mono.just(aadhaarResponseDto));
        when(rsaUtil.decrypt(any())).thenReturn("test");
        when(transactionService.createTransactionEntity(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.empty());
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());
        //when(MapperUtils.mapKycDetails(any(),any())).thenReturn(new ABHAProfileDto());
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(lgdDistrictResponses));
        when(accountService.prepareNewAccount(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("success"));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any())).thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(accountAuthMethodsDtos));
        StepVerifier.create(enrolByIrisService.verifyIris(enrolByAadhaarRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    public void verifyIrisTests5(){
        List<AccountAuthMethodsDto> accountAuthMethodsDtos = new ArrayList<>();
        accountAuthMethodsDtos.add(accountAuthMethodsDto);
        List<LgdDistrictResponse> lgdDistrictResponses=new ArrayList<>();
        lgdDistrictResponse.setDistrictCode("123");
        lgdDistrictResponses.add(lgdDistrictResponse);
        ReflectionTestUtils.setField(enrolByIrisService,"maxMobileLinkingCount",1);
        ReflectionTestUtils.setField(enrolByIrisService,"isTransactionManagementEnable",true);
        irisDto.setMobile("9876543220");
        authData.setAuthMethods(authMethods);
        authData.setIris(irisDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        accountDto.setStatus("DEACTIVATED");
        when(accountService.getMobileLinkedAccountCount(any())).thenReturn(Mono.just(0));
        when(aadhaarAppService.verifyIris(any())).thenReturn(Mono.just(aadhaarResponseDto));
        when(rsaUtil.decrypt(any())).thenReturn("test");
        when(transactionService.createTransactionEntity(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.empty());
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());
        //when(MapperUtils.mapKycDetails(any(),any())).thenReturn(new ABHAProfileDto());
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(lgdDistrictResponses));
        when(accountService.prepareNewAccount(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("success"));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any())).thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(accountAuthMethodsDtos));
        StepVerifier.create(enrolByIrisService.verifyIris(enrolByAadhaarRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    public void verifyIrisTests6(){
        List<AccountAuthMethodsDto> accountAuthMethodsDtos = new ArrayList<>();
        accountAuthMethodsDtos.add(accountAuthMethodsDto);
        List<LgdDistrictResponse> lgdDistrictResponses=new ArrayList<>();
        lgdDistrictResponse.setDistrictCode("123");
        lgdDistrictResponses.add(lgdDistrictResponse);
        ReflectionTestUtils.setField(enrolByIrisService,"maxMobileLinkingCount",1);
        ReflectionTestUtils.setField(enrolByIrisService,"isTransactionManagementEnable",true);
        irisDto.setMobile("9876543220");
        authData.setAuthMethods(authMethods);
        authData.setIris(irisDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        accountDto.setStatus("DEACTIVATED");
        when(accountService.getMobileLinkedAccountCount(any())).thenReturn(Mono.just(0));
        when(aadhaarAppService.verifyIris(any())).thenReturn(Mono.just(aadhaarResponseDto));
        when(rsaUtil.decrypt(any())).thenReturn("test");
        when(transactionService.createTransactionEntity(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());
        //when(MapperUtils.mapKycDetails(any(),any())).thenReturn(new ABHAProfileDto());
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(lgdDistrictResponses));
        when(accountService.prepareNewAccount(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("success"));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any())).thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(accountAuthMethodsDtos));
        StepVerifier.create(enrolByIrisService.verifyIris(enrolByAadhaarRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    public void verifyIrisTestsexceptions(){

        List<AccountAuthMethodsDto> accountAuthMethodsDtos = new ArrayList<>();
        accountAuthMethodsDtos.add(accountAuthMethodsDto);
        List<LgdDistrictResponse> lgdDistrictResponses=new ArrayList<>();
        aadhaarResponseDto.setStatus("status");
        lgdDistrictResponse.setDistrictCode("123");
        lgdDistrictResponses.add(lgdDistrictResponse);
        ReflectionTestUtils.setField(enrolByIrisService,"maxMobileLinkingCount",1);
        ReflectionTestUtils.setField(enrolByIrisService,"isTransactionManagementEnable",true);
        irisDto.setMobile("9876543220");
        authData.setAuthMethods(authMethods);
        authData.setIris(irisDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        accountDto.setStatus("DEACTIVATED");
        when(accountService.getMobileLinkedAccountCount(any())).thenReturn(Mono.just(0));
        when(aadhaarAppService.verifyIris(any())).thenReturn(Mono.just(aadhaarResponseDto));
        when(rsaUtil.decrypt(any())).thenReturn("test");
        when(transactionService.createTransactionEntity(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());
        //when(MapperUtils.mapKycDetails(any(),any())).thenReturn(new ABHAProfileDto());
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(lgdDistrictResponses));
        when(accountService.prepareNewAccount(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("success"));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any())).thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(accountAuthMethodsDtos));
        when(redisService.isReceiverOtpTrackerAvailable(any())).thenReturn(true);
        when(redisService.getReceiverOtpTracker(any())).thenReturn(receiverOtpTracker);
        //when(redisService.saveReceiverOtpTracker(any(),any())).thenReturn(Mono.empty());
        StepVerifier.create(enrolByIrisService.verifyIris(enrolByAadhaarRequestDto,requestHeaders))
                .expectError(AadhaarExceptions.class)
                .verify();
        when(redisService.isReceiverOtpTrackerAvailable(any())).thenReturn(false);
        StepVerifier.create(enrolByIrisService.verifyIris(enrolByAadhaarRequestDto,requestHeaders))
                .expectError(AadhaarExceptions.class)
                .verify();
    }
}
