package in.gov.abdm.abha.enrollment.aadhaar.send_otp;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarUserKycDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.*;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentRequestDto;
import in.gov.abdm.abha.enrollment.model.entities.*;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import in.gov.abdm.abha.enrollment.model.nepix.VerifyDLResponse;
import in.gov.abdm.abha.enrollment.model.notification.NotificationResponseDto;
import in.gov.abdm.abha.enrollment.model.redis.otp.RedisOtp;
import in.gov.abdm.abha.enrollment.services.aadhaar.impl.AadhaarAppServiceImpl;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.account_auth_methods.AccountAuthMethodService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.de_duplication.DeDuplicationService;
import in.gov.abdm.abha.enrollment.services.document.DocumentAppService;
import in.gov.abdm.abha.enrollment.services.document.IdentityDocumentDBService;
import in.gov.abdm.abha.enrollment.services.document.impl.IdentityDocumentDBServiceImpl;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.impl.EnrolUsingAadhaarServiceImpl;
import in.gov.abdm.abha.enrollment.services.enrol.document.EnrolUsingDrivingLicence;
import in.gov.abdm.abha.enrollment.services.notification.NotificationService;
import in.gov.abdm.abha.enrollment.services.redis.RedisService;
import in.gov.abdm.abha.enrollment.utilities.EnrolmentCipher;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class EnrolServiceTests {
    public static final String AADHAAR_NUMBER = "QYhr7tdzsYyYVfFjnv/fRApJBixLC2xt1Xv1Sk/AU1SQfmagNEyamwg6o3/kiD9cKuyG3D1M1aNvVMgBCLYnuKhglQgnivzcIVVR3icwxjlJp/7gOEEB76OJzl7EG8AyqB1omC4KpgcAPjq/436t1150Mn4sDrbomJVgYZbDECD2808cxJ8ygs3iv0n/FQldvaU3bdDkKnHAE/XMnxVm7KGzn7/XI7ylxvGNmnVfwzfdnFlHnaL0oFbs0X9G9hDhI370BclD22qo8yh5y4jhbYYShtBKdB9E93CWt94Cx4y43fNAjAJJq2caMvR3M1vxFumTkc/Gn2IAtPxWMHi/e8qloF0oGO3I+j2ktWTRR6qbh7JyigcQLdOg8QPa95cpn4AyiRgXh0KyACtYFiakI3T1PsVx+GO/RrzuWIX5eYBe4TRbvD7DfuXJFrrxOhn8NOmTVOTf6OgYhAtL9OiLNLeplL1MFhZmnyqNJIAfyKZvKlQfFFMtugRZBJVmzTzI1qit1hAODI/X+0o4NMllfEMeFW6GWLnBCjl3ZM37+IS8YXLwsTYHvVd0s8zvPI6MohEdM6GyqpGnXyBYSTjblE3RG+pFvdovhRbH74yem1IdQKmFHWAoJXgWeCy9BQfBxv1SCI13IrpuvZhJ9I+/9SUX/Sl1gpg8iFHMXTqFEns=";
    public static final String TOKEN = "eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiI5MS0zODY0LTQ3MzUtNzc2OCIsImNsaWVudElkIjoiYWJoYS1wcm9maWxlLWFwcC1hcGkiLCJzeXN0ZW0iOiJBQkhBLU4iLCJtb2JpbGUiOiI3MDM3MjQ4NTEwIiwiYWJoYU51bWJlciI6IjkxLTM4NjQtNDczNS03NzY4IiwicHJlZmVycmVkQWJoYUFkZHJlc3MiOiI5MTM4NjQ0NzM1Nzc2OEBhYmRtIiwidHlwIjoiVHJhbnNhY3Rpb24iLCJleHAiOjE2ODAwODEyMDAsImlhdCI6MTY4MDA3OTQwMCwidHhuSWQiOiJiMzU5YjQ1OC1kYTBmLTQ0NjMtOTg5MC1mNGRhNGUzMjEyNjcifQ.RhofHMB7mJPXQLggZFMNc52Li7cA8fO_yI8WAyzpwRdKihuEMOJ6AE7uBI27vRr1iHr6mTMvjzc5eM9Izw9zmAGaEcuJQu6RSznCBNRHIs-dkQwHPtgKw4ICKdX6WdiOvCzaO9a4qYxoyeDRVvU5nZ4-4QFEYNJtDUaLBIKJEbXDtzr1pq9irxCczo9-99ZYeIzxduE_sTCNyCUi2MaAj2Bo0Ij4Qs555jJ9eDOrpLG2BsYHsrkEltN7_o7gm4DFd9uIWSzcPVRQZmuk4NlynpE5LXW1QUxZrg6hxhnbJWNw_E6fmDgXigyPrwT1UdPTrERCC7FjxBUVvpYcSftdQY0aVBqMooIwRfWC2Oqy-0F6wHDegotnyxsCSE_1QVR5QwgJF-16745Eq8yQM0WgJS8FeJ9i5ah-HpAVuZpFqYCHm520uKNFHzSAsidrTRUhJTbmwAVv0LSqjEL0I1Thp300e1W04owuobE2JVQr42eKIElAbFdcXO3XsaVJ5hbQO1i_pGSNqxywStNIVtfAhhBChJ-aOPrPmU55ZDyqkjwULbL35kb2Ai_QGEm7ie2HdEcAHusZyqqrzvDfmFY9lcw7RFITvJ_V4rp2b9Rx9UoQKQ5f1nn4Y9rnhdALVzqBh7Wjdv-OsN-zHQth-Vu79XhzTvNfB14iGf0D2RQGBIY";
    public static final String REFRESH_TOKEN = "eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiI5MS0zODY0LTQ3MzUtNzc2OCIsImNsaWVudElkIjoiYWJoYS1wcm9maWxlLWFwcC1hcGkiLCJzeXN0ZW0iOiJBQkhBLU4iLCJ0eXAiOiJSZWZyZXNoIiwiZXhwIjoxNjgxMzc1NDAxLCJpYXQiOjE2ODAwNzk0MDF9.TgXvxiZLYAzhcphDgQjWwdpyvg01pZ9ANbdrKIsdgw55ZMuEe_K2JVNs9ynLWDKJ54IPmNuHvFQS6Wvs17sMiYCCnEaUzkpn86-xM4O5TZHkGAi6WhdGTsYYIQZghqYBpH89Y88AyIYV3jZEUI89bjVlziK9nNKxTQLSRWpZSw42bPLbi8CSHv14H9ozlJGoRsjbXSUJrArw52yGwmWkOs8rQROrIaftqQjkmucTvoptXe80K6PRiL9tT2sY739iAigAj-6ffinlIis_6goNQ5aAJFUsAE7c7aVKMdW686pp0aILiDJYyfSdpzCXvj3ihBlBImhcyDlj38b4PGWUSNVkSCyOvl_pCmGjYF3lUgsqejYb_7_nYS9su0HlVKRCkeTBnA5JvomNOCwOfRaK7wuCoZU5P1Na--dYUjRGZV1M2r-hUzj7lJO2s0JxiY37BCmZuJXnzVdKVcqcWk2OyICHHJE8SfIvPh2HW3IUF-jFhDWSHP2a53UbiDPQK-S9bJkVFnsGm6OO4WB2C-Yd1TzcguYgf9If8GcRpion6KZB05luFvUv6Z-ymcDtSd_KE_IPD4tWhiapWgkVSZlhAS34F1DmlZr1oj8LBjbOrdiZz95BsiANMpT_CW1QWnEHNrq9w5jJcwC51Ityw_MoBNK9XwZ9CDyGtuTmIMCTgfI";
    public static final String PID = "QYhr7tdzsYyYVfFjnv/fRApJBixLC2xt1Xv1Sk/AU1SQfmagNhgsftedo3/kiD9cKuyG3D1M1aNvVMgBCLYnuKhglQgnivzcIVVR3icwxjlJp/7gOEEB76OJzl7EG8AyqB1omC4KpgcAPjq/436t1150Mn4sDrbomJVgYZbDECD2808cxJ8ygs3iv0n/FQldvaU3bdDkKnHAE/XMnxVm7KGzn7/XI7ylxvGNmnVfwzfdnFlHnaL0oFbs0X9G9hDhI370BclD22qo8yh5y4jhbYYShtBKdB9E93CWt94Cx4y43fNAjAJJq2caMvR3M1vxFumTkc/Gn2IAtPxWMHi/e8qloF0oGO3I+j2ktWTRR6qbh7JyigcQLdOg8QPa95cpn4AyiRgXh0KyACtYFiakI3T1PsVx+GO/RrzuWIX5eYBe4TRbvD7DfuXJFrrxOhn8NOmTVOTf6OgYhAtL9OiLNLeplL1MFhZmnyqNJIAfyKZvKlQfFFMtugRZBJVmzTzI1qit1hAODI/X+0o4NMllfEMeFW6GWLnBCjl3ZM37+IS8YXLwsTYHvVd0s8zvPI6MohEdM6GyqpGnXyBYSTjblE3RG+pFvdovhRbH74yem1IdQKmFHWAoJXgWeCy9BQfBxv1SCI13IrpuvZhJ9I+/9SUX/Sl1gpg8iFHMXTqFEns=";

    @InjectMocks
    EnrolUsingAadhaarServiceImpl enrolUsingAadhaarService;
    @InjectMocks
    EnrolUsingDrivingLicence enrolUsingDrivingLicence;
    @Mock
    RedisService redisService;
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
    DocumentAppService documentAppService;
    @Mock
    IdentityDocumentDBServiceImpl identityDocumentDBService;
    @Mock
    EnrolmentCipher enrolmentCipher;
    private AadhaarResponseDto aadhaarResponseDto;
    private AadhaarUserKycDto aadhaarUserKycDto;
    private TransactionDto transactionDto;
    private NotificationResponseDto notificationResponseDto;
    private AccountDto accountDto;
    private HidPhrAddressDto hidPhrAddressDto;
    private AccountAuthMethodsDto authMethodsDto;
    private EnrolByAadhaarRequestDto enrolByAadhaarRequestDto;
    private AuthData authData;
    private OtpDto otpDto;
    private ConsentDto consentDto;
    private LgdDistrictResponse lgdDistrictResponse;
    private RedisOtp redisOtp;
    private VerifyDLResponse verifyDLResponse;
    private IdentityDocumentsDto identityDocumentsDto;
    private EnrolByDocumentRequestDto enrolByDocumentRequestDto;
    private FaceDto faceDto;

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
        otpDto = new OtpDto();
        consentDto = new ConsentDto();
        aadhaarUserKycDto = new AadhaarUserKycDto();
        lgdDistrictResponse = new LgdDistrictResponse();
        redisOtp = new RedisOtp();
        verifyDLResponse = new VerifyDLResponse();
        identityDocumentsDto = new IdentityDocumentsDto();
        enrolByDocumentRequestDto = new EnrolByDocumentRequestDto() ;
        faceDto = new FaceDto();
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
        otpDto = null;
        consentDto = null;
        aadhaarUserKycDto = null;
        lgdDistrictResponse = null;
        redisOtp = null;
        transactionDto = null;
        verifyDLResponse = null;
        identityDocumentsDto = null;
        enrolByDocumentRequestDto = null;
        faceDto = null;
    }

    @Test
    void verifyAadhaarOtpSuccess()
    {
        when(redisService.getRedisOtp(any())).thenReturn(redisOtp);
        when(redisService.isMultipleOtpVerificationAllowed(any())).thenReturn(true);
        when(accountService.getMobileLinkedAccountCount(any()))
                .thenReturn(Mono.just(-1));
        when(rsaUtil.encrypt(any())).thenReturn(AADHAAR_NUMBER);

        aadhaarResponseDto.setStatus("success");
        aadhaarUserKycDto.setSignature("01002414pAn4JX6Bx65QNy9T3F0UCWcd/j0ZGZ/au0WjPsFFfKC8tONnhVpFbB0N4A5doIlf");
        aadhaarResponseDto.setAadhaarUserKycDto(aadhaarUserKycDto);
        when(aadhaarAppService.verifyOtp(any()))
                .thenReturn(Mono.just(aadhaarResponseDto));

        transactionDto.setTxnId(UUID.fromString("cda04910-37ca-4f2e-84d9-4e5a970d3dc7"));
        transactionDto.setMobile("7037248510");
        when(transactionService.findTransactionDetailsFromDB(any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.empty());
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(Arrays.asList(lgdDistrictResponse)));

        accountDto.setYearOfBirth("1995");
        accountDto.setMonthOfBirth("09");
        accountDto.setDayOfBirth("29");
        when(accountService.prepareNewAccount(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());

        when(abhaAddressGenerator.generateDefaultAbhaAddress(any()))
                  .thenReturn("76524587621574@abdm");
        when(transactionService.updateTransactionEntity(any(),any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any()))
                .thenReturn(Mono.just(accountDto));
        hidPhrAddressDto.setHealthIdNumber("76-5245-8762-1574");
        hidPhrAddressDto.setPhrAddress("76524587621574@abdm");
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any()))
                .thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
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
        authMethods.add(AuthMethods.OTP);
        otpDto.setTxnId("558737e6-677c-4fde-b945-c8e7ce1b3519");
        otpDto.setOtpValue("fJR1sitlt+iAdHkIqNNkPifGSQ4LN0q2XnX/sCzISAAqEfelYV0CiD9dFdIXwv0cezInxWqwKa0G2tNWK6U7fm4rK/oDg9SPJJiMcbHJ7c95tvAgQGhA8dh7THHAON+3IuyEsvdNU54VRQjtgOB1F7LBOYbpXLiLzDJOuKYw8wAm6KXhcgbi52mEbaxInERO6jyrzJP/Dk84vxVTnxxTCOIiPt5FUwuUz4k9ERNLuTKOHvK/xU+OFGn84tL8LC52iqXq70fwjmUqI3bk9l9gbQ9uwvMmN+fAAuGyH7gLUPEQCgrOacXQzncesAHiYLhzhYc6XqDeeKeovYRKUPfGmbqyQjHBIJCP9L+9z8thAulqtFC7TGTKv2dWXcG0pYlrrr1X1BbSlAi/R5ptMmiyxPqT1aZtyEjScE+6iSnW2VHxkUzGH5FiZf+evUlPamwtvgxI/9jrmnyE5qfaxA2AqdE3Jl0xJWmjKPsSrIEAX9c92+7QcCjx4SqQlXv9wicXGrKuV6OKl+MJWYrp5aNAG7AyIUrg4ukZvm+DeziOK/wBTPgLSIigZak0FjOnRJEk45S8cwokTUWAwGjNP4/nkTFctgEICobR4GnRTOB1VMd8XfeidShf7mn82yFywehfXopDFVET2cspdh740lQMnC2mSNFEPXD6NyRVgYhDQbU=");
        otpDto.setTimeStamp("2023-03-29 14:20:13");
        otpDto.setMobile("7084248510");
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        authData.setAuthMethods(authMethods);
        authData.setOtp(otpDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        Mono<EnrolByAadhaarResponseDto> responseDtoMono
               = enrolUsingAadhaarService.verifyOtp(enrolByAadhaarRequestDto);
        StepVerifier.create(responseDtoMono)
                .assertNext(response->{
                    assert response!=null;
                    assert !response.getTxnId().isEmpty();
                    assert !response.getMessage().isEmpty();
                    assert response.getAbhaProfileDto()!=null;
                    assert response.getResponseTokensDto()!=null;
                    assert response.isNew();
                }).verifyComplete();
    }

    @Test
    void verifyAndCreateAccountSuccess()
    {
        transactionDto.setMobileVerified(true);
        when(transactionService.findTransactionDetailsFromDB(any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.getAccountByDocumentCode(any()))
                .thenReturn(Mono.empty());

        verifyDLResponse.setAuthResult("success");
        when(documentAppService.verify(any()))
                .thenReturn(Mono.just(verifyDLResponse));
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());

        lgdDistrictResponse.setDistrictCode("177");
        lgdDistrictResponse.setDistrictName("SAHARANPUR");
        lgdDistrictResponse.setStateCode("9");
        lgdDistrictResponse.setStateName("UTTAR PRADESH");
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(Arrays.asList(lgdDistrictResponse)));
        when(accountService.createAccountEntity(any()))
                .thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(enrolmentCipher.encrypt(any()))
                .thenReturn("");
        when(identityDocumentDBService.addIdentityDocuments(any()))
                .thenReturn(Mono.just(identityDocumentsDto));
        when(accountAuthMethodService.addAccountAuthMethods(any()))
                .thenReturn(Mono.just(Arrays.asList(authMethodsDto)));

        transactionDto.setTxnId(UUID.fromString("672e449e-c05a-4a28-be31-37ed60c8b6d6"));
        when(transactionService.deleteTransactionEntity(String.valueOf(transactionDto.getTxnId())))
                .thenReturn(Mono.just(ResponseEntity.ok(Mono.empty())));

        notificationResponseDto.setStatus("sent");
        when(notificationService.sendRegistrationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));

        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        enrolByDocumentRequestDto.setConsent(consentDto);
        enrolByDocumentRequestDto.setTxnId("672e449e-c05a-4a28-be31-37ed60c8b6d6");
        enrolByDocumentRequestDto.setDocumentType("DRIVING_LICENCE");
        enrolByDocumentRequestDto.setDocumentId("UP1130030033357");
        enrolByDocumentRequestDto.setFirstName("Aman");
        enrolByDocumentRequestDto.setLastName("Verma");
        enrolByDocumentRequestDto.setDob("1984-07-01");
        enrolByDocumentRequestDto.setGender("M");
        enrolByDocumentRequestDto.setPinCode("247232");
        enrolByDocumentRequestDto.setState("Uttar Pradesh");
        StepVerifier.create(enrolUsingDrivingLicence.verifyAndCreateAccount(enrolByDocumentRequestDto,null))
                .expectNextCount(1L)
                .verifyComplete();
    }

    @Test
    void faceAuthSuccess()
    {
        aadhaarResponseDto.setStatus("success");
        aadhaarUserKycDto.setStatus("success");
        aadhaarResponseDto.setAadhaarUserKycDto(aadhaarUserKycDto);
        when(aadhaarAppService.faceAuth(any()))
                .thenReturn(Mono.just(aadhaarResponseDto));
        when(rsaUtil.decrypt(any()))
                .thenReturn("853123431963");

        transactionDto.setStatus("ACTIVE");
        transactionDto.setTxnId(UUID.fromString("cda04910-37ca-4f2e-84d9-4e5a970d3dc7"));
        transactionDto.setAadharNo("");
        when(transactionService.createTransactionEntity(any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.empty());
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(Arrays.asList(lgdDistrictResponse)));

        accountDto.setYearOfBirth("1995");
        accountDto.setMonthOfBirth("09");
        accountDto.setDayOfBirth("29");
        when(accountService.prepareNewAccount(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());
        when(abhaAddressGenerator.generateDefaultAbhaAddress(any()))
                .thenReturn("76524587621574@abdm");
        when(transactionService.updateTransactionEntity(any(),any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any()))
                .thenReturn(Mono.just(accountDto));

        hidPhrAddressDto.setHealthIdNumber("76-5245-8762-1574");
        hidPhrAddressDto.setPhrAddress("76524587621574@abdm");
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any()))
                .thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(accountAuthMethodService.addAccountAuthMethods(any()))
                .thenReturn(Mono.just(Arrays.asList(authMethodsDto)));
        when(jwtUtil.generateToken(any(),any()))
                .thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any()))
                .thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime())
                .thenReturn(1296000L);

        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.FACE);
        faceDto.setTimestamp("2023-04-24 13:24:13");
        faceDto.setAadhaar(AADHAAR_NUMBER);
        faceDto.setRdPidData(PID);
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        authData.setAuthMethods(authMethods);
        authData.setFace(faceDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        StepVerifier.create(enrolUsingAadhaarService.faceAuth(enrolByAadhaarRequestDto))
                .expectNextCount(1L)
                .verifyComplete();
    }
}
