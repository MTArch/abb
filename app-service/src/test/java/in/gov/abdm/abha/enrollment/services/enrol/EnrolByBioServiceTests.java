package in.gov.abdm.abha.enrollment.services.enrol;
import in.gov.abdm.abha.enrollment.commontestdata.CommonTestData;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarExceptions;
import in.gov.abdm.abha.enrollment.exception.abha_db.AbhaDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.application.AbhaUnProcessableException;
import in.gov.abdm.abha.enrollment.exception.notification.NotificationGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarAuthOtpDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarUserKycDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicResponse;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.*;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.entities.*;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import in.gov.abdm.abha.enrollment.model.notification.NotificationResponseDto;
import in.gov.abdm.abha.enrollment.model.redis.otp.ReceiverOtpTracker;
import in.gov.abdm.abha.enrollment.model.redis.otp.RedisOtp;
import in.gov.abdm.abha.enrollment.services.aadhaar.impl.AadhaarAppServiceImpl;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.account_auth_methods.AccountAuthMethodService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.de_duplication.DeDuplicationService;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.bio.EnrolByBioService;
import in.gov.abdm.abha.enrollment.services.notification.NotificationService;
import in.gov.abdm.abha.enrollment.services.redis.RedisService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.LgdUtility;
import in.gov.abdm.abha.enrollment.utilities.MapperUtils;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaAddressGenerator;
import in.gov.abdm.abha.enrollment.utilities.jwt.JWTUtil;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class EnrolByBioServiceTests {

    public static final String AADHAAR_NUMBER = "QYhr7tdzsYyYVfFjnv/fRApJBixLC2xt1Xv1Sk/AU1SQfmagNEyamwg6o3/kiD9cKuyG3D1M1aNvVMgBCLYnuKhglQgnivzcIVVR3icwxjlJp/7gOEEB76OJzl7EG8AyqB1omC4KpgcAPjq/436t1150Mn4sDrbomJVgYZbDECD2808cxJ8ygs3iv0n/FQldvaU3bdDkKnHAE/XMnxVm7KGzn7/XI7ylxvGNmnVfwzfdnFlHnaL0oFbs0X9G9hDhI370BclD22qo8yh5y4jhbYYShtBKdB9E93CWt94Cx4y43fNAjAJJq2caMvR3M1vxFumTkc/Gn2IAtPxWMHi/e8qloF0oGO3I+j2ktWTRR6qbh7JyigcQLdOg8QPa95cpn4AyiRgXh0KyACtYFiakI3T1PsVx+GO/RrzuWIX5eYBe4TRbvD7DfuXJFrrxOhn8NOmTVOTf6OgYhAtL9OiLNLeplL1MFhZmnyqNJIAfyKZvKlQfFFMtugRZBJVmzTzI1qit1hAODI/X+0o4NMllfEMeFW6GWLnBCjl3ZM37+IS8YXLwsTYHvVd0s8zvPI6MohEdM6GyqpGnXyBYSTjblE3RG+pFvdovhRbH74yem1IdQKmFHWAoJXgWeCy9BQfBxv1SCI13IrpuvZhJ9I+/9SUX/Sl1gpg8iFHMXTqFEns=";
    public static final String TOKEN = "eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiI5MS0zODY0LTQ3MzUtNzc2OCIsImNsaWVudElkIjoiYWJoYS1wcm9maWxlLWFwcC1hcGkiLCJzeXN0ZW0iOiJBQkhBLU4iLCJtb2JpbGUiOiI3MDM3MjQ4NTEwIiwiYWJoYU51bWJlciI6IjkxLTM4NjQtNDczNS03NzY4IiwicHJlZmVycmVkQWJoYUFkZHJlc3MiOiI5MTM4NjQ0NzM1Nzc2OEBhYmRtIiwidHlwIjoiVHJhbnNhY3Rpb24iLCJleHAiOjE2ODAwODEyMDAsImlhdCI6MTY4MDA3OTQwMCwidHhuSWQiOiJiMzU5YjQ1OC1kYTBmLTQ0NjMtOTg5MC1mNGRhNGUzMjEyNjcifQ.RhofHMB7mJPXQLggZFMNc52Li7cA8fO_yI8WAyzpwRdKihuEMOJ6AE7uBI27vRr1iHr6mTMvjzc5eM9Izw9zmAGaEcuJQu6RSznCBNRHIs-dkQwHPtgKw4ICKdX6WdiOvCzaO9a4qYxoyeDRVvU5nZ4-4QFEYNJtDUaLBIKJEbXDtzr1pq9irxCczo9-99ZYeIzxduE_sTCNyCUi2MaAj2Bo0Ij4Qs555jJ9eDOrpLG2BsYHsrkEltN7_o7gm4DFd9uIWSzcPVRQZmuk4NlynpE5LXW1QUxZrg6hxhnbJWNw_E6fmDgXigyPrwT1UdPTrERCC7FjxBUVvpYcSftdQY0aVBqMooIwRfWC2Oqy-0F6wHDegotnyxsCSE_1QVR5QwgJF-16745Eq8yQM0WgJS8FeJ9i5ah-HpAVuZpFqYCHm520uKNFHzSAsidrTRUhJTbmwAVv0LSqjEL0I1Thp300e1W04owuobE2JVQr42eKIElAbFdcXO3XsaVJ5hbQO1i_pGSNqxywStNIVtfAhhBChJ-aOPrPmU55ZDyqkjwULbL35kb2Ai_QGEm7ie2HdEcAHusZyqqrzvDfmFY9lcw7RFITvJ_V4rp2b9Rx9UoQKQ5f1nn4Y9rnhdALVzqBh7Wjdv-OsN-zHQth-Vu79XhzTvNfB14iGf0D2RQGBIY";
    public static final String REFRESH_TOKEN = "eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiI5MS0zODY0LTQ3MzUtNzc2OCIsImNsaWVudElkIjoiYWJoYS1wcm9maWxlLWFwcC1hcGkiLCJzeXN0ZW0iOiJBQkhBLU4iLCJ0eXAiOiJSZWZyZXNoIiwiZXhwIjoxNjgxMzc1NDAxLCJpYXQiOjE2ODAwNzk0MDF9.TgXvxiZLYAzhcphDgQjWwdpyvg01pZ9ANbdrKIsdgw55ZMuEe_K2JVNs9ynLWDKJ54IPmNuHvFQS6Wvs17sMiYCCnEaUzkpn86-xM4O5TZHkGAi6WhdGTsYYIQZghqYBpH89Y88AyIYV3jZEUI89bjVlziK9nNKxTQLSRWpZSw42bPLbi8CSHv14H9ozlJGoRsjbXSUJrArw52yGwmWkOs8rQROrIaftqQjkmucTvoptXe80K6PRiL9tT2sY739iAigAj-6ffinlIis_6goNQ5aAJFUsAE7c7aVKMdW686pp0aILiDJYyfSdpzCXvj3ihBlBImhcyDlj38b4PGWUSNVkSCyOvl_pCmGjYF3lUgsqejYb_7_nYS9su0HlVKRCkeTBnA5JvomNOCwOfRaK7wuCoZU5P1Na--dYUjRGZV1M2r-hUzj7lJO2s0JxiY37BCmZuJXnzVdKVcqcWk2OyICHHJE8SfIvPh2HW3IUF-jFhDWSHP2a53UbiDPQK-S9bJkVFnsGm6OO4WB2C-Yd1TzcguYgf9If8GcRpion6KZB05luFvUv6Z-ymcDtSd_KE_IPD4tWhiapWgkVSZlhAS34F1DmlZr1oj8LBjbOrdiZz95BsiANMpT_CW1QWnEHNrq9w5jJcwC51Ityw_MoBNK9XwZ9CDyGtuTmIMCTgfI";
    public static final String PID = "QYhr7tdzsYyYVfFjnv/fRApJBixLC2xt1Xv1Sk/AU1SQfmagNhgsftedo3/kiD9cKuyG3D1M1aNvVMgBCLYnuKhglQgnivzcIVVR3icwxjlJp/7gOEEB76OJzl7EG8AyqB1omC4KpgcAPjq/436t1150Mn4sDrbomJVgYZbDECD2808cxJ8ygs3iv0n/FQldvaU3bdDkKnHAE/XMnxVm7KGzn7/XI7ylxvGNmnVfwzfdnFlHnaL0oFbs0X9G9hDhI370BclD22qo8yh5y4jhbYYShtBKdB9E93CWt94Cx4y43fNAjAJJq2caMvR3M1vxFumTkc/Gn2IAtPxWMHi/e8qloF0oGO3I+j2ktWTRR6qbh7JyigcQLdOg8QPa95cpn4AyiRgXh0KyACtYFiakI3T1PsVx+GO/RrzuWIX5eYBe4TRbvD7DfuXJFrrxOhn8NOmTVOTf6OgYhAtL9OiLNLeplL1MFhZmnyqNJIAfyKZvKlQfFFMtugRZBJVmzTzI1qit1hAODI/X+0o4NMllfEMeFW6GWLnBCjl3ZM37+IS8YXLwsTYHvVd0s8zvPI6MohEdM6GyqpGnXyBYSTjblE3RG+pFvdovhRbH74yem1IdQKmFHWAoJXgWeCy9BQfBxv1SCI13IrpuvZhJ9I+/9SUX/Sl1gpg8iFHMXTqFEns=";

    @InjectMocks
    EnrolByBioService enrolByBioService;
    @Mock
    AadhaarAppServiceImpl aadhaarAppService;
    @Mock
    TransactionService transactionService;
    @Mock
    RSAUtil rsaUtil;
    @Mock
    AccountService accountService;
    @Mock
    AbhaAddressGenerator abhaAddressGenerator;
    @Mock
    NotificationService notificationService;
    @Mock
    JWTUtil jwtUtil;
    @Mock
    LgdUtility lgdUtility;
    @Mock
    DeDuplicationService deDuplicationService;
    @Mock
    HidPhrAddressService hidPhrAddressService;
    @Mock
    AccountAuthMethodService accountAuthMethodService;
    @Mock
    RedisService redisService;
    @Mock
    RedisOtp redisOtp;

    private Boolean isTransactionManagementEnable;
    private AadhaarResponseDto aadhaarResponseDto;
    private AadhaarUserKycDto aadhaarUserKycDto;
    private TransactionDto transactionDto;
    private AccountDto accountDto;
    private HidPhrAddressDto hidPhrAddressDto;
    private AccountAuthMethodsDto authMethodsDto;
    private EnrolByAadhaarRequestDto enrolByAadhaarRequestDto,enrolByAadhaarRequestDtoNoMobile;
    private AuthData authData;
    private ConsentDto consentDto;
    private LgdDistrictResponse lgdDistrictResponse;
    private BioDto bioDto;
    private AadhaarAuthOtpDto aadhaarAuthOtpDto;
    private RequestHeaders requestHeaders;
    private ReceiverOtpTracker receiverOtpTracker;
    private VerifyDemographicResponse verifyDemographicResponse;
    private ABHAProfileDto abhaProfileDto;
    private MapperUtils mapperUtils;
    private NotificationResponseDto notificationResponseDto;

    @BeforeEach
    void setup()
    {
        MockitoAnnotations.openMocks(this);
        transactionDto = new TransactionDto();
        accountDto = new AccountDto();
        hidPhrAddressDto = new HidPhrAddressDto();
        authMethodsDto = new AccountAuthMethodsDto();
        enrolByAadhaarRequestDto = new EnrolByAadhaarRequestDto();
        enrolByAadhaarRequestDtoNoMobile=new EnrolByAadhaarRequestDto();
        aadhaarResponseDto = new AadhaarResponseDto();
        authData = new AuthData();
        bioDto = new BioDto();
        consentDto = new ConsentDto();
        aadhaarUserKycDto = new AadhaarUserKycDto();
        lgdDistrictResponse = new LgdDistrictResponse();
        requestHeaders = new RequestHeaders();
        aadhaarAuthOtpDto = new AadhaarAuthOtpDto();
        receiverOtpTracker=new ReceiverOtpTracker();
        verifyDemographicResponse=new VerifyDemographicResponse();
        abhaProfileDto = new ABHAProfileDto();
        notificationResponseDto=new NotificationResponseDto("sent");

        verifyDemographicResponse.setVerified(true);
        verifyDemographicResponse.setReason("verified");
        verifyDemographicResponse.setXmlUid("123");
        aadhaarResponseDto.setStatus("success");
        aadhaarAuthOtpDto.setUidtkn("test");
        aadhaarResponseDto.setAadhaarAuthOtpDto(aadhaarAuthOtpDto);
        aadhaarUserKycDto.setStatus("success");
        aadhaarUserKycDto.setBirthdate("10-10-2022");
        aadhaarUserKycDto.setSignature("sign");
        aadhaarResponseDto.setAadhaarUserKycDto(aadhaarUserKycDto);
        transactionDto.setStatus("ACTIVE");
        transactionDto.setTxnId(UUID.fromString("cda04910-37ca-4f2e-84d9-4e5a970d3dc7"));
        transactionDto.setAadharNo(AADHAAR_NUMBER);
        transactionDto.setMobile("******3872");
        accountDto.setYearOfBirth("1995");
        accountDto.setMonthOfBirth("09");
        accountDto.setDayOfBirth("29");
        accountDto.setHealthIdNumber("1234");
        accountDto.setStatus("ACTIVE");
        abhaProfileDto.setAbhaNumber("1234");
        hidPhrAddressDto.setHealthIdNumber("76-5245-8762-1574");
        hidPhrAddressDto.setPhrAddress("76524587621574@abdm");

        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.BIO);
        bioDto.setAadhaar(AADHAAR_NUMBER);
        bioDto.setFingerPrintAuthPid(PID);
        bioDto.setMobile("9876543872");
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        authData.setAuthMethods(authMethods);
        authData.setBio(bioDto);
        OtpDto otpDto=new OtpDto();
        otpDto.setOtpValue("123332");
        otpDto.setMobile("9876543872");
        otpDto.setTxnId(CommonTestData.TRANSACTION_ID_VALID);
        authData.setOtp(otpDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);

        isTransactionManagementEnable=true;

    }

    @AfterEach
    void tearDown()
    {
        accountDto = null;
        hidPhrAddressDto = null;
        authMethodsDto = null;
        enrolByAadhaarRequestDto = null;
        authData = null;
        bioDto = null;
        consentDto = null;
        aadhaarUserKycDto = null;
        lgdDistrictResponse = null;
        transactionDto = null;
        requestHeaders = null;
        aadhaarAuthOtpDto = null;
        receiverOtpTracker =null;
        verifyDemographicResponse=null;
        notificationResponseDto=null;
        abhaProfileDto=null;
    }

    @Test
    void verifyBioSuccess()
    {

        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));
        when(accountService.getMobileLinkedAccountCount(any())).thenReturn(Mono.just(-1));
        when(aadhaarAppService.verifyBio(any(), any())).thenReturn(Mono.just(aadhaarResponseDto));
        when(rsaUtil.decrypt(any())).thenReturn("853123431963");
        when(transactionService.createTransactionEntity(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.empty());
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(Arrays.asList(lgdDistrictResponse)));
        when(accountService.prepareNewAccount(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any())).thenReturn(Mono.empty());
        when(abhaAddressGenerator.generateDefaultAbhaAddress(any())).thenReturn("76524587621574@abdm");
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any())).thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(Arrays.asList(authMethodsDto)));
        when(jwtUtil.generateToken(any(),any())).thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any())).thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime()).thenReturn(1296000L);

        StepVerifier.create(enrolByBioService.verifyBio(enrolByAadhaarRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyBioSuccessnomobile()
    {   bioDto.setMobile(null);
        authData.setBio(bioDto);
        enrolByAadhaarRequestDtoNoMobile.setAuthData(authData);
        enrolByAadhaarRequestDtoNoMobile.setConsent(consentDto);
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));
        when(accountService.getMobileLinkedAccountCount(any())).thenReturn(Mono.just(-1));
        when(aadhaarAppService.verifyBio(any(), any())).thenReturn(Mono.just(aadhaarResponseDto));
        when(rsaUtil.decrypt(any())).thenReturn("853123431963");
        when(transactionService.createTransactionEntity(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.empty());
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(Arrays.asList(lgdDistrictResponse)));
        when(accountService.prepareNewAccount(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any())).thenReturn(Mono.empty());
        when(abhaAddressGenerator.generateDefaultAbhaAddress(any())).thenReturn("76524587621574@abdm");
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any())).thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(Arrays.asList(authMethodsDto)));
        when(jwtUtil.generateToken(any(),any())).thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any())).thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime()).thenReturn(1296000L);

        StepVerifier.create(enrolByBioService.verifyBio(enrolByAadhaarRequestDtoNoMobile,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyBioSuccessAbhaUnProcessableExceptionError()
    {
        when(accountService.getMobileLinkedAccountCount(any())).thenReturn(Mono.just(6));
         StepVerifier.create(enrolByBioService.verifyBio(enrolByAadhaarRequestDto,requestHeaders))
                .expectError(AbhaUnProcessableException.class).verify();
    }
    @Test
    void verifyBioSuccess2()
    {
        accountDto.setYearOfBirth("2023");
        ReflectionTestUtils.setField(enrolByBioService,"isTransactionManagementEnable",true);
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));
        when(accountService.getMobileLinkedAccountCount(any())).thenReturn(Mono.just(-1));
        when(aadhaarAppService.verifyBio(any(), any())).thenReturn(Mono.just(aadhaarResponseDto));
        when(rsaUtil.decrypt(any())).thenReturn("853123431963");
        when(transactionService.createTransactionEntity(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.just(accountDto));
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(Arrays.asList(lgdDistrictResponse)));
        when(accountService.prepareNewAccount(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any())).thenReturn(Mono.empty());
        when(abhaAddressGenerator.generateDefaultAbhaAddress(any())).thenReturn("76524587621574@abdm");
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("saved"));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any())).thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(Arrays.asList(authMethodsDto)));
        when(notificationService.sendABHACreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any())).thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any())).thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime()).thenReturn(1296000L);

        StepVerifier.create(enrolByBioService.verifyBio(enrolByAadhaarRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyBioSuccess3()
    {
        accountDto.setYearOfBirth("2023");
        accountDto.setStatus("DEACTIVATED");
        accountDto.setMobile(null);
        transactionDto.setMobile("23456764532");
        ReflectionTestUtils.setField(enrolByBioService,"isTransactionManagementEnable",true);
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));
        when(accountService.getMobileLinkedAccountCount(any())).thenReturn(Mono.just(-1));
        when(aadhaarAppService.verifyBio(any(), any())).thenReturn(Mono.just(aadhaarResponseDto));
        when(rsaUtil.decrypt(any())).thenReturn("853123431963");
        when(transactionService.createTransactionEntity(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.empty());
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(Arrays.asList(lgdDistrictResponse)));
        when(accountService.prepareNewAccount(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any())).thenReturn(Mono.empty());
        when(abhaAddressGenerator.generateDefaultAbhaAddress(any())).thenReturn("76524587621574@abdm");
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("saved"));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any())).thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(Arrays.asList(authMethodsDto)));
        when(notificationService.sendABHACreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any())).thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any())).thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime()).thenReturn(1296000L);

        StepVerifier.create(enrolByBioService.verifyBio(enrolByAadhaarRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyBioSuccesserror()
    {
        accountDto.setYearOfBirth("2023");
        accountDto.setStatus("DEACTIVATED");
        accountDto.setMobile(null);
        aadhaarResponseDto.setStatus("2q");
        transactionDto.setMobile("23456764532");
        ReflectionTestUtils.setField(enrolByBioService,"isTransactionManagementEnable",true);
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));
        when(accountService.getMobileLinkedAccountCount(any())).thenReturn(Mono.just(-1));
        when(aadhaarAppService.verifyBio(any(), any())).thenReturn(Mono.just(aadhaarResponseDto));
        when(rsaUtil.decrypt(any())).thenReturn("853123431963");
        when(transactionService.createTransactionEntity(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.just(accountDto));
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(Arrays.asList(lgdDistrictResponse)));
        when(accountService.prepareNewAccount(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any())).thenReturn(Mono.empty());
        when(abhaAddressGenerator.generateDefaultAbhaAddress(any())).thenReturn("76524587621574@abdm");
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("saved"));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any())).thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(Arrays.asList(authMethodsDto)));
        when(notificationService.sendABHACreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any())).thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any())).thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime()).thenReturn(1296000L);
        when(redisService.isReceiverOtpTrackerAvailable(any())).thenReturn(true);
        when(redisService.getReceiverOtpTracker(any())).thenReturn(new ReceiverOtpTracker("",1,1,true));
       // Assert.assertThrows(AadhaarExceptions.class,()->enrolByBioService.verifyBio(enrolByAadhaarRequestDto,requestHeaders));
        StepVerifier.create(enrolByBioService.verifyBio(enrolByAadhaarRequestDto,requestHeaders))
                .expectError(AadhaarExceptions.class)
                .verify();
    }
    @Test
    void verifyBioSuccess4()
    {
        accountDto.setYearOfBirth("2023");
        accountDto.setStatus("DEACTIVATED");
        accountDto.setMobile(null);
        //transactionDto.setMobile("23456764532");
        ReflectionTestUtils.setField(enrolByBioService,"isTransactionManagementEnable",true);
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));
        when(accountService.getMobileLinkedAccountCount(any())).thenReturn(Mono.just(-1));
        when(aadhaarAppService.verifyBio(any(), any())).thenReturn(Mono.just(aadhaarResponseDto));
        when(rsaUtil.decrypt(any())).thenReturn("853123431963");
        when(transactionService.createTransactionEntity(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.empty());
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(Arrays.asList(lgdDistrictResponse)));
        when(accountService.prepareNewAccount(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any())).thenReturn(Mono.empty());
        when(abhaAddressGenerator.generateDefaultAbhaAddress(any())).thenReturn("76524587621574@abdm");
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("saved"));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any())).thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(Arrays.asList(authMethodsDto)));
        when(notificationService.sendABHACreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any())).thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any())).thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime()).thenReturn(1296000L);

        StepVerifier.create(enrolByBioService.verifyBio(enrolByAadhaarRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyBioSuccess5()
    {
        accountDto.setYearOfBirth("2023");
        accountDto.setStatus("DEACTIVATED");
        accountDto.setMobile(null);
        bioDto.setMobile(null);
        authData.setBio(bioDto);
        transactionDto.setMobile(null);
        enrolByAadhaarRequestDtoNoMobile.setAuthData(authData);
        enrolByAadhaarRequestDtoNoMobile.setConsent(consentDto);
        //transactionDto.setMobile("23456764532");
        ReflectionTestUtils.setField(enrolByBioService,"isTransactionManagementEnable",true);
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));
        when(accountService.getMobileLinkedAccountCount(any())).thenReturn(Mono.just(-1));
        when(aadhaarAppService.verifyBio(any(), any())).thenReturn(Mono.just(aadhaarResponseDto));
        when(rsaUtil.decrypt(any())).thenReturn("853123431963");
        when(transactionService.createTransactionEntity(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.empty());
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(Arrays.asList(lgdDistrictResponse)));
        when(accountService.prepareNewAccount(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any())).thenReturn(Mono.empty());
        when(abhaAddressGenerator.generateDefaultAbhaAddress(any())).thenReturn("76524587621574@abdm");
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("saved"));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any())).thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(Arrays.asList(authMethodsDto)));
        when(notificationService.sendABHACreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any())).thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any())).thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime()).thenReturn(1296000L);

        StepVerifier.create(enrolByBioService.verifyBio(enrolByAadhaarRequestDtoNoMobile,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyBioSuccessError()
    {
        accountDto.setYearOfBirth("2023");
        accountDto.setStatus("DEACTIVATED");
        accountDto.setMobile(null);
        accountDto.setHealthIdNumber("");
        notificationResponseDto.setStatus("t");
        //transactionDto.setMobile("23456764532");
        ReflectionTestUtils.setField(enrolByBioService,"isTransactionManagementEnable",true);
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));
        when(accountService.getMobileLinkedAccountCount(any())).thenReturn(Mono.just(-1));
        when(aadhaarAppService.verifyBio(any(), any())).thenReturn(Mono.just(aadhaarResponseDto));
        when(rsaUtil.decrypt(any())).thenReturn("853123431963");
        when(transactionService.createTransactionEntity(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.empty());
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(Arrays.asList(lgdDistrictResponse)));
        when(accountService.prepareNewAccount(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any())).thenReturn(Mono.empty());
        when(abhaAddressGenerator.generateDefaultAbhaAddress(any())).thenReturn("76524587621574@abdm");
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("saved"));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any())).thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(Arrays.asList(authMethodsDto)));
        when(notificationService.sendABHACreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any())).thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any())).thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime()).thenReturn(1296000L);

        StepVerifier.create(enrolByBioService.verifyBio(enrolByAadhaarRequestDto,requestHeaders))
                .expectError(NotificationGatewayUnavailableException.class)
                .verify();
    }
    @Test
    void verifyBioSuccess6()
    {
        accountDto.setYearOfBirth("2023");
        ReflectionTestUtils.setField(enrolByBioService,"isTransactionManagementEnable",true);
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));
        when(accountService.getMobileLinkedAccountCount(any())).thenReturn(Mono.just(-1));
        when(aadhaarAppService.verifyBio(any(), any())).thenReturn(Mono.just(aadhaarResponseDto));
        when(rsaUtil.decrypt(any())).thenReturn("853123431963");
        when(transactionService.createTransactionEntity(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.just(accountDto));
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(Arrays.asList(lgdDistrictResponse)));
        when(accountService.prepareNewAccount(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any())).thenReturn(Mono.empty());
        when(abhaAddressGenerator.generateDefaultAbhaAddress(any())).thenReturn("76524587621574@abdm");
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("saved"));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.error(Exception::new));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any())).thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.empty());
        when(notificationService.sendABHACreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any())).thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any())).thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime()).thenReturn(1296000L);

        StepVerifier.create(enrolByBioService.verifyBio(enrolByAadhaarRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyBioSuccesserror3()
    {
        accountDto.setYearOfBirth("2023");
        ReflectionTestUtils.setField(enrolByBioService,"isTransactionManagementEnable",false);
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));
        when(accountService.getMobileLinkedAccountCount(any())).thenReturn(Mono.just(-1));
        when(aadhaarAppService.verifyBio(any(), any())).thenReturn(Mono.just(aadhaarResponseDto));
        when(rsaUtil.decrypt(any())).thenReturn("853123431963");
        when(transactionService.createTransactionEntity(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.empty());
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(Arrays.asList(lgdDistrictResponse)));
        when(accountService.prepareNewAccount(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any())).thenReturn(Mono.empty());
        when(abhaAddressGenerator.generateDefaultAbhaAddress(any())).thenReturn("76524587621574@abdm");
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("saved"));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any())).thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(Arrays.asList()));
        when(notificationService.sendABHACreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any())).thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any())).thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime()).thenReturn(1296000L);

        StepVerifier.create(enrolByBioService.verifyBio(enrolByAadhaarRequestDto,requestHeaders))
                .expectError(AbhaDBGatewayUnavailableException.class)
                .verify();
    }
    @Test
    void verifyBioSuccesserror2()
    {
        accountDto.setYearOfBirth("2023");
        accountDto.setStatus("DEACTIVATED");
        accountDto.setMobile(null);
        aadhaarResponseDto.setStatus("2q");
        transactionDto.setMobile("23456764532");
        aadhaarResponseDto.setAadhaarAuthOtpDto(null);
        ReflectionTestUtils.setField(enrolByBioService,"isTransactionManagementEnable",true);
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));
        when(accountService.getMobileLinkedAccountCount(any())).thenReturn(Mono.just(-1));
        when(aadhaarAppService.verifyBio(any(), any())).thenReturn(Mono.just(aadhaarResponseDto));
        when(rsaUtil.decrypt(any())).thenReturn("853123431963");
        when(transactionService.createTransactionEntity(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.just(accountDto));
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(Arrays.asList(lgdDistrictResponse)));
        when(accountService.prepareNewAccount(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any())).thenReturn(Mono.empty());
        when(abhaAddressGenerator.generateDefaultAbhaAddress(any())).thenReturn("76524587621574@abdm");
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("saved"));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any())).thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(Arrays.asList(authMethodsDto)));
        when(notificationService.sendABHACreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any())).thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any())).thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime()).thenReturn(1296000L);
        when(redisService.isReceiverOtpTrackerAvailable(any())).thenReturn(true);
        when(redisService.getReceiverOtpTracker(any())).thenReturn(new ReceiverOtpTracker("",1,1,true));
        // Assert.assertThrows(AadhaarExceptions.class,()->enrolByBioService.verifyBio(enrolByAadhaarRequestDto,requestHeaders));
        StepVerifier.create(enrolByBioService.verifyBio(enrolByAadhaarRequestDto,requestHeaders))
                .expectError(AadhaarExceptions.class)
                .verify();
    }




}
