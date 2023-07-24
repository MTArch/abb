package in.gov.abdm.abha.enrollment.aadhaar.send_otp;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.MobileType;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarUserKycDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicResponse;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.demographic.Demographic;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.*;
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
import in.gov.abdm.abha.enrollment.services.notification.NotificationService;
import in.gov.abdm.abha.enrollment.utilities.LgdUtility;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.ArrayList;
import java.util.Arrays;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class EnrolByDemographicServiceTests {

    public static final String TOKEN = "eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiI5MS0zODY0LTQ3MzUtNzc2OCIsImNsaWVudElkIjoiYWJoYS1wcm9maWxlLWFwcC1hcGkiLCJzeXN0ZW0iOiJBQkhBLU4iLCJtb2JpbGUiOiI3MDM3MjQ4NTEwIiwiYWJoYU51bWJlciI6IjkxLTM4NjQtNDczNS03NzY4IiwicHJlZmVycmVkQWJoYUFkZHJlc3MiOiI5MTM4NjQ0NzM1Nzc2OEBhYmRtIiwidHlwIjoiVHJhbnNhY3Rpb24iLCJleHAiOjE2ODAwODEyMDAsImlhdCI6MTY4MDA3OTQwMCwidHhuSWQiOiJiMzU5YjQ1OC1kYTBmLTQ0NjMtOTg5MC1mNGRhNGUzMjEyNjcifQ.RhofHMB7mJPXQLggZFMNc52Li7cA8fO_yI8WAyzpwRdKihuEMOJ6AE7uBI27vRr1iHr6mTMvjzc5eM9Izw9zmAGaEcuJQu6RSznCBNRHIs-dkQwHPtgKw4ICKdX6WdiOvCzaO9a4qYxoyeDRVvU5nZ4-4QFEYNJtDUaLBIKJEbXDtzr1pq9irxCczo9-99ZYeIzxduE_sTCNyCUi2MaAj2Bo0Ij4Qs555jJ9eDOrpLG2BsYHsrkEltN7_o7gm4DFd9uIWSzcPVRQZmuk4NlynpE5LXW1QUxZrg6hxhnbJWNw_E6fmDgXigyPrwT1UdPTrERCC7FjxBUVvpYcSftdQY0aVBqMooIwRfWC2Oqy-0F6wHDegotnyxsCSE_1QVR5QwgJF-16745Eq8yQM0WgJS8FeJ9i5ah-HpAVuZpFqYCHm520uKNFHzSAsidrTRUhJTbmwAVv0LSqjEL0I1Thp300e1W04owuobE2JVQr42eKIElAbFdcXO3XsaVJ5hbQO1i_pGSNqxywStNIVtfAhhBChJ-aOPrPmU55ZDyqkjwULbL35kb2Ai_QGEm7ie2HdEcAHusZyqqrzvDfmFY9lcw7RFITvJ_V4rp2b9Rx9UoQKQ5f1nn4Y9rnhdALVzqBh7Wjdv-OsN-zHQth-Vu79XhzTvNfB14iGf0D2RQGBIY";
    public static final String REFRESH_TOKEN = "eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiI5MS0zODY0LTQ3MzUtNzc2OCIsImNsaWVudElkIjoiYWJoYS1wcm9maWxlLWFwcC1hcGkiLCJzeXN0ZW0iOiJBQkhBLU4iLCJ0eXAiOiJSZWZyZXNoIiwiZXhwIjoxNjgxMzc1NDAxLCJpYXQiOjE2ODAwNzk0MDF9.TgXvxiZLYAzhcphDgQjWwdpyvg01pZ9ANbdrKIsdgw55ZMuEe_K2JVNs9ynLWDKJ54IPmNuHvFQS6Wvs17sMiYCCnEaUzkpn86-xM4O5TZHkGAi6WhdGTsYYIQZghqYBpH89Y88AyIYV3jZEUI89bjVlziK9nNKxTQLSRWpZSw42bPLbi8CSHv14H9ozlJGoRsjbXSUJrArw52yGwmWkOs8rQROrIaftqQjkmucTvoptXe80K6PRiL9tT2sY739iAigAj-6ffinlIis_6goNQ5aAJFUsAE7c7aVKMdW686pp0aILiDJYyfSdpzCXvj3ihBlBImhcyDlj38b4PGWUSNVkSCyOvl_pCmGjYF3lUgsqejYb_7_nYS9su0HlVKRCkeTBnA5JvomNOCwOfRaK7wuCoZU5P1Na--dYUjRGZV1M2r-hUzj7lJO2s0JxiY37BCmZuJXnzVdKVcqcWk2OyICHHJE8SfIvPh2HW3IUF-jFhDWSHP2a53UbiDPQK-S9bJkVFnsGm6OO4WB2C-Yd1TzcguYgf9If8GcRpion6KZB05luFvUv6Z-ymcDtSd_KE_IPD4tWhiapWgkVSZlhAS34F1DmlZr1oj8LBjbOrdiZz95BsiANMpT_CW1QWnEHNrq9w5jJcwC51Ityw_MoBNK9XwZ9CDyGtuTmIMCTgfI";

    @InjectMocks
    EnrolByDemographicService enrolByDemographicService;

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

    private AadhaarResponseDto aadhaarResponseDto;
    private AadhaarUserKycDto aadhaarUserKycDto;
    private TransactionDto transactionDto;
    private NotificationResponseDto notificationResponseDto;
    private AccountDto accountDto;
    private HidPhrAddressDto hidPhrAddressDto;
    private AccountAuthMethodsDto authMethodsDto;
    private EnrolByAadhaarRequestDto enrolByAadhaarRequestDto;
    private AuthData authData;
    private Demographic demographic;
    private ConsentDto consentDto;
    private LgdDistrictResponse lgdDistrictResponse;
    private RedisOtp redisOtp;

    private VerifyDLResponse verifyDLResponse;
    private IdentityDocumentsDto identityDocumentsDto;
    private EnrolByDocumentRequestDto enrolByDocumentRequestDto;
    private RequestHeaders requestHeaders;

    @BeforeEach
    void setup()
    {
        MockitoAnnotations.openMocks(this);
        transactionDto = new TransactionDto();
        notificationResponseDto = new NotificationResponseDto();
        accountDto = new AccountDto();
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
        enrolByDocumentRequestDto = new EnrolByDocumentRequestDto() ;
        requestHeaders = new RequestHeaders();
    }

    @AfterEach
    void tearDown()
    {
        notificationResponseDto = null;
        accountDto = null;
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
    }


    @Test
    void validateAndEnrolByDemoAuthSuccess()
    {
        when(accountService.getMobileLinkedAccountCount(any()))
                .thenReturn(Mono.just(-1));
        when(rsaUtils.decrypt(any()))
                .thenReturn("806863997309");
        VerifyDemographicResponse verifyDemographicResponse = new VerifyDemographicResponse();
        verifyDemographicResponse.setVerified(true);
        when(aadhaarAppService.verifyDemographicDetails(any()))
                .thenReturn(Mono.just(verifyDemographicResponse));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.empty());
        when(abhaAddressGenerator.generateDefaultAbhaAddress(any()))
                .thenReturn("76524587621574@abdm");
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());
        lgdDistrictResponse.setDistrictCode("177");
        lgdDistrictResponse.setDistrictName("SAHARANPUR");
        lgdDistrictResponse.setStateCode("9");
        lgdDistrictResponse.setStateName("UTTAR PRADESH");
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(Arrays.asList(lgdDistrictResponse)));
        when(accountService.createAccountEntity(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        hidPhrAddressDto.setHealthIdNumber("76-5245-8762-1574");
        hidPhrAddressDto.setPhrAddress("76524587621574@abdm");
        when(hidPhrAddressService.prepareNewHidPhrAddress(any()))
                .thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(identityDocumentDBService.addIdentityDocuments(any()))
                .thenReturn(Mono.just(identityDocumentsDto));
        when(accountAuthMethodService.addAccountAuthMethods(any()))
                .thenReturn(Mono.just(Arrays.asList(authMethodsDto)));
        notificationResponseDto.setStatus("sent");
        when(notificationService.sendRegistrationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any()))
                .thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any()))
                .thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime())
                .thenReturn(1296000L);

        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.DEMO);
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
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        authData.setAuthMethods(authMethods);
        authData.setDemographic(demographic);
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        StepVerifier.create(enrolByDemographicService.validateAndEnrolByDemoAuth(enrolByAadhaarRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();


    }
}
