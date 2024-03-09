
package in.gov.abdm.abha.enrollment.services.enrol;
import in.gov.abdm.abha.enrollment.client.HidBenefitDBFClient;
import in.gov.abdm.abha.enrollment.client.LgdAppFClient;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.MobileType;
import in.gov.abdm.abha.enrollment.exception.abha_db.AbhaDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.application.AbhaUnProcessableException;
import in.gov.abdm.abha.enrollment.exception.application.BadRequestException;
import in.gov.abdm.abha.enrollment.exception.notification.NotificationGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarUserKycDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicResponse;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.demographic.Demographic;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.*;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentRequestDto;
import in.gov.abdm.abha.enrollment.model.entities.*;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import in.gov.abdm.abha.enrollment.model.nepix.VerifyDLResponse;
import in.gov.abdm.abha.enrollment.model.notification.NotificationResponseDto;
import in.gov.abdm.abha.enrollment.model.redis.otp.RedisOtp;
import in.gov.abdm.abha.enrollment.services.aadhaar.AadhaarAppService;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.account_auth_methods.AccountAuthMethodService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.de_duplication.DeDuplicationService;
import in.gov.abdm.abha.enrollment.services.document.IdentityDocumentDBService;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.demographic.EnrolByDemographicService;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.demographic.EnrolByDemographicValidatorService;
import in.gov.abdm.abha.enrollment.services.notification.NotificationService;
import in.gov.abdm.abha.enrollment.services.redis.RedisService;
import in.gov.abdm.abha.enrollment.utilities.LgdUtility;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaAddressGenerator;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaNumberGenerator;
import in.gov.abdm.abha.enrollment.utilities.jwt.JWTUtil;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class EnrolByDemographicServiceTests {

    public static final String TOKEN = "eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiI5MS0zODY0LTQ3MzUtNzc2OCIsImNsaWVudElkIjoiYWJoYS1wcm9maWxlLWFwcC1hcGkiLCJzeXN0ZW0iOiJBQkhBLU4iLCJtb2JpbGUiOiI3MDM3MjQ4NTEwIiwiYWJoYU51bWJlciI6IjkxLTM4NjQtNDczNS03NzY4IiwicHJlZmVycmVkQWJoYUFkZHJlc3MiOiI5MTM4NjQ0NzM1Nzc2OEBhYmRtIiwidHlwIjoiVHJhbnNhY3Rpb24iLCJleHAiOjE2ODAwODEyMDAsImlhdCI6MTY4MDA3OTQwMCwidHhuSWQiOiJiMzU5YjQ1OC1kYTBmLTQ0NjMtOTg5MC1mNGRhNGUzMjEyNjcifQ.RhofHMB7mJPXQLggZFMNc52Li7cA8fO_yI8WAyzpwRdKihuEMOJ6AE7uBI27vRr1iHr6mTMvjzc5eM9Izw9zmAGaEcuJQu6RSznCBNRHIs-dkQwHPtgKw4ICKdX6WdiOvCzaO9a4qYxoyeDRVvU5nZ4-4QFEYNJtDUaLBIKJEbXDtzr1pq9irxCczo9-99ZYeIzxduE_sTCNyCUi2MaAj2Bo0Ij4Qs555jJ9eDOrpLG2BsYHsrkEltN7_o7gm4DFd9uIWSzcPVRQZmuk4NlynpE5LXW1QUxZrg6hxhnbJWNw_E6fmDgXigyPrwT1UdPTrERCC7FjxBUVvpYcSftdQY0aVBqMooIwRfWC2Oqy-0F6wHDegotnyxsCSE_1QVR5QwgJF-16745Eq8yQM0WgJS8FeJ9i5ah-HpAVuZpFqYCHm520uKNFHzSAsidrTRUhJTbmwAVv0LSqjEL0I1Thp300e1W04owuobE2JVQr42eKIElAbFdcXO3XsaVJ5hbQO1i_pGSNqxywStNIVtfAhhBChJ-aOPrPmU55ZDyqkjwULbL35kb2Ai_QGEm7ie2HdEcAHusZyqqrzvDfmFY9lcw7RFITvJ_V4rp2b9Rx9UoQKQ5f1nn4Y9rnhdALVzqBh7Wjdv-OsN-zHQth-Vu79XhzTvNfB14iGf0D2RQGBIY";
    public static final String REFRESH_TOKEN = "eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiI5MS0zODY0LTQ3MzUtNzc2OCIsImNsaWVudElkIjoiYWJoYS1wcm9maWxlLWFwcC1hcGkiLCJzeXN0ZW0iOiJBQkhBLU4iLCJ0eXAiOiJSZWZyZXNoIiwiZXhwIjoxNjgxMzc1NDAxLCJpYXQiOjE2ODAwNzk0MDF9.TgXvxiZLYAzhcphDgQjWwdpyvg01pZ9ANbdrKIsdgw55ZMuEe_K2JVNs9ynLWDKJ54IPmNuHvFQS6Wvs17sMiYCCnEaUzkpn86-xM4O5TZHkGAi6WhdGTsYYIQZghqYBpH89Y88AyIYV3jZEUI89bjVlziK9nNKxTQLSRWpZSw42bPLbi8CSHv14H9ozlJGoRsjbXSUJrArw52yGwmWkOs8rQROrIaftqQjkmucTvoptXe80K6PRiL9tT2sY739iAigAj-6ffinlIis_6goNQ5aAJFUsAE7c7aVKMdW686pp0aILiDJYyfSdpzCXvj3ihBlBImhcyDlj38b4PGWUSNVkSCyOvl_pCmGjYF3lUgsqejYb_7_nYS9su0HlVKRCkeTBnA5JvomNOCwOfRaK7wuCoZU5P1Na--dYUjRGZV1M2r-hUzj7lJO2s0JxiY37BCmZuJXnzVdKVcqcWk2OyICHHJE8SfIvPh2HW3IUF-jFhDWSHP2a53UbiDPQK-S9bJkVFnsGm6OO4WB2C-Yd1TzcguYgf9If8GcRpion6KZB05luFvUv6Z-ymcDtSd_KE_IPD4tWhiapWgkVSZlhAS34F1DmlZr1oj8LBjbOrdiZz95BsiANMpT_CW1QWnEHNrq9w5jJcwC51Ityw_MoBNK9XwZ9CDyGtuTmIMCTgfI";

    @InjectMocks
    EnrolByDemographicService enrolByDemographicService;
    @Mock
    EnrolByDemographicValidatorService enrolByDemographicValidatorService;

    @Mock
    RSAUtil rsaUtils;
    @Mock
    AadhaarAppService aadhaarAppService;
    @Mock
    AccountService accountService;
    @Mock
    AbhaAddressGenerator abhaAddressGenerator;
    @Mock
    DeDuplicationService deDuplicationService;
    @Mock
    LgdUtility lgdUtility;
    @Mock
    HidPhrAddressService hidPhrAddressService;
    @Mock
    IdentityDocumentDBService identityDocumentDBService;
    @Mock
    AccountAuthMethodService accountAuthMethodService;
    @Mock
    NotificationService notificationService;
    @Mock
    JWTUtil jwtUtil;
    @Mock
    HidBenefitDBFClient hidBenefitDBFClient;
    @Mock
    LgdAppFClient lgdAppFClient;
    @Mock
    RedisService redisService;

    private AadhaarResponseDto aadhaarResponseDto;
    private AadhaarUserKycDto aadhaarUserKycDto;
    private TransactionDto transactionDto;
    private NotificationResponseDto notificationResponseDto;
    private AccountDto accountDto1;
    private HidPhrAddressDto hidPhrAddressDto;
    private AccountAuthMethodsDto authMethodsDto;
    private EnrolByAadhaarRequestDto enrolByAadhaarRequestDto;
    private AuthData authData;
    private Demographic demographic;
    private ConsentDto consentDto;
    private LgdDistrictResponse lgdDistrictResponse;
    private HidBenefitDto hidBenefitDto;
    private RedisOtp redisOtp;
    private EnrolByAadhaarResponseDto enrolByAadhaarResponseDto;
    private  VerifyDemographicResponse verifyDemographicResponse;

    private VerifyDLResponse verifyDLResponse;
    private IdentityDocumentsDto identityDocumentsDto;
    private EnrolByDocumentRequestDto enrolByDocumentRequestDto;
    private RequestHeaders requestHeaders;
    private List<LgdDistrictResponse> list;
    private IntegratedProgramDto integratedProgramDto;
    private List<IntegratedProgramDto> list1;

    @BeforeEach
    void setup()
    {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(enrolByDemographicService,"maxMobileLinkingCount",2);
        ReflectionTestUtils.setField(enrolByDemographicService,"isTransactionManagementEnable",true);
        ReflectionTestUtils.setField(enrolByDemographicValidatorService,"documentPhotoMinSizeLimit","1");
        ReflectionTestUtils.setField(enrolByDemographicValidatorService,"documentPhotoMaxSizeLimit","2");
        Demographic demographicTest= Demographic.builder().build();
        Demographic demographicTest2=new Demographic("","","","","","","","","",MobileType.WRONG,"","","","","","","","");
        transactionDto = new TransactionDto();
        notificationResponseDto = new NotificationResponseDto();
        accountDto1 = new AccountDto();
        hidPhrAddressDto = new HidPhrAddressDto();
        authMethodsDto = new AccountAuthMethodsDto();
        enrolByAadhaarRequestDto = new EnrolByAadhaarRequestDto();
        aadhaarResponseDto = new AadhaarResponseDto();
        authData = new AuthData();
        demographic = new Demographic();
        consentDto = new ConsentDto();
        aadhaarUserKycDto = new AadhaarUserKycDto();
        lgdDistrictResponse = new LgdDistrictResponse();
        redisOtp = new RedisOtp();
        verifyDLResponse = new VerifyDLResponse();
        identityDocumentsDto = new IdentityDocumentsDto();
        enrolByDocumentRequestDto = new EnrolByDocumentRequestDto();
        enrolByAadhaarResponseDto = new EnrolByAadhaarResponseDto();
        requestHeaders = new RequestHeaders();
        hidBenefitDto = new HidBenefitDto();
        verifyDemographicResponse = new VerifyDemographicResponse();
        verifyDemographicResponse.setVerified(true);
        verifyDemographicResponse.setReason("reasson");
        verifyDemographicResponse.setXmlUid("4567");
        lgdDistrictResponse.setDistrictCode("177");
        lgdDistrictResponse.setDistrictName("SAHARANPUR");
        lgdDistrictResponse.setStateCode("9");
        lgdDistrictResponse.setStateName("UTTAR PRADESH");
        lgdDistrictResponse.setDistrictCode("177");
        lgdDistrictResponse.setDistrictName("SAHARANPUR");
        lgdDistrictResponse.setStateCode("9");
        lgdDistrictResponse.setStateName("UTTAR PRADESH");
        list = new ArrayList<>();
        list.add(lgdDistrictResponse);
        hidPhrAddressDto.setHealthIdNumber("76-5245-8762-1574");
        hidPhrAddressDto.setPhrAddress("76524587621574@abdm");
        HidBenefitDto h = new HidBenefitDto(hidBenefitDto.getHidBenefitId(), hidBenefitDto.getProgramName(),hidBenefitDto.getBenefitName(),hidBenefitDto.getBenefitId(),hidBenefitDto.getStateCode(),hidBenefitDto.getValidTill(),hidBenefitDto.getLinkedDate(),hidBenefitDto.getLinkedBy(),hidBenefitDto.getHealthIdNumber(),hidBenefitDto.getMobileNumber(),hidBenefitDto.getStatus(),hidBenefitDto.getCreatedDate(),hidBenefitDto.getUpdatedDate(),hidBenefitDto.getCreatedBy(),hidBenefitDto.getUpdatedBy());
        hidBenefitDto.setHidBenefitId("123");
        accountDto1.setHealthIdNumber("123");
        accountDto1.setStatus(AccountStatus.ACTIVE.getValue());
        notificationResponseDto.setStatus("sent");
        demographic.setAadhaarNumber("VG3NkvoIjGIE6KbzN+4MQzhZRIq/a1LQF6UAP5+1SOP3+rB9Mgyw/U7jz/wWAVn+G+Xmlt3rBPJr37hxAc5jnw5eSEPdjAbGpyLa4Y69+RAL0T/B5D8SF1M1WHbmbm3xcCxCHaebLzloBqc2Ov8EMFr0eXKZVN8zPHLxzOVxiYsGDQIlK4C7+CwIZVpvl8k9zU8uINpb0LWpsUlzf8logoaapOqtvo63tYHAhL0ENiK7XSXuXtKH7OLmiLx17jlOHV2sMn5RxfPKs/sPVHjrcfDcgLYgBmZFmZNMjpT4cJlJEVrukMDtqZ9y2/MItFoipFTolWAq0drAJhXRw1UKGkIve3YCkYUNjOMYDeOPdjXLK0WmP7YBuVceYJ4iOh6EYZ1CJIhfX0s53P1z/ySKgrErcOWWhYI6HX7023mMgStYDJ6CdToYpwKup7st0cZu9KKyu9xDlfNE3xtv3Q5BWfcRyvq6busH9S83h2kaelYOS15nJ0jmr7qlMazlwsOB7yFv9xXm7o9VqKJUmCJdbpO/drPeOup7IJfdm2sOOvPxwftwU6ZuWES2fGpX9+ZbS6bLdSLgNL1PTIngZB3GJj4vo718EGzy0lu3NQomZqJ/ZG13Ky9sl0/dxfIHkI6wVYJTXi1qgWCcMla5uQwj46LOu+d7an6V3xcBN0/Djqk=");
        demographic.setAddress("test");
        demographic.setMobileType(MobileType.SELF);
        demographic.setDayOfBirth("01");
        demographic.setMonthOfBirth("09");
        demographic.setYearOfBirth("2000");
        demographic.setDistrict("test");
        demographic.setFirstName("Anchal");
        demographic.setGender("F");
        demographic.setLastName("Singh");
        demographic.setMobile("7493757643");
        demographic.setStateCode("1234");
        demographic.setValidity("");
        String validity = demographic.getValidity();
      //  demographic.setConsentFormImage("ing");
        demographic.setDistrictCode("123");
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.DEMO);
        authMethods.add(AuthMethods.DEMO_AUTH);
        authData.setAuthMethods(authMethods);
        authData.setDemographic(demographic);
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        enrolByAadhaarResponseDto.setMessage("message");
        accountDto1.setMobile("2343231234");
        integratedProgramDto= new IntegratedProgramDto();
        requestHeaders.setClientId("clientId");
        requestHeaders.setBenefitName("name");
        requestHeaders.setBenefitName("name");
        integratedProgramDto.setId("id");
        integratedProgramDto.setBenefitName(requestHeaders.getBenefitName());
        integratedProgramDto.setProgramName("programName");
        integratedProgramDto.setClientId("clientId");
        list1 = new ArrayList<>();
        list1.add(integratedProgramDto);



    }

    @AfterEach
    void tearDown()
    {
        notificationResponseDto = null;
        accountDto1 = null;
        hidPhrAddressDto = null;
        authMethodsDto = null;
        enrolByAadhaarRequestDto = null;
        authData = null;
        demographic = null;

        consentDto = null;
        aadhaarUserKycDto = null;
        lgdDistrictResponse = null;
        redisOtp = null;
        transactionDto = null;
        verifyDLResponse = null;
        identityDocumentsDto = null;
        enrolByDocumentRequestDto = null;
        requestHeaders = null;
        verifyDemographicResponse=null;
        enrolByAadhaarResponseDto=null;
        hidBenefitDto=null;
    }


    @Test
    void validateAndEnrolByDemoAuthSuccess()
    {IntegratedProgramDto integratedProgramDto= new IntegratedProgramDto();
        integratedProgramDto.setId("id");
        integratedProgramDto.setBenefitName(requestHeaders.getBenefitName());
        integratedProgramDto.setProgramName("programName");
        integratedProgramDto.setClientId("clientId");
        List<IntegratedProgramDto> list1 = new ArrayList<>();
        list1.add(integratedProgramDto);
        when(redisService.getIntegratedPrograms()).thenReturn(list1);
          when(accountService.getMobileLinkedAccountCount(any())).thenReturn(Mono.just(-1));
//        when(rsaUtils.decrypt(any())).thenReturn("806863997309");
          when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.just(accountDto1));
        when(abhaAddressGenerator.generateDefaultAbhaAddress(any())).thenReturn("76524587621574@abdm");
        when(deDuplicationService.checkDeDuplication(any())).thenReturn(Mono.empty());
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(Arrays.asList(lgdDistrictResponse)));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto1));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any())).thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(hidBenefitDBFClient.existByHealthIdAndBenefit(any(),any())).thenReturn(Mono.just(true));
        when(hidBenefitDBFClient.saveHidBenefit(any())).thenReturn(Mono.just(hidBenefitDto));
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(identityDocumentDBService.addIdentityDocuments(any())).thenReturn(Mono.just(identityDocumentsDto));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(Arrays.asList(authMethodsDto)));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());
        when(notificationService.sendABHACreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any())).thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any())).thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime()).thenReturn(1296000L);
        StepVerifier.create(enrolByDemographicService.validateAndEnrolByDemoAuth(enrolByAadhaarRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();


    }
    @Test
    void validateAndEnrolByDemoAuthSuccess2()
    {   demographic.setStateCode("9");
        when(accountService.getMobileLinkedAccountCount(any()))
                .thenReturn(Mono.just(-1));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto1));
        when(rsaUtils.decrypt(any()))
                .thenReturn("806863997309");
        when(aadhaarAppService.verifyDemographicDetails(any()))
                .thenReturn(Mono.just(verifyDemographicResponse));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.empty());
        when(abhaAddressGenerator.generateDefaultAbhaAddress(any()))
                .thenReturn("76524587621574@abdm");
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());
        accountDto1.setStatus(AccountStatus.DELETED.getValue());
        when(accountService.findByXmlUid(any())).thenReturn(Mono.just(accountDto1));

        when(lgdAppFClient.getByDistrictCode(any(),any(),any())).thenReturn(Mono.just(list));
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(list));
        when(lgdUtility.getDistrictCode(any())).thenReturn(Mono.just(list));
        when(accountService.createAccountEntity(any(),any(),any()))
                .thenReturn(Mono.just(accountDto1));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any()))
                .thenReturn(hidPhrAddressDto);
        IntegratedProgramDto integratedProgramDto= new IntegratedProgramDto();
        integratedProgramDto.setId("id");
        integratedProgramDto.setBenefitName(requestHeaders.getBenefitName());
        integratedProgramDto.setProgramName("programName");
        integratedProgramDto.setClientId("clientId");
        List<IntegratedProgramDto> list1 = new ArrayList<>();
        list1.add(integratedProgramDto);
        when(redisService.getIntegratedPrograms()).thenReturn(list1);
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("saved"));
        when(identityDocumentDBService.addIdentityDocuments(any()))
                .thenReturn(Mono.just(identityDocumentsDto));
        when(accountAuthMethodService.addAccountAuthMethods(any()))
                .thenReturn(Mono.just(Arrays.asList(authMethodsDto)));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(hidBenefitDBFClient.existByHealthIdAndBenefit(any(),any())).thenReturn(Mono.just(true));

        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any()))
                .thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any()))
                .thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime())
                .thenReturn(1296000L);
        when(enrolByDemographicValidatorService.isValidProfilePhoto(any())).thenReturn(true);
        //when(enrolByDemographicValidatorService.

        StepVerifier.create(enrolByDemographicService.validateAndEnrolByDemoAuth(enrolByAadhaarRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();


    }
    @Test
    void validateAndEnrolByDemoAuthSuccessNotificationGatewayUnavailableException()
    {   demographic.setStateCode("9");
        ArrayList<AuthMethods> authMethods2 = new ArrayList<>();
        authMethods2.add(AuthMethods.DEMO);
        authData.setAuthMethods(authMethods2);
        authData.setDemographic(demographic);
        notificationResponseDto.setStatus("esd");
        enrolByAadhaarRequestDto.setAuthData(authData);
        when(accountService.getMobileLinkedAccountCount(any()))
                .thenReturn(Mono.just(-1));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto1));
        when(rsaUtils.decrypt(any()))
                .thenReturn("806863997309");
        when(aadhaarAppService.verifyDemographicDetails(any()))
                .thenReturn(Mono.just(verifyDemographicResponse));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.empty());
        when(abhaAddressGenerator.generateDefaultAbhaAddress(any()))
                .thenReturn("76524587621574@abdm");
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());
        accountDto1.setStatus(AccountStatus.DELETED.getValue());
        when(accountService.findByXmlUid(any())).thenReturn(Mono.just(accountDto1));

        when(lgdAppFClient.getByDistrictCode(any(),any(),any())).thenReturn(Mono.just(list));
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(Arrays.asList()));
        when(lgdUtility.getDistrictCode(any())).thenReturn(Mono.just(list));
        when(accountService.createAccountEntity(any(),any(),any()))
                .thenReturn(Mono.just(accountDto1));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any()))
                .thenReturn(hidPhrAddressDto);
        IntegratedProgramDto integratedProgramDto= new IntegratedProgramDto();
        integratedProgramDto.setId("id");
        integratedProgramDto.setBenefitName(requestHeaders.getBenefitName());
        integratedProgramDto.setProgramName("programName");
        integratedProgramDto.setClientId("clientId");
        List<IntegratedProgramDto> list1 = new ArrayList<>();
        list1.add(integratedProgramDto);
        when(redisService.getIntegratedPrograms()).thenReturn(list1);
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("saved"));
        when(identityDocumentDBService.addIdentityDocuments(any()))
                .thenReturn(Mono.just(identityDocumentsDto));
        when(accountAuthMethodService.addAccountAuthMethods(any()))
                .thenReturn(Mono.just(Arrays.asList(authMethodsDto)));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(hidBenefitDBFClient.existByHealthIdAndBenefit(any(),any())).thenReturn(Mono.just(true));

        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any()))
                .thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any()))
                .thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime())
                .thenReturn(1296000L);
        when(enrolByDemographicValidatorService.isValidProfilePhoto(any())).thenReturn(true);
        //when(enrolByDemographicValidatorService.

        StepVerifier.create(enrolByDemographicService.validateAndEnrolByDemoAuth(enrolByAadhaarRequestDto,requestHeaders))
                .expectError(NotificationGatewayUnavailableException.class)
                .verify();


    }
    @Test
    void validateAndEnrolByDemoAuthBadRequestException()
    {
        demographic.setStateCode("");
        demographic.setDistrictCode("");
        demographic.setMobile("917839929929");
        ArrayList<AuthMethods> authMethods2 = new ArrayList<>();
        authMethods2.add(AuthMethods.DEMO);
        authData.setAuthMethods(authMethods2);
        authData.setDemographic(demographic);
        notificationResponseDto.setStatus("esd");
        enrolByAadhaarRequestDto.setAuthData(authData);
        when(accountService.getMobileLinkedAccountCount(any()))
                .thenReturn(Mono.just(-1));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto1));
        when(rsaUtils.decrypt(any()))
                .thenReturn("806863997309");
        when(aadhaarAppService.verifyDemographicDetails(any()))
                .thenReturn(Mono.just(verifyDemographicResponse));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.empty());
        when(abhaAddressGenerator.generateDefaultAbhaAddress(any()))
                .thenReturn("76524587621574@abdm");
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());
        accountDto1.setStatus(AccountStatus.DELETED.getValue());
        when(accountService.findByXmlUid(any())).thenReturn(Mono.just(accountDto1));

        when(lgdAppFClient.getByDistrictCode(any(),any(),any())).thenReturn(Mono.just(list));
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(Arrays.asList()));
        when(lgdUtility.getDistrictCode(any())).thenReturn(Mono.just(list));
        when(accountService.createAccountEntity(any(),any(),any()))
                .thenReturn(Mono.just(accountDto1));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any()))
                .thenReturn(hidPhrAddressDto);
        IntegratedProgramDto integratedProgramDto= new IntegratedProgramDto();
        integratedProgramDto.setId("id");
        integratedProgramDto.setBenefitName(requestHeaders.getBenefitName());
        integratedProgramDto.setProgramName("programName");
        integratedProgramDto.setClientId("clientId");
        List<IntegratedProgramDto> list1 = new ArrayList<>();
        list1.add(integratedProgramDto);
        when(redisService.getIntegratedPrograms()).thenReturn(list1);
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("saved"));
        when(identityDocumentDBService.addIdentityDocuments(any()))
                .thenReturn(Mono.just(identityDocumentsDto));
        when(accountAuthMethodService.addAccountAuthMethods(any()))
                .thenReturn(Mono.just(Arrays.asList(authMethodsDto)));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(hidBenefitDBFClient.existByHealthIdAndBenefit(any(),any())).thenReturn(Mono.just(true));

        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any()))
                .thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any()))
                .thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime())
                .thenReturn(1296000L);
        when(enrolByDemographicValidatorService.isValidProfilePhoto(any())).thenReturn(true);
        //when(enrolByDemographicValidatorService.

        StepVerifier.create(enrolByDemographicService.validateAndEnrolByDemoAuth(enrolByAadhaarRequestDto,requestHeaders))
                .expectError(BadRequestException.class)
                .verify();
        demographic.setMobile("+1917839929929");
        ReflectionTestUtils.setField(enrolByDemographicService,"maxMobileLinkingCount",0);
        when(accountService.getMobileLinkedAccountCount(any()))
                .thenReturn(Mono.just(1));
        StepVerifier.create(enrolByDemographicService.validateAndEnrolByDemoAuth(enrolByAadhaarRequestDto,requestHeaders))
                .expectError(AbhaUnProcessableException.class)
                .verify();

    }
    @Test
    void validateAndEnrolByDemoAuthAbhaUnProcessableException()
    {   demographic.setStateCode("");
        demographic.setMobile("+917839929929");
        verifyDemographicResponse.setVerified(false);
        demographic.setDistrictCode("");
        ArrayList<AuthMethods> authMethods2 = new ArrayList<>();
        authMethods2.add(AuthMethods.DEMO);
        authData.setAuthMethods(authMethods2);
        authData.setDemographic(demographic);
        notificationResponseDto.setStatus("esd");
        enrolByAadhaarRequestDto.setAuthData(authData);
        when(accountService.getMobileLinkedAccountCount(any()))
                .thenReturn(Mono.just(-1));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto1));
        when(rsaUtils.decrypt(any()))
                .thenReturn("806863997309");
        when(aadhaarAppService.verifyDemographicDetails(any()))
                .thenReturn(Mono.just(verifyDemographicResponse));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.empty());
        when(abhaAddressGenerator.generateDefaultAbhaAddress(any()))
                .thenReturn("76524587621574@abdm");
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());
        accountDto1.setStatus(AccountStatus.DELETED.getValue());
        when(accountService.findByXmlUid(any())).thenReturn(Mono.just(accountDto1));

        when(lgdAppFClient.getByDistrictCode(any(),any(),any())).thenReturn(Mono.just(list));
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(Arrays.asList()));
        when(lgdUtility.getDistrictCode(any())).thenReturn(Mono.just(list));
        when(accountService.createAccountEntity(any(),any(),any()))
                .thenReturn(Mono.just(accountDto1));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any()))
                .thenReturn(hidPhrAddressDto);
        IntegratedProgramDto integratedProgramDto= new IntegratedProgramDto();
        integratedProgramDto.setId("id");
        integratedProgramDto.setBenefitName(requestHeaders.getBenefitName());
        integratedProgramDto.setProgramName("programName");
        integratedProgramDto.setClientId("clientId");
        List<IntegratedProgramDto> list1 = new ArrayList<>();
        list1.add(integratedProgramDto);
        when(redisService.getIntegratedPrograms()).thenReturn(list1);
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("saved"));
        when(identityDocumentDBService.addIdentityDocuments(any()))
                .thenReturn(Mono.just(identityDocumentsDto));
        when(accountAuthMethodService.addAccountAuthMethods(any()))
                .thenReturn(Mono.just(Arrays.asList(authMethodsDto)));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(hidBenefitDBFClient.existByHealthIdAndBenefit(any(),any())).thenReturn(Mono.just(true));

        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any()))
                .thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any()))
                .thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime())
                .thenReturn(1296000L);
        when(enrolByDemographicValidatorService.isValidProfilePhoto(any())).thenReturn(true);
        //when(enrolByDemographicValidatorService.

        StepVerifier.create(enrolByDemographicService.validateAndEnrolByDemoAuth(enrolByAadhaarRequestDto,requestHeaders))
                .expectError(AbhaUnProcessableException.class)
                .verify();


    }


    @Test
    void validateAndEnrolByDemoAuthSuccessbreakName2()
    {   demographic.setStateCode("9");
        demographic.setMobile(null);
        demographic.setFirstName("AN ana nan");
        when(accountService.getMobileLinkedAccountCount(any()))
                .thenReturn(Mono.just(-1));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto1));
        when(rsaUtils.decrypt(any()))
                .thenReturn("806863997309");
        when(aadhaarAppService.verifyDemographicDetails(any()))
                .thenReturn(Mono.just(verifyDemographicResponse));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.empty());
        when(abhaAddressGenerator.generateDefaultAbhaAddress(any()))
                .thenReturn("76524587621574@abdm");
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());
        accountDto1.setStatus(AccountStatus.DELETED.getValue());
        when(accountService.findByXmlUid(any())).thenReturn(Mono.just(accountDto1));

        when(lgdAppFClient.getByDistrictCode(any(),any(),any())).thenReturn(Mono.just(list));
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(list));
        IntegratedProgramDto integratedProgramDto= new IntegratedProgramDto();
        integratedProgramDto.setId("id");
        integratedProgramDto.setBenefitName(requestHeaders.getBenefitName());
        integratedProgramDto.setProgramName("programName");
        integratedProgramDto.setClientId("clientId");
        List<IntegratedProgramDto> list1 = new ArrayList<>();
        list1.add(integratedProgramDto);
        when(redisService.getIntegratedPrograms()).thenReturn(list1);
        when(lgdUtility.getDistrictCode(any())).thenReturn(Mono.just(list));
        when(accountService.createAccountEntity(any(),any(),any()))
                .thenReturn(Mono.just(accountDto1));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any()))
                .thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("saved"));
        when(identityDocumentDBService.addIdentityDocuments(any()))
                .thenReturn(Mono.just(identityDocumentsDto));
        when(accountAuthMethodService.addAccountAuthMethods(any()))
                .thenReturn(Mono.just(Arrays.asList(authMethodsDto)));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(hidBenefitDBFClient.existByHealthIdAndBenefit(any(),any())).thenReturn(Mono.just(true));

        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any()))
                .thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any()))
                .thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime())
                .thenReturn(1296000L);
        when(enrolByDemographicValidatorService.isValidProfilePhoto(any())).thenReturn(true);
        //when(enrolByDemographicValidatorService.

        StepVerifier.create(enrolByDemographicService.validateAndEnrolByDemoAuth(enrolByAadhaarRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
        demographic.setFirstName("AN ana");
        StepVerifier.create(enrolByDemographicService.validateAndEnrolByDemoAuth(enrolByAadhaarRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();


    }
    @Test
    void validateAndEnrolByDemoAuthError2()
    {   demographic.setStateCode("9");
        demographic.setFirstName("AN ana nan");
        accountDto1.setHealthIdNumber("");
        when(accountService.getMobileLinkedAccountCount(any()))
                .thenReturn(Mono.just(-1));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto1));
        when(rsaUtils.decrypt(any()))
                .thenReturn("806863997309");
        when(aadhaarAppService.verifyDemographicDetails(any()))
                .thenReturn(Mono.just(verifyDemographicResponse));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.empty());
        when(abhaAddressGenerator.generateDefaultAbhaAddress(any()))
                .thenReturn("76524587621574@abdm");
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());
        accountDto1.setStatus(AccountStatus.DELETED.getValue());
        when(accountService.findByXmlUid(any())).thenReturn(Mono.just(accountDto1));

        when(lgdAppFClient.getByDistrictCode(any(),any(),any())).thenReturn(Mono.just(list));
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(list));
        IntegratedProgramDto integratedProgramDto= new IntegratedProgramDto();
        integratedProgramDto.setId("id");
        integratedProgramDto.setBenefitName(requestHeaders.getBenefitName());
        integratedProgramDto.setProgramName("programName");
        integratedProgramDto.setClientId("clientId");
        List<IntegratedProgramDto> list1 = new ArrayList<>();
        list1.add(integratedProgramDto);
        when(redisService.getIntegratedPrograms()).thenReturn(list1);
        when(lgdUtility.getDistrictCode(any())).thenReturn(Mono.just(list));
        when(accountService.createAccountEntity(any(),any(),any()))
                .thenReturn(Mono.just(accountDto1));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any()))
                .thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("saved"));
        when(identityDocumentDBService.addIdentityDocuments(any()))
                .thenReturn(Mono.just(identityDocumentsDto));
        when(accountAuthMethodService.addAccountAuthMethods(any()))
                .thenReturn(Mono.just(Arrays.asList(authMethodsDto)));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(hidBenefitDBFClient.existByHealthIdAndBenefit(any(),any())).thenReturn(Mono.just(true));

        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any()))
                .thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any()))
                .thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime())
                .thenReturn(1296000L);
        when(enrolByDemographicValidatorService.isValidProfilePhoto(any())).thenReturn(true);
        //when(enrolByDemographicValidatorService.

        StepVerifier.create(enrolByDemographicService.validateAndEnrolByDemoAuth(enrolByAadhaarRequestDto,requestHeaders))
                .expectError(AbhaDBGatewayUnavailableException.class)
                .verify();


    }

    @Test
    void validateAndEnrolByDemoAuthError()
    {
        when(accountService.getMobileLinkedAccountCount(any()))
                .thenReturn(Mono.just(-1));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto1));
        when(rsaUtils.decrypt(any()))
                .thenReturn("806863997309");
        when(aadhaarAppService.verifyDemographicDetails(any()))
                .thenReturn(Mono.just(verifyDemographicResponse));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.empty());
        when(abhaAddressGenerator.generateDefaultAbhaAddress(any()))
                .thenReturn("76524587621574@abdm");
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());
        accountDto1.setStatus(AccountStatus.DELETED.getValue());
        when(accountService.findByXmlUid(any())).thenReturn(Mono.just(accountDto1));

        when(lgdAppFClient.getByDistrictCode(any(),any(),any())).thenReturn(Mono.just(list));
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(list));
        when(lgdUtility.getDistrictCode(any())).thenReturn(Mono.just(list));
        when(accountService.createAccountEntity(any(),any(),any()))
                .thenReturn(Mono.just(accountDto1));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any()))
                .thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(identityDocumentDBService.addIdentityDocuments(any()))
                .thenReturn(Mono.just(identityDocumentsDto));
        when(accountAuthMethodService.addAccountAuthMethods(any()))
                .thenReturn(Mono.just(Arrays.asList(authMethodsDto)));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(hidBenefitDBFClient.existByHealthIdAndBenefit(any(),any())).thenReturn(Mono.just(true));
        IntegratedProgramDto integratedProgramDto= new IntegratedProgramDto();
        integratedProgramDto.setId("id");
        integratedProgramDto.setBenefitName(requestHeaders.getBenefitName());
        integratedProgramDto.setProgramName("programName");
        integratedProgramDto.setClientId("clientId");
        list1 = new ArrayList<>();
        list1.add(integratedProgramDto);
        when(redisService.getIntegratedPrograms()).thenReturn(list1);
        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any()))
                .thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any()))
                .thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime())
                .thenReturn(1296000L);
        when(enrolByDemographicValidatorService.isValidProfilePhoto(any())).thenReturn(true);
        //when(enrolByDemographicValidatorService.

        StepVerifier.create(enrolByDemographicService.validateAndEnrolByDemoAuth(enrolByAadhaarRequestDto,requestHeaders))
                .expectError(BadRequestException.class)
                .verify();


    }
    @Test
    void validateAndEnrolByDemoAuthSuccess3()
    {AccountDto accountDto1=new AccountDto();
        when(accountService.getMobileLinkedAccountCount(any()))
                .thenReturn(Mono.just(-1));
        when(rsaUtils.decrypt(any()))
                .thenReturn("806863997309");
        when(aadhaarAppService.verifyDemographicDetails(any()))
                .thenReturn(Mono.just(verifyDemographicResponse));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.just(accountDto1));
        when(abhaAddressGenerator.generateDefaultAbhaAddress(any()))
                .thenReturn("76524587621574@abdm");
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());
        accountDto1.setStatus(AccountStatus.DEACTIVATED.getValue());
        when(accountService.findByXmlUid(any())).thenReturn(Mono.just(accountDto1));
        when(lgdAppFClient.getByDistrictCode(any(),any(),any())).thenReturn(Mono.just(list));
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(list));
        when(lgdUtility.getDistrictCode(any())).thenReturn(Mono.just(list));
        when(accountService.createAccountEntity(any(),any(),any()))
                .thenReturn(Mono.just(accountDto1));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());

        when(redisService.getIntegratedPrograms()).thenReturn(list1);
        when(hidPhrAddressService.prepareNewHidPhrAddress(any()))
                .thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(identityDocumentDBService.addIdentityDocuments(any()))
                .thenReturn(Mono.just(identityDocumentsDto));
        when(accountAuthMethodService.addAccountAuthMethods(any()))
                .thenReturn(Mono.just(Arrays.asList(authMethodsDto)));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(hidBenefitDBFClient.existByHealthIdAndBenefit(any(),any())).thenReturn(Mono.just(false));

        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any()))
                .thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any()))
                .thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime())
                .thenReturn(1296000L);

        StepVerifier.create(enrolByDemographicService.validateAndEnrolByDemoAuth(enrolByAadhaarRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();


    }
    @Test
    void validateAndEnrolByDemoAuthSuccess4()
    {AccountDto accountDto1=new AccountDto();
        when(accountService.getMobileLinkedAccountCount(any()))
                .thenReturn(Mono.just(-1));
        when(rsaUtils.decrypt(any()))
                .thenReturn("806863997309");
        when(aadhaarAppService.verifyDemographicDetails(any()))
                .thenReturn(Mono.just(verifyDemographicResponse));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.just(accountDto1));
        when(abhaAddressGenerator.generateDefaultAbhaAddress(any()))
                .thenReturn("76524587621574@abdm");
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());
        accountDto1.setStatus(AccountStatus.DEACTIVATED.getValue());
        when(accountService.findByXmlUid(any())).thenReturn(Mono.just(accountDto1));
        when(lgdAppFClient.getByDistrictCode(any(),any(),any())).thenReturn(Mono.just(list));
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(list));
        when(lgdUtility.getDistrictCode(any())).thenReturn(Mono.just(list));
        when(accountService.createAccountEntity(any(),any(),any()))
                .thenReturn(Mono.just(accountDto1));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.error(Exception::new));

        when(redisService.getIntegratedPrograms()).thenReturn(list1);
        when(hidPhrAddressService.prepareNewHidPhrAddress(any()))
                .thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(identityDocumentDBService.addIdentityDocuments(any()))
                .thenReturn(Mono.just(identityDocumentsDto));
        when(accountAuthMethodService.addAccountAuthMethods(any()))
                .thenReturn(Mono.just(Arrays.asList(authMethodsDto)));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(hidBenefitDBFClient.existByHealthIdAndBenefit(any(),any())).thenReturn(Mono.just(false));

        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any()))
                .thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any()))
                .thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime())
                .thenReturn(1296000L);

        StepVerifier.create(enrolByDemographicService.validateAndEnrolByDemoAuth(enrolByAadhaarRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();


    }
}

