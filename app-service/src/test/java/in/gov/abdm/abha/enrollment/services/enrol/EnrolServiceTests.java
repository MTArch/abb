package in.gov.abdm.abha.enrollment.services.enrol;
import in.gov.abdm.abha.enrollment.commontestdata.CommonTestData;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarExceptions;
import in.gov.abdm.abha.enrollment.exception.application.AbhaUnAuthorizedException;
import in.gov.abdm.abha.enrollment.exception.application.AbhaUnProcessableException;
import in.gov.abdm.abha.enrollment.exception.application.BadRequestException;
import in.gov.abdm.abha.enrollment.exception.hidbenefit.BenefitNotFoundException;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.*;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicResponse;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.*;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ResponseTokensDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentRequestDto;
import in.gov.abdm.abha.enrollment.model.entities.*;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import in.gov.abdm.abha.enrollment.model.nepix.VerifyDLResponse;
import in.gov.abdm.abha.enrollment.model.notification.NotificationResponseDto;
import in.gov.abdm.abha.enrollment.model.notification.NotificationType;
import in.gov.abdm.abha.enrollment.model.notification.SendNotificationRequestDto;
import in.gov.abdm.abha.enrollment.model.redis.otp.ReceiverOtpTracker;
import in.gov.abdm.abha.enrollment.model.redis.otp.RedisOtp;
import in.gov.abdm.abha.enrollment.services.aadhaar.impl.AadhaarAppServiceImpl;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.account_auth_methods.AccountAuthMethodService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.de_duplication.DeDuplicationService;
import in.gov.abdm.abha.enrollment.services.document.DocumentAppService;
import in.gov.abdm.abha.enrollment.services.document.impl.IdentityDocumentDBServiceImpl;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.impl.EnrolUsingAadhaarServiceImpl;
import in.gov.abdm.abha.enrollment.services.enrol.document.EnrolUsingDrivingLicence;
import in.gov.abdm.abha.enrollment.services.notification.NotificationService;
import in.gov.abdm.abha.enrollment.services.notification.TemplatesHelper;
import in.gov.abdm.abha.enrollment.services.redis.RedisService;
import in.gov.abdm.abha.enrollment.utilities.EnrolmentCipher;
import in.gov.abdm.abha.enrollment.utilities.LgdUtility;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;

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
    @Mock
    RedisOtp redisOtpMock;
    @Mock
    TemplatesHelper templatesHelper;


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
    private RequestHeaders requestHeaders;
    private VerifyDemographicResponse verifyDemographicResponse;
    private LocalizedDetails localizedDetails;
    private SendNotificationRequestDto sendNotificationRequestDto;
    private ReceiverOtpTracker receiverOtpTracker;


    @BeforeEach
    void setup()
    {
        MockitoAnnotations.openMocks(this);
        sendNotificationRequestDto=new SendNotificationRequestDto();
        LocalizedAccountDetails localizedAccDetails=new LocalizedAccountDetails("","","","","","","","");
        receiverOtpTracker=new ReceiverOtpTracker("",1,1,true);
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
        aadhaarUserKycDto=new AadhaarUserKycDto("","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",new LocalizedDetails("","","","","","","","","","","","","","","","","","","","","","","","","","",""));
        aadhaarUserKycDto = new AadhaarUserKycDto();
        lgdDistrictResponse = new LgdDistrictResponse();
        redisOtp = new RedisOtp();
        redisOtp.setReceiver("sasd");
        redisOtp.setOtpValue("123432");

        verifyDLResponse = new VerifyDLResponse();
        identityDocumentsDto = new IdentityDocumentsDto();
        enrolByDocumentRequestDto = new EnrolByDocumentRequestDto() ;
        localizedDetails=new LocalizedDetails();
        faceDto = new FaceDto();
        requestHeaders = new RequestHeaders();

        Map<String,Object> ftkn = new HashMap<>();
        ftkn.put("roleas","1");
        ftkn.put("suwb","1");
        requestHeaders.setBenefitName("name");
        requestHeaders.setClientId("client");
        requestHeaders.setRoleList(List.of("HidIntegratedProgram"));
        requestHeaders.setFTokenClaims(ftkn);
        localizedDetails.setName("name");
        localizedDetails.setAddress("address");
        localizedDetails.setState("state");
        localizedDetails.setDistrict("dist");
        localizedDetails.setSubDist("subdist");
        localizedDetails.setStreet("street");
        localizedDetails.setLocality("locality");
        localizedDetails.setVillageTownCity("village");


        aadhaarResponseDto.setStatus("success");
        aadhaarUserKycDto.setSignature("01002414pAn4JX6Bx65QNy9T3F0UCWcd/j0ZGZ/au0WjPsFFfKC8tONnhVpFbB0N4A5doIlf");
        aadhaarUserKycDto.setPincode("123");
        aadhaarUserKycDto.setState("state");
        aadhaarUserKycDto.setPhone("******6789");
        aadhaarUserKycDto.setLocalizedDetails(localizedDetails);
        aadhaarUserKycDto.setHealthId(aadhaarUserKycDto.getHealthId());
        aadhaarUserKycDto.setUserEkycId(aadhaarUserKycDto.getUserEkycId());
        aadhaarUserKycDto.setHealthIdNumber(aadhaarUserKycDto.getHealthIdNumber());
        aadhaarUserKycDto.setPostOffice(aadhaarUserKycDto.getPostOffice());
        aadhaarUserKycDto.setAadhaar(aadhaarUserKycDto.getAadhaar());
        aadhaarUserKycDto.setErrorCode(aadhaarUserKycDto.getErrorCode());
        aadhaarUserKycDto.setActionCode(aadhaarUserKycDto.getActionCode());
        aadhaarUserKycDto.setMigrated(aadhaarUserKycDto.getMigrated());
        aadhaarUserKycDto.setTxnId(aadhaarUserKycDto.getTxnId());
        aadhaarUserKycDto.setAddress(aadhaarUserKycDto.getAddress());

        aadhaarResponseDto.setAadhaarUserKycDto(aadhaarUserKycDto);
        transactionDto.setTxnId(UUID.fromString("cda04910-37ca-4f2e-84d9-4e5a970d3dc7"));
        transactionDto.setMobile("7084248510");
        accountDto.setHealthIdNumber("12321");
        accountDto.setStatus(AccountStatus.DEACTIVATED.getValue());
        accountDto.setDayOfBirth("12");
        accountDto.setMonthOfBirth("05");
        accountDto.setYearOfBirth("2000");
        accountDto.setStateCode("1");
        accountDto.setDistrictCode("2");
        hidPhrAddressDto.setHealthIdNumber("76-5245-8762-1574");
        hidPhrAddressDto.setPhrAddress("76524587621574@abdm");
        HidPhrAddressDto h=new HidPhrAddressDto(hidPhrAddressDto.getHidPhrAddressId(), hidPhrAddressDto.getHealthIdNumber(), hidPhrAddressDto.getPhrAddress(), hidPhrAddressDto.getStatus(), hidPhrAddressDto.getPreferred(),hidPhrAddressDto.getLastModifiedBy(),hidPhrAddressDto.getLastModifiedDate(),hidPhrAddressDto.getHasMigrated(),hidPhrAddressDto.getCreatedBy(),hidPhrAddressDto.getCreatedDate(),hidPhrAddressDto.getLinked(), hidPhrAddressDto.getCmMigrated(), hidPhrAddressDto.isNewHidPhrAddress());
        notificationResponseDto.setStatus("sent");
        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.OTP);
        otpDto.setTxnId("558737e6-677c-4fde-b945-c8e7ce1b3519");
        otpDto.setOtpValue("fJR1sitlt+iAdHkIqNNkPifGSQ4LN0q2XnX/sCzISAAqEfelYV0CiD9dFdIXwv0cezInxWqwKa0G2tNWK6U7fm4rK/oDg9SPJJiMcbHJ7c95tvAgQGhA8dh7THHAON+3IuyEsvdNU54VRQjtgOB1F7LBOYbpXLiLzDJOuKYw8wAm6KXhcgbi52mEbaxInERO6jyrzJP/Dk84vxVTnxxTCOIiPt5FUwuUz4k9ERNLuTKOHvK/xU+OFGn84tL8LC52iqXq70fwjmUqI3bk9l9gbQ9uwvMmN+fAAuGyH7gLUPEQCgrOacXQzncesAHiYLhzhYc6XqDeeKeovYRKUPfGmbqyQjHBIJCP9L+9z8thAulqtFC7TGTKv2dWXcG0pYlrrr1X1BbSlAi/R5ptMmiyxPqT1aZtyEjScE+6iSnW2VHxkUzGH5FiZf+evUlPamwtvgxI/9jrmnyE5qfaxA2AqdE3Jl0xJWmjKPsSrIEAX9c92+7QcCjx4SqQlXv9wicXGrKuV6OKl+MJWYrp5aNAG7AyIUrg4ukZvm+DeziOK/wBTPgLSIigZak0FjOnRJEk45S8cwokTUWAwGjNP4/nkTFctgEICobR4GnRTOB1VMd8XfeidShf7mn82yFywehfXopDFVET2cspdh740lQMnC2mSNFEPXD6NyRVgYhDQbU=");
        otpDto.setMobile("7084246789");
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        authData.setAuthMethods(authMethods);
        authData.setAuthMethods(authMethods);
        authData.setOtp(otpDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        verifyDemographicResponse=new VerifyDemographicResponse(true,"12","12");
        VerifyDemographicResponse v=new VerifyDemographicResponse();
        v.setReason("1");
        String a =v.getReason();
        VerifyDemographicResponse v1=VerifyDemographicResponse.builder().build();

        localizedDetails.setGender(aadhaarUserKycDto.getLocalizedDetails().getGender());
        localizedDetails.setEmail(aadhaarUserKycDto.getLocalizedDetails().getEmail());
        localizedDetails.setPhone(aadhaarUserKycDto.getLocalizedDetails().getPhone());
        localizedDetails.setPincode(aadhaarUserKycDto.getLocalizedDetails().getPincode());
        localizedDetails.setBirthdate(aadhaarUserKycDto.getLocalizedDetails().getBirthdate());

        localizedDetails.setCareOf(aadhaarUserKycDto.getLocalizedDetails().getCareOf());
        localizedDetails.setHouse(aadhaarUserKycDto.getLocalizedDetails().getHouse());
        localizedDetails.setLandmark(aadhaarUserKycDto.getLocalizedDetails().getLandmark());
        localizedDetails.setPostOffice(aadhaarUserKycDto.getLocalizedDetails().getPostOffice());
        localizedDetails.setSignature(aadhaarUserKycDto.getLocalizedDetails().getSignature());

        localizedDetails.setAadhaar(aadhaarUserKycDto.getLocalizedDetails().getAadhaar());
        localizedDetails.setUidiaTxn(aadhaarUserKycDto.getLocalizedDetails().getUidiaTxn());
        localizedDetails.setErrorCode(aadhaarUserKycDto.getLocalizedDetails().getErrorCode());
        localizedDetails.setReason(aadhaarUserKycDto.getLocalizedDetails().getReason());
        localizedDetails.setStatus(aadhaarUserKycDto.getLocalizedDetails().getStatus());

        localizedDetails.setResponseCode(aadhaarUserKycDto.getLocalizedDetails().getResponseCode());
        localizedDetails.setActionCode(aadhaarUserKycDto.getLocalizedDetails().getActionCode());
        localizedDetails.setMigrated(aadhaarUserKycDto.getLocalizedDetails().getMigrated());
        localizedDetails.setTxnId(aadhaarUserKycDto.getLocalizedDetails().getTxnId());



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
        requestHeaders = null;
        verifyDemographicResponse=null;
    }

    @Test
    void verifyAadhaarOtpSuccess()
    {
        ReflectionTestUtils.setField(enrolUsingAadhaarService,"maxMobileLinkingCount",1);
        ReflectionTestUtils.setField(enrolUsingAadhaarService,"isTransactionManagementEnable",false);transactionDto.setMobile("******8510");
        when(redisService.getRedisOtp(any())).thenReturn(redisOtp);
        when(redisService.isMultipleOtpVerificationAllowed(any())).thenReturn(true);
        when(accountService.getMobileLinkedAccountCount(any()))
                .thenReturn(Mono.just(-1));
        when(rsaUtil.encrypt(any())).thenReturn(AADHAAR_NUMBER);

        when(aadhaarAppService.verifyOtp(any()))
                .thenReturn(Mono.just(aadhaarResponseDto));


        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.empty());
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(Collections.singletonList(lgdDistrictResponse)));

        when(accountService.prepareNewAccount(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());

        when(abhaAddressGenerator.generateDefaultAbhaAddress(any()))
                  .thenReturn("76524587621574@abdm");
        when(transactionService.findTransactionDetailsFromDB(any()))
                .thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any()))
                .thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(accountAuthMethodService.addAccountAuthMethods(any()))
                .thenReturn(Mono.just(Collections.singletonList(authMethodsDto)));


        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any()))
                .thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any()))
                .thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime())
                .thenReturn(1296000L);

        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));

        Mono<EnrolByAadhaarResponseDto> responseDtoMono
               = enrolUsingAadhaarService.verifyOtp(enrolByAadhaarRequestDto,requestHeaders);

        when(transactionService.createTransactionEntity(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.empty());
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());
        //when(MapperUtils.mapKycDetails(any(),any())).thenReturn(new ABHAProfileDto());
        //when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(lgdDistrictResponses));
        when(accountService.prepareNewAccount(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("success"));
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));
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
    void verifyAadhaarOtpSuccess3()
    {
        accountDto.setStatus(AccountStatus.DEACTIVATED.toString());
        ReflectionTestUtils.setField(enrolUsingAadhaarService,"maxMobileLinkingCount",1);
        ReflectionTestUtils.setField(enrolUsingAadhaarService,"isTransactionManagementEnable",false);transactionDto.setMobile("******8510");
        when(redisService.getRedisOtp(any())).thenReturn(redisOtp);
        when(redisService.isMultipleOtpVerificationAllowed(any())).thenReturn(true);
        when(accountService.getMobileLinkedAccountCount(any()))
                .thenReturn(Mono.just(-1));
        when(rsaUtil.encrypt(any())).thenReturn(AADHAAR_NUMBER);

        when(aadhaarAppService.verifyOtp(any()))
                .thenReturn(Mono.just(aadhaarResponseDto));

        when(transactionService.findTransactionDetailsFromDB(any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.just(accountDto));
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(Collections.singletonList(lgdDistrictResponse)));

        when(accountService.prepareNewAccount(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());

        when(abhaAddressGenerator.generateDefaultAbhaAddress(any()))
                .thenReturn("76524587621574@abdm");
        when(transactionService.updateTransactionEntity(any(),any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any()))
                .thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(accountAuthMethodService.addAccountAuthMethods(any()))
                .thenReturn(Mono.just(Collections.singletonList(authMethodsDto)));


        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any()))
                .thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any()))
                .thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime())
                .thenReturn(1296000L);

        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));

        Mono<EnrolByAadhaarResponseDto> responseDtoMono
                = enrolUsingAadhaarService.verifyOtp(enrolByAadhaarRequestDto,requestHeaders);

        when(transactionService.createTransactionEntity(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());
        //when(MapperUtils.mapKycDetails(any(),any())).thenReturn(new ABHAProfileDto());
        //when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(lgdDistrictResponses));
        when(accountService.prepareNewAccount(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("success"));
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));
        StepVerifier.create(enrolUsingAadhaarService.verifyOtp(enrolByAadhaarRequestDto,requestHeaders)).expectNextCount(1L).verifyComplete();
    }

    @Test
    void verifyAadhaarOtpSuccess4()
    {
        accountDto.setStatus(AccountStatus.ACTIVE.toString());
        ReflectionTestUtils.setField(enrolUsingAadhaarService,"maxMobileLinkingCount",1);
        ReflectionTestUtils.setField(enrolUsingAadhaarService,"isTransactionManagementEnable",false);
        transactionDto.setMobile("******8510");
        when(redisService.getRedisOtp(any())).thenReturn(redisOtp);
        when(redisService.isMultipleOtpVerificationAllowed(any())).thenReturn(true);
        when(accountService.getMobileLinkedAccountCount(any()))
                .thenReturn(Mono.just(-1));
        when(rsaUtil.encrypt(any())).thenReturn(AADHAAR_NUMBER);

        when(aadhaarAppService.verifyOtp(any()))
                .thenReturn(Mono.just(aadhaarResponseDto));

        when(transactionService.findTransactionDetailsFromDB(any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.just(accountDto));
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(Collections.singletonList(lgdDistrictResponse)));

        when(accountService.prepareNewAccount(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());

        when(abhaAddressGenerator.generateDefaultAbhaAddress(any()))
                .thenReturn("76524587621574@abdm");
        when(transactionService.updateTransactionEntity(any(),any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any()))
                .thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(accountAuthMethodService.addAccountAuthMethods(any()))
                .thenReturn(Mono.just(Collections.singletonList(authMethodsDto)));
        List<LgdDistrictResponse> lgdDistrictResponses=new ArrayList<>();
        lgdDistrictResponse.setDistrictCode("123");
        lgdDistrictResponses.add(lgdDistrictResponse);
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(lgdDistrictResponses));

        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any()))
                .thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any()))
                .thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime())
                .thenReturn(1296000L);

        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));

        Mono<EnrolByAadhaarResponseDto> responseDtoMono
                = enrolUsingAadhaarService.verifyOtp(enrolByAadhaarRequestDto,requestHeaders);

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
        when(accountService.updateAccountByHealthIdNumber(any(),any())).thenReturn(Mono.just(accountDto));
       // when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));
        //accountDto.setStatus(AccountStatus.DELETED.getValue());
        ABHAProfileDto abhaProfileDto=new ABHAProfileDto();
        abhaProfileDto.setAbhaNumber("12321");
        abhaProfileDto.setAbhaStatus(AccountStatus.ACTIVE);
        abhaProfileDto.setPhrAddress(List.of("76524587621574@abdm"));
        abhaProfileDto.setDistrictCode("2");
        abhaProfileDto.setStateCode("1");
        ResponseTokensDto responseTokensDto = ResponseTokensDto.builder()
                .token(jwtUtil.generateToken(transactionDto.getTxnId().toString(), accountDto))
                .expiresIn(jwtUtil.jwtTokenExpiryTime())
                .refreshToken(jwtUtil.generateRefreshToken(accountDto.getHealthIdNumber()))
                .refreshExpiresIn(jwtUtil.jwtRefreshTokenExpiryTime())
                .build();
        EnrolByAadhaarResponseDto enrolByAadhaarResponseDto = EnrolByAadhaarResponseDto.builder()
                .txnId(transactionDto.getTxnId().toString())
                .abhaProfileDto(abhaProfileDto)
                .message(AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST)
                .isNew(false)
                .build();
        enrolByAadhaarResponseDto.setResponseTokensDto(responseTokensDto);
        StepVerifier.create(enrolUsingAadhaarService.verifyOtp(enrolByAadhaarRequestDto,requestHeaders)).expectNext(enrolByAadhaarResponseDto).verifyComplete();
    }


    @Test
    void faceAuthSuccess()
    {
        when(accountService.getMobileLinkedAccountCount(any()))
                .thenReturn(Mono.just(-1));
        aadhaarResponseDto.setStatus("success");
        aadhaarUserKycDto.setStatus("success");
        aadhaarUserKycDto.setSignature("");
        aadhaarResponseDto.setAadhaarUserKycDto(aadhaarUserKycDto);
        when(aadhaarAppService.faceAuth(any()))
                .thenReturn(Mono.just(aadhaarResponseDto));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.empty());
        when(rsaUtil.decrypt(any()))
                .thenReturn("853123431963");

        transactionDto.setStatus("ACTIVE");
        transactionDto.setTxnId(UUID.fromString("cda04910-37ca-4f2e-84d9-4e5a970d3dc7"));
        transactionDto.setAadharNo("omJXDzhTJYvtCVhy+hlXSdZ3GW9ZIHMHz1FxQZnwl/oQlV9TSHFxU0CXW7ncj2JXYWGLEJjlrqJXqEXcWUQiktcNYrQ6xEviLepYv50KsvTcHJ2UJjaWQiZrTks0XrPPCeKuOLqdVz/+1Z2r5xupNOxNCkSuYTi9RIO2ZR394zCo9pT2kqoWB9E13g33EO+FubWRr5JEHK5Whcn/pY1kKkLClwsQMuBjV8RRAoVPOxtOPSgjq6j5U2C9Xca55b4xoseMRgYMu8yOWBBYeVnmbFtW8F5nfj42gXkx/QIK/DBogVZTm+zpk7amZnC990RcBmm9Qa1IIYH6VvFPjW8NgE+zLz53cI3fHmjQtcHLKe8nvm3lxFtqTP/vJavcF5EmcBaZuQ9/d4Tb3QrXvdvR4X6NIlzIIvclVZY3PS4Rn0gDhUxbkKwXEMm1qq4Bt0yjNike+Ox8766ELnFw5/+E+7Q0AAVTYgkRYB9J7iZ7QDKGxpvvQ6qKQtJ/5AViV4wQe/Hi/joxCM2zatJ6A3F97FG9ebffMqYPzw2mQrzXkerE1VFhm3kGg6qX3Vrf4zYm8b5KzJjQi8MzNX7u5yxO3FpPxdk2vjKV7w70Xr+fRTLhRKZug7F7gHV0aOE7+JzIVHPHy5McYG9ZvvM8PDdmXuR8g/5hD+c9M/D3KxwkXZI=");
        when(transactionService.createTransactionEntity(any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.just(accountDto));
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(Collections.singletonList(lgdDistrictResponse)));
        when(transactionService.findTransactionDetailsFromDB(any()))
                .thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any()))
                .thenReturn(Mono.just(transactionDto));

        accountDto.setYearOfBirth("1995");
        accountDto.setMonthOfBirth("09");
        accountDto.setDayOfBirth("29");
        when(accountService.prepareNewAccount(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));

        when(abhaAddressGenerator.generateDefaultAbhaAddress(any()))
                .thenReturn("76524587621574@abdm");
        transactionDto.setMobile("******6789");
        when(transactionService.updateTransactionEntity(any(),any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        notificationResponseDto.setStatus("sent");
        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));

        hidPhrAddressDto.setHealthIdNumber("76-5245-8762-1574");
        hidPhrAddressDto.setPhrAddress("76524587621574@abdm");
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any()))
                .thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(accountAuthMethodService.addAccountAuthMethods(any()))
                .thenReturn(Mono.just(Collections.singletonList(authMethodsDto)));
        when(jwtUtil.generateToken(any(),any()))
                .thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any()))
                .thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime())
                .thenReturn(1296000L);
        when(accountService.settingClientIdAndOrigin(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any()))
                .thenReturn(Mono.just("success"));
        when(redisOtpMock.getReceiver())
                .thenReturn("1234");
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());

        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.FACE);
        faceDto.setAadhaar(AADHAAR_NUMBER);
        faceDto.setRdPidData(PID);
        faceDto.setMobile("9887656789");
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        authData.setAuthMethods(authMethods);
        authData.setFace(faceDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        StepVerifier.create(enrolUsingAadhaarService.faceAuth(enrolByAadhaarRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void requestNotificationSuccessNoMobileSet()
    {
        accountDto.setStatus(AccountStatus.ACTIVE.getValue());
        sendNotificationRequestDto.setType("CREATION");
        sendNotificationRequestDto.setAbhaNumber(CommonTestData.ABHA_NUMBER_VALID);
        sendNotificationRequestDto.setNotificationType(List.of(NotificationType.SMS));
        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));
        when(templatesHelper.prepareSMSMessage(any())).thenReturn(Mono.just("test"));
        when(accountService.getAccountByHealthIdNumber(any())).thenReturn(Mono.just(accountDto));
        StepVerifier.create(enrolUsingAadhaarService.requestNotification(sendNotificationRequestDto,requestHeaders))
                .expectNextCount(0L)
                .verifyComplete();
    }
    @Test
    void requestNotificationSuccessSMS()
    {
        accountDto.setStatus(AccountStatus.ACTIVE.getValue());
        accountDto.setMobile("9876283838");
        sendNotificationRequestDto.setType("CREATION");
        sendNotificationRequestDto.setAbhaNumber(CommonTestData.ABHA_NUMBER_VALID);
        sendNotificationRequestDto.setNotificationType(List.of(NotificationType.SMS));
        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));
        when(templatesHelper.prepareSMSMessage(any())).thenReturn(Mono.just("test"));
        when(accountService.getAccountByHealthIdNumber(any())).thenReturn(Mono.just(accountDto));
        StepVerifier.create(enrolUsingAadhaarService.requestNotification(sendNotificationRequestDto,requestHeaders))
                .expectNextCount(0L)
                .verifyComplete();
    }
    @Test
    void requestNotificationSuccessEmail()
    {
        accountDto.setStatus(AccountStatus.ACTIVE.getValue());
        accountDto.setMobile("9876283838");
        accountDto.setEmail("abc@abc");
        sendNotificationRequestDto.setType("CREATION");
        sendNotificationRequestDto.setAbhaNumber(CommonTestData.ABHA_NUMBER_VALID);
        sendNotificationRequestDto.setNotificationType(List.of(NotificationType.EMAIL));
        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));
        when(notificationService.sendEmailOtp(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));
        when(templatesHelper.prepareSMSMessage(any(),any())).thenReturn(Mono.just("test"));
        when(accountService.getAccountByHealthIdNumber(any())).thenReturn(Mono.just(accountDto));
        StepVerifier.create(enrolUsingAadhaarService.requestNotification(sendNotificationRequestDto,requestHeaders))
                .expectNextCount(0L)
                .verifyComplete();
    }
    @Test
    void requestNotificationSuccessSMSandEmail()
    {
        accountDto.setStatus(AccountStatus.ACTIVE.getValue());
        accountDto.setMobile("9876283838");
        sendNotificationRequestDto.setType("CREATION");
        sendNotificationRequestDto.setAbhaNumber(CommonTestData.ABHA_NUMBER_VALID);
        sendNotificationRequestDto.setNotificationType(Arrays.asList(NotificationType.EMAIL,NotificationType.SMS));
        when(notificationService.sendABHACreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(notificationService.sendEmailOtp(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(notificationService.sendSmsAndEmailOtp(any(),any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(templatesHelper.prepareSMSMessage(any(),any())).thenReturn(Mono.just("test"));
        when(accountService.getAccountByHealthIdNumber(any())).thenReturn(Mono.just(accountDto));
        StepVerifier.create(enrolUsingAadhaarService.requestNotification(sendNotificationRequestDto,requestHeaders))
                .expectNextCount(0L)
                .verifyComplete();
        accountDto.setEmail("abc@abc");
        StepVerifier.create(enrolUsingAadhaarService.requestNotification(sendNotificationRequestDto,requestHeaders))
                .expectNextCount(0L)
                .verifyComplete();
    }
    @Test
    void validateNotificationRequestSuccessABhaEmpty()
    {
        sendNotificationRequestDto.setType("");
        sendNotificationRequestDto.setAbhaNumber("");
        sendNotificationRequestDto.setNotificationType(List.of(NotificationType.WRONG));
        Assert.assertThrows(BadRequestException.class,()->enrolUsingAadhaarService.validateNotificationRequest(sendNotificationRequestDto));
    }
    @Test
    void validateNotificationRequestSuccess()
    {
        sendNotificationRequestDto.setType("test");
        sendNotificationRequestDto.setAbhaNumber("w23");
        sendNotificationRequestDto.setNotificationType(List.of());
        Assert.assertThrows(BadRequestException.class,()->enrolUsingAadhaarService.validateNotificationRequest(sendNotificationRequestDto));
    }
    @Test
    void validateHeadersTests()
    {
       Assert.assertThrows(BenefitNotFoundException.class,()->enrolUsingAadhaarService.validateHeaders(requestHeaders, List.of(AuthMethods.BIO),"tkn"));
    }
    @Test
    void validateHeadersTests2()
    {
        Map<String,Object> ftkn = new HashMap<>();
        ftkn.put("roles","OFFLINE_HID");
        ftkn.put("sub","OFFLINE_HID");
        requestHeaders.setBenefitName("name");
        requestHeaders.setClientId("client");
        requestHeaders.setRoleList(List.of("HidIntegratedProgram"));
        requestHeaders.setFTokenClaims(ftkn);
        IntegratedProgramDto integratedProgramDto= new IntegratedProgramDto();
        integratedProgramDto.setId("id");
        integratedProgramDto.setBenefitName(requestHeaders.getBenefitName());
        integratedProgramDto.setProgramName("programName");
        integratedProgramDto.setClientId(requestHeaders.getClientId());
        List<IntegratedProgramDto> list1 = new ArrayList<>();
        list1.add(integratedProgramDto);
        when(redisService.getIntegratedPrograms()).thenReturn(list1);
        when(redisService.reloadAndGetIntegratedPrograms()).thenReturn(Mono.just(list1));
        Mono<Boolean> res = enrolUsingAadhaarService.validateHeaders(requestHeaders,Arrays.asList(AuthMethods.BIO,AuthMethods.DEMO,AuthMethods.OTP,AuthMethods.IRIS,AuthMethods.FACE,AuthMethods.WRONG,AuthMethods.DEMO_AUTH),"");
    }
    @Test
    void validateHeadersTests2flse()
    {
        Map<String,Object> ftkn = new HashMap<>();
        ftkn.put("roles","OFFLINE_HID");
        ftkn.put("sub","OFFLINE_HID");
        requestHeaders.setBenefitName("name");
        requestHeaders.setClientId("client");
        requestHeaders.setRoleList(List.of("HidIntegratedProgram"));
        requestHeaders.setFTokenClaims(ftkn);
        IntegratedProgramDto integratedProgramDto= new IntegratedProgramDto();
        integratedProgramDto.setId("id");
        integratedProgramDto.setBenefitName(requestHeaders.getBenefitName());
        integratedProgramDto.setProgramName("programName");
        integratedProgramDto.setClientId(requestHeaders.getClientId());
        List<IntegratedProgramDto> list1 = new ArrayList<>();
        //list1.add(integratedProgramDto);
        when(redisService.getIntegratedPrograms()).thenReturn(list1);
        when(redisService.reloadAndGetIntegratedPrograms()).thenReturn(Mono.just(list1));

        Mono<Boolean> res = enrolUsingAadhaarService.validateHeaders(requestHeaders,Arrays.asList(AuthMethods.BIO,AuthMethods.DEMO,AuthMethods.OTP,AuthMethods.IRIS,AuthMethods.FACE,AuthMethods.WRONG,AuthMethods.DEMO_AUTH),"");
    }
    @Test
    void validateHeadersTests3()
    {
        Map<String,Object> ftkn = new HashMap<>();
        ftkn.put("roles","OFFLINE_HID");
        ftkn.put("sub","OFFLINE_HID");
        requestHeaders.setBenefitName("name");
        requestHeaders.setClientId("client");
        requestHeaders.setRoleList(List.of("HidIntegratedProgram"));
        requestHeaders.setFTokenClaims(ftkn);
        IntegratedProgramDto integratedProgramDto= new IntegratedProgramDto();
        integratedProgramDto.setId("id");
        integratedProgramDto.setBenefitName("nameN");
        integratedProgramDto.setProgramName("programName");
        integratedProgramDto.setClientId("clientId");
        List<IntegratedProgramDto> list1 = new ArrayList<>();
        list1.add(integratedProgramDto);
        when(redisService.getIntegratedPrograms()).thenReturn(list1);
        Mono<Boolean> res = enrolUsingAadhaarService.validateHeaders(requestHeaders,Arrays.asList(AuthMethods.FACE,AuthMethods.WRONG),"");
    }
    @Test
    void validateHeadersTestsErrorBenefitNotFoundException()
    {
        Map<String,Object> ftkn = new HashMap<>();
        ftkn.put("roles","OFFLINE_HID");
        ftkn.put("sub","OFFLINE_HID");
        requestHeaders.setBenefitName("name");
        requestHeaders.setClientId("client");
        requestHeaders.setRoleList(List.of("a"));
        requestHeaders.setFTokenClaims(ftkn);
        IntegratedProgramDto integratedProgramDto= new IntegratedProgramDto();
        integratedProgramDto.setId("id");
        integratedProgramDto.setBenefitName("nameN");
        integratedProgramDto.setProgramName("programName");
        integratedProgramDto.setClientId("clientId");
        List<IntegratedProgramDto> list1 = new ArrayList<>();
        list1.add(integratedProgramDto);
        when(redisService.getIntegratedPrograms()).thenReturn(list1);
        Assert.assertThrows(BenefitNotFoundException.class,()->enrolUsingAadhaarService.validateHeaders(requestHeaders,Arrays.asList(AuthMethods.BIO,AuthMethods.DEMO,AuthMethods.OTP,AuthMethods.IRIS,AuthMethods.FACE,AuthMethods.WRONG,AuthMethods.DEMO_AUTH),""));


    }
    @Test
    void validateHeadersTestsErrorBenefitNotFoundException2()
    {
        Map<String,Object> ftkn = new HashMap<>();
        ftkn.put("roles","OFFLINE_HID");
        ftkn.put("sub","OFFLINE_HID");
        requestHeaders.setBenefitName("");
        requestHeaders.setClientId("client");
        requestHeaders.setRoleList(List.of("HidIntegratedProgram"));
        requestHeaders.setFTokenClaims(ftkn);
        IntegratedProgramDto integratedProgramDto= new IntegratedProgramDto();
        integratedProgramDto.setId("id");
        integratedProgramDto.setBenefitName("nameN");
        integratedProgramDto.setProgramName("programName");
        integratedProgramDto.setClientId("clientId");
        List<IntegratedProgramDto> list1 = new ArrayList<>();
        list1.add(integratedProgramDto);
        when(redisService.getIntegratedPrograms()).thenReturn(list1);
        Assert.assertThrows(BenefitNotFoundException.class,()->enrolUsingAadhaarService.validateHeaders(requestHeaders,Arrays.asList(AuthMethods.BIO,AuthMethods.DEMO,AuthMethods.OTP,AuthMethods.IRIS,AuthMethods.FACE,AuthMethods.WRONG,AuthMethods.DEMO_AUTH),""));


    }
    @Test
    void validateHeadersTestsErrorBenefitNotFoundException3()
    {
        Map<String,Object> ftkn = new HashMap<>();
        ftkn.put("roles","OFFLINE_HID");
       // ftkn.put("sub","OFFLINE_HID");
        requestHeaders.setBenefitName("name");
        requestHeaders.setClientId("client");
        requestHeaders.setRoleList(List.of("HidIntegratedProgram"));
        requestHeaders.setFTokenClaims(ftkn);
        IntegratedProgramDto integratedProgramDto= new IntegratedProgramDto();
        integratedProgramDto.setId("id");
        integratedProgramDto.setBenefitName("nameN");
        integratedProgramDto.setProgramName("programName");
        integratedProgramDto.setClientId("clientId");
        List<IntegratedProgramDto> list1 = new ArrayList<>();
        list1.add(integratedProgramDto);
        when(redisService.getIntegratedPrograms()).thenReturn(list1);
        Assert.assertThrows(AbhaUnAuthorizedException.class,()->enrolUsingAadhaarService.validateHeaders(requestHeaders,Arrays.asList(AuthMethods.BIO,AuthMethods.DEMO,AuthMethods.OTP,AuthMethods.IRIS,AuthMethods.FACE,AuthMethods.WRONG,AuthMethods.DEMO_AUTH),""));


    }
    @Test
    void faceAuthSuccessAccStatusInActive()
    {accountDto.setStatus(AccountStatus.IN_ACTIVE.getValue());
        when(accountService.getMobileLinkedAccountCount(any()))
                .thenReturn(Mono.just(-1));
        aadhaarResponseDto.setStatus("success");
        aadhaarUserKycDto.setStatus("success");
        aadhaarUserKycDto.setSignature("");
        aadhaarResponseDto.setAadhaarUserKycDto(aadhaarUserKycDto);
        when(aadhaarAppService.faceAuth(any()))
                .thenReturn(Mono.just(aadhaarResponseDto));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.empty());
        when(rsaUtil.decrypt(any()))
                .thenReturn("853123431963");

        transactionDto.setStatus("ACTIVE");
        transactionDto.setTxnId(UUID.fromString("cda04910-37ca-4f2e-84d9-4e5a970d3dc7"));
        transactionDto.setAadharNo("omJXDzhTJYvtCVhy+hlXSdZ3GW9ZIHMHz1FxQZnwl/oQlV9TSHFxU0CXW7ncj2JXYWGLEJjlrqJXqEXcWUQiktcNYrQ6xEviLepYv50KsvTcHJ2UJjaWQiZrTks0XrPPCeKuOLqdVz/+1Z2r5xupNOxNCkSuYTi9RIO2ZR394zCo9pT2kqoWB9E13g33EO+FubWRr5JEHK5Whcn/pY1kKkLClwsQMuBjV8RRAoVPOxtOPSgjq6j5U2C9Xca55b4xoseMRgYMu8yOWBBYeVnmbFtW8F5nfj42gXkx/QIK/DBogVZTm+zpk7amZnC990RcBmm9Qa1IIYH6VvFPjW8NgE+zLz53cI3fHmjQtcHLKe8nvm3lxFtqTP/vJavcF5EmcBaZuQ9/d4Tb3QrXvdvR4X6NIlzIIvclVZY3PS4Rn0gDhUxbkKwXEMm1qq4Bt0yjNike+Ox8766ELnFw5/+E+7Q0AAVTYgkRYB9J7iZ7QDKGxpvvQ6qKQtJ/5AViV4wQe/Hi/joxCM2zatJ6A3F97FG9ebffMqYPzw2mQrzXkerE1VFhm3kGg6qX3Vrf4zYm8b5KzJjQi8MzNX7u5yxO3FpPxdk2vjKV7w70Xr+fRTLhRKZug7F7gHV0aOE7+JzIVHPHy5McYG9ZvvM8PDdmXuR8g/5hD+c9M/D3KxwkXZI=");
        when(transactionService.createTransactionEntity(any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.just(accountDto));
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(Collections.singletonList(lgdDistrictResponse)));
        when(transactionService.findTransactionDetailsFromDB(any()))
                .thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any()))
                .thenReturn(Mono.just(transactionDto));

        accountDto.setYearOfBirth("1995");
        accountDto.setMonthOfBirth("09");
        accountDto.setDayOfBirth("29");
        when(accountService.prepareNewAccount(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));

        when(abhaAddressGenerator.generateDefaultAbhaAddress(any()))
                .thenReturn("76524587621574@abdm");
        transactionDto.setMobile("******6789");
        when(transactionService.updateTransactionEntity(any(),any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        notificationResponseDto.setStatus("sent");
        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));

        hidPhrAddressDto.setHealthIdNumber("76-5245-8762-1574");
        hidPhrAddressDto.setPhrAddress("76524587621574@abdm");
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any()))
                .thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(accountAuthMethodService.addAccountAuthMethods(any()))
                .thenReturn(Mono.just(Collections.singletonList(authMethodsDto)));
        when(jwtUtil.generateToken(any(),any()))
                .thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any()))
                .thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime())
                .thenReturn(1296000L);
        when(accountService.settingClientIdAndOrigin(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any()))
                .thenReturn(Mono.just("success"));
        when(redisOtpMock.getReceiver())
                .thenReturn("1234");
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.error(Exception::new));

        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.FACE);
        faceDto.setAadhaar(AADHAAR_NUMBER);
        faceDto.setRdPidData(PID);
        faceDto.setMobile("9887656789");
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        authData.setAuthMethods(authMethods);
        authData.setFace(faceDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        StepVerifier.create(enrolUsingAadhaarService.faceAuth(enrolByAadhaarRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void faceAuthSuccessAccStatusDeleted()
    {accountDto.setStatus(AccountStatus.DELETED.getValue());
        accountDto.setYearOfBirth("2023");
        transactionDto.setMobile("******6789");
        when(accountService.getMobileLinkedAccountCount(any()))
                .thenReturn(Mono.just(-1));
        aadhaarResponseDto.setStatus("success");
        aadhaarUserKycDto.setStatus("success");
        aadhaarUserKycDto.setSignature("");
        aadhaarResponseDto.setAadhaarUserKycDto(aadhaarUserKycDto);
        when(aadhaarAppService.faceAuth(any()))
                .thenReturn(Mono.just(aadhaarResponseDto));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.empty());
        when(rsaUtil.decrypt(any()))
                .thenReturn("853123431963");
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));

        transactionDto.setStatus("ACTIVE");
        transactionDto.setTxnId(UUID.fromString("cda04910-37ca-4f2e-84d9-4e5a970d3dc7"));
        transactionDto.setAadharNo("omJXDzhTJYvtCVhy+hlXSdZ3GW9ZIHMHz1FxQZnwl/oQlV9TSHFxU0CXW7ncj2JXYWGLEJjlrqJXqEXcWUQiktcNYrQ6xEviLepYv50KsvTcHJ2UJjaWQiZrTks0XrPPCeKuOLqdVz/+1Z2r5xupNOxNCkSuYTi9RIO2ZR394zCo9pT2kqoWB9E13g33EO+FubWRr5JEHK5Whcn/pY1kKkLClwsQMuBjV8RRAoVPOxtOPSgjq6j5U2C9Xca55b4xoseMRgYMu8yOWBBYeVnmbFtW8F5nfj42gXkx/QIK/DBogVZTm+zpk7amZnC990RcBmm9Qa1IIYH6VvFPjW8NgE+zLz53cI3fHmjQtcHLKe8nvm3lxFtqTP/vJavcF5EmcBaZuQ9/d4Tb3QrXvdvR4X6NIlzIIvclVZY3PS4Rn0gDhUxbkKwXEMm1qq4Bt0yjNike+Ox8766ELnFw5/+E+7Q0AAVTYgkRYB9J7iZ7QDKGxpvvQ6qKQtJ/5AViV4wQe/Hi/joxCM2zatJ6A3F97FG9ebffMqYPzw2mQrzXkerE1VFhm3kGg6qX3Vrf4zYm8b5KzJjQi8MzNX7u5yxO3FpPxdk2vjKV7w70Xr+fRTLhRKZug7F7gHV0aOE7+JzIVHPHy5McYG9ZvvM8PDdmXuR8g/5hD+c9M/D3KxwkXZI=");
        when(transactionService.createTransactionEntity(any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.just(accountDto));
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(Collections.singletonList(lgdDistrictResponse)));
        when(transactionService.findTransactionDetailsFromDB(any()))
                .thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any()))
                .thenReturn(Mono.just(transactionDto));

        accountDto.setYearOfBirth("1995");
        accountDto.setMonthOfBirth("09");
        accountDto.setDayOfBirth("29");
        when(accountService.prepareNewAccount(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));

        when(abhaAddressGenerator.generateDefaultAbhaAddress(any()))
                .thenReturn("76524587621574@abdm");
        transactionDto.setMobile("******6789");
        when(transactionService.updateTransactionEntity(any(),any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        notificationResponseDto.setStatus("sent");
        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));

        hidPhrAddressDto.setHealthIdNumber("76-5245-8762-1574");
        hidPhrAddressDto.setPhrAddress("76524587621574@abdm");
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any()))
                .thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(accountAuthMethodService.addAccountAuthMethods(any()))
                .thenReturn(Mono.just(Collections.singletonList(authMethodsDto)));
        when(jwtUtil.generateToken(any(),any()))
                .thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any()))
                .thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime())
                .thenReturn(1296000L);
        when(accountService.settingClientIdAndOrigin(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any()))
                .thenReturn(Mono.just("success"));
        when(redisOtpMock.getReceiver())
                .thenReturn("1234");
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.error(Exception::new));
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));

        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.FACE);
        faceDto.setAadhaar(AADHAAR_NUMBER);
        faceDto.setRdPidData(PID);
        faceDto.setMobile("9887656789");
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        authData.setAuthMethods(authMethods);
        authData.setFace(faceDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        StepVerifier.create(enrolUsingAadhaarService.faceAuth(enrolByAadhaarRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void faceAuthSuccessAccStatusDeleted2()
    {
        ReflectionTestUtils.setField(enrolUsingAadhaarService,"isTransactionManagementEnable",true);
        transactionDto.setMobile("******6789");

        accountDto.setStatus(AccountStatus.DELETED.getValue());
        accountDto.setYearOfBirth("2023");
        when(accountService.getMobileLinkedAccountCount(any()))
                .thenReturn(Mono.just(-1));
        aadhaarResponseDto.setStatus("success");
        aadhaarUserKycDto.setStatus("success");
        aadhaarUserKycDto.setSignature("");
        aadhaarResponseDto.setAadhaarUserKycDto(aadhaarUserKycDto);
        when(aadhaarAppService.faceAuth(any()))
                .thenReturn(Mono.just(aadhaarResponseDto));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.empty());
        when(rsaUtil.decrypt(any()))
                .thenReturn("853123431963");

        transactionDto.setStatus("ACTIVE");
        transactionDto.setTxnId(UUID.fromString("cda04910-37ca-4f2e-84d9-4e5a970d3dc7"));
        transactionDto.setAadharNo("omJXDzhTJYvtCVhy+hlXSdZ3GW9ZIHMHz1FxQZnwl/oQlV9TSHFxU0CXW7ncj2JXYWGLEJjlrqJXqEXcWUQiktcNYrQ6xEviLepYv50KsvTcHJ2UJjaWQiZrTks0XrPPCeKuOLqdVz/+1Z2r5xupNOxNCkSuYTi9RIO2ZR394zCo9pT2kqoWB9E13g33EO+FubWRr5JEHK5Whcn/pY1kKkLClwsQMuBjV8RRAoVPOxtOPSgjq6j5U2C9Xca55b4xoseMRgYMu8yOWBBYeVnmbFtW8F5nfj42gXkx/QIK/DBogVZTm+zpk7amZnC990RcBmm9Qa1IIYH6VvFPjW8NgE+zLz53cI3fHmjQtcHLKe8nvm3lxFtqTP/vJavcF5EmcBaZuQ9/d4Tb3QrXvdvR4X6NIlzIIvclVZY3PS4Rn0gDhUxbkKwXEMm1qq4Bt0yjNike+Ox8766ELnFw5/+E+7Q0AAVTYgkRYB9J7iZ7QDKGxpvvQ6qKQtJ/5AViV4wQe/Hi/joxCM2zatJ6A3F97FG9ebffMqYPzw2mQrzXkerE1VFhm3kGg6qX3Vrf4zYm8b5KzJjQi8MzNX7u5yxO3FpPxdk2vjKV7w70Xr+fRTLhRKZug7F7gHV0aOE7+JzIVHPHy5McYG9ZvvM8PDdmXuR8g/5hD+c9M/D3KxwkXZI=");
        when(transactionService.createTransactionEntity(any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.just(accountDto));
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(Collections.singletonList(lgdDistrictResponse)));
        when(transactionService.findTransactionDetailsFromDB(any()))
                .thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any()))
                .thenReturn(Mono.just(transactionDto));

        accountDto.setYearOfBirth("1995");
        accountDto.setMonthOfBirth("09");
        accountDto.setDayOfBirth("29");
        when(accountService.prepareNewAccount(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));

        when(abhaAddressGenerator.generateDefaultAbhaAddress(any()))
                .thenReturn("76524587621574@abdm");
        transactionDto.setMobile("******6789");
        when(transactionService.updateTransactionEntity(any(),any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        notificationResponseDto.setStatus("sent");
        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));

        hidPhrAddressDto.setHealthIdNumber("76-5245-8762-1574");
        hidPhrAddressDto.setPhrAddress("76524587621574@abdm");
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any()))
                .thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(accountAuthMethodService.addAccountAuthMethods(any()))
                .thenReturn(Mono.just(Collections.singletonList(authMethodsDto)));
        when(jwtUtil.generateToken(any(),any()))
                .thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any()))
                .thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime())
                .thenReturn(1296000L);
        when(accountService.settingClientIdAndOrigin(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any()))
                .thenReturn(Mono.just("success"));
        when(redisOtpMock.getReceiver())
                .thenReturn("1234");
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.error(Exception::new));

        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.FACE);
        faceDto.setAadhaar(AADHAAR_NUMBER);
        faceDto.setRdPidData(PID);
        faceDto.setMobile("9887656789");
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        authData.setAuthMethods(authMethods);
        authData.setFace(faceDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));

        StepVerifier.create(enrolUsingAadhaarService.faceAuth(enrolByAadhaarRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void faceAuthSuccessAccStatusDeleted3()
    {
        ReflectionTestUtils.setField(enrolUsingAadhaarService,"isTransactionManagementEnable",true);
        transactionDto.setMobile("******6789");

        accountDto.setStatus(AccountStatus.DELETED.getValue());
        accountDto.setYearOfBirth("2023");
        when(accountService.getMobileLinkedAccountCount(any()))
                .thenReturn(Mono.just(10));
        aadhaarResponseDto.setStatus("success");
        aadhaarUserKycDto.setStatus("success");
        aadhaarUserKycDto.setSignature("");
        aadhaarResponseDto.setAadhaarUserKycDto(aadhaarUserKycDto);
        when(aadhaarAppService.faceAuth(any()))
                .thenReturn(Mono.just(aadhaarResponseDto));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.empty());
        when(rsaUtil.decrypt(any()))
                .thenReturn("853123431963");

        transactionDto.setStatus("ACTIVE");
        transactionDto.setTxnId(UUID.fromString("cda04910-37ca-4f2e-84d9-4e5a970d3dc7"));
        transactionDto.setAadharNo("omJXDzhTJYvtCVhy+hlXSdZ3GW9ZIHMHz1FxQZnwl/oQlV9TSHFxU0CXW7ncj2JXYWGLEJjlrqJXqEXcWUQiktcNYrQ6xEviLepYv50KsvTcHJ2UJjaWQiZrTks0XrPPCeKuOLqdVz/+1Z2r5xupNOxNCkSuYTi9RIO2ZR394zCo9pT2kqoWB9E13g33EO+FubWRr5JEHK5Whcn/pY1kKkLClwsQMuBjV8RRAoVPOxtOPSgjq6j5U2C9Xca55b4xoseMRgYMu8yOWBBYeVnmbFtW8F5nfj42gXkx/QIK/DBogVZTm+zpk7amZnC990RcBmm9Qa1IIYH6VvFPjW8NgE+zLz53cI3fHmjQtcHLKe8nvm3lxFtqTP/vJavcF5EmcBaZuQ9/d4Tb3QrXvdvR4X6NIlzIIvclVZY3PS4Rn0gDhUxbkKwXEMm1qq4Bt0yjNike+Ox8766ELnFw5/+E+7Q0AAVTYgkRYB9J7iZ7QDKGxpvvQ6qKQtJ/5AViV4wQe/Hi/joxCM2zatJ6A3F97FG9ebffMqYPzw2mQrzXkerE1VFhm3kGg6qX3Vrf4zYm8b5KzJjQi8MzNX7u5yxO3FpPxdk2vjKV7w70Xr+fRTLhRKZug7F7gHV0aOE7+JzIVHPHy5McYG9ZvvM8PDdmXuR8g/5hD+c9M/D3KxwkXZI=");
        when(transactionService.createTransactionEntity(any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.just(accountDto));
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(Collections.singletonList(lgdDistrictResponse)));
        when(transactionService.findTransactionDetailsFromDB(any()))
                .thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any()))
                .thenReturn(Mono.just(transactionDto));

        accountDto.setYearOfBirth("1995");
        accountDto.setMonthOfBirth("09");
        accountDto.setDayOfBirth("29");
        when(accountService.prepareNewAccount(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));

        when(abhaAddressGenerator.generateDefaultAbhaAddress(any()))
                .thenReturn("76524587621574@abdm");
        transactionDto.setMobile("******6789");
        when(transactionService.updateTransactionEntity(any(),any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        notificationResponseDto.setStatus("sent");
        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));

        hidPhrAddressDto.setHealthIdNumber("76-5245-8762-1574");
        hidPhrAddressDto.setPhrAddress("76524587621574@abdm");
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any()))
                .thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(accountAuthMethodService.addAccountAuthMethods(any()))
                .thenReturn(Mono.just(Collections.singletonList(authMethodsDto)));
        when(jwtUtil.generateToken(any(),any()))
                .thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any()))
                .thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime())
                .thenReturn(1296000L);
        when(accountService.settingClientIdAndOrigin(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any()))
                .thenReturn(Mono.just("success"));
        when(redisOtpMock.getReceiver())
                .thenReturn("1234");
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.error(Exception::new));

        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.FACE);
        faceDto.setAadhaar(AADHAAR_NUMBER);
        faceDto.setRdPidData(PID);
        faceDto.setMobile("");
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        authData.setAuthMethods(authMethods);
        authData.setFace(faceDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));

        StepVerifier.create(enrolUsingAadhaarService.faceAuth(enrolByAadhaarRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }

    @Test
    void faceAuthSuccessAccStatusDeletedError()
    {
        ReflectionTestUtils.setField(enrolUsingAadhaarService,"isTransactionManagementEnable",true);
        transactionDto.setMobile("******6789");

        accountDto.setStatus(AccountStatus.DELETED.getValue());
        accountDto.setYearOfBirth("2023");
        when(accountService.getMobileLinkedAccountCount(any()))
                .thenReturn(Mono.just(10));
        aadhaarResponseDto.setStatus("success");
        aadhaarUserKycDto.setStatus("success");
        aadhaarUserKycDto.setSignature("");
        aadhaarResponseDto.setAadhaarUserKycDto(aadhaarUserKycDto);
        when(aadhaarAppService.faceAuth(any()))
                .thenReturn(Mono.just(aadhaarResponseDto));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.empty());
        when(rsaUtil.decrypt(any()))
                .thenReturn("853123431963");

        transactionDto.setStatus("ACTIVE");
        transactionDto.setTxnId(UUID.fromString("cda04910-37ca-4f2e-84d9-4e5a970d3dc7"));
        transactionDto.setAadharNo("omJXDzhTJYvtCVhy+hlXSdZ3GW9ZIHMHz1FxQZnwl/oQlV9TSHFxU0CXW7ncj2JXYWGLEJjlrqJXqEXcWUQiktcNYrQ6xEviLepYv50KsvTcHJ2UJjaWQiZrTks0XrPPCeKuOLqdVz/+1Z2r5xupNOxNCkSuYTi9RIO2ZR394zCo9pT2kqoWB9E13g33EO+FubWRr5JEHK5Whcn/pY1kKkLClwsQMuBjV8RRAoVPOxtOPSgjq6j5U2C9Xca55b4xoseMRgYMu8yOWBBYeVnmbFtW8F5nfj42gXkx/QIK/DBogVZTm+zpk7amZnC990RcBmm9Qa1IIYH6VvFPjW8NgE+zLz53cI3fHmjQtcHLKe8nvm3lxFtqTP/vJavcF5EmcBaZuQ9/d4Tb3QrXvdvR4X6NIlzIIvclVZY3PS4Rn0gDhUxbkKwXEMm1qq4Bt0yjNike+Ox8766ELnFw5/+E+7Q0AAVTYgkRYB9J7iZ7QDKGxpvvQ6qKQtJ/5AViV4wQe/Hi/joxCM2zatJ6A3F97FG9ebffMqYPzw2mQrzXkerE1VFhm3kGg6qX3Vrf4zYm8b5KzJjQi8MzNX7u5yxO3FpPxdk2vjKV7w70Xr+fRTLhRKZug7F7gHV0aOE7+JzIVHPHy5McYG9ZvvM8PDdmXuR8g/5hD+c9M/D3KxwkXZI=");
        when(transactionService.createTransactionEntity(any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.just(accountDto));
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(Collections.singletonList(lgdDistrictResponse)));
        when(transactionService.findTransactionDetailsFromDB(any()))
                .thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any()))
                .thenReturn(Mono.just(transactionDto));

        accountDto.setYearOfBirth("1995");
        accountDto.setMonthOfBirth("09");
        accountDto.setDayOfBirth("29");
        when(accountService.prepareNewAccount(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));

        when(abhaAddressGenerator.generateDefaultAbhaAddress(any()))
                .thenReturn("76524587621574@abdm");
        transactionDto.setMobile("******6789");
        when(transactionService.updateTransactionEntity(any(),any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        notificationResponseDto.setStatus("sent");
        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));

        hidPhrAddressDto.setHealthIdNumber("76-5245-8762-1574");
        hidPhrAddressDto.setPhrAddress("76524587621574@abdm");
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any()))
                .thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(accountAuthMethodService.addAccountAuthMethods(any()))
                .thenReturn(Mono.just(Collections.singletonList(authMethodsDto)));
        when(jwtUtil.generateToken(any(),any()))
                .thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any()))
                .thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime())
                .thenReturn(1296000L);
        when(accountService.settingClientIdAndOrigin(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any()))
                .thenReturn(Mono.just("success"));
        when(redisOtpMock.getReceiver())
                .thenReturn("1234");
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.error(Exception::new));

        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.FACE);
        faceDto.setAadhaar(AADHAAR_NUMBER);
        faceDto.setRdPidData(PID);
        faceDto.setMobile("9887656789");
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        authData.setAuthMethods(authMethods);
        authData.setFace(faceDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));

        StepVerifier.create(enrolUsingAadhaarService.faceAuth(enrolByAadhaarRequestDto,requestHeaders))
                .expectError(AbhaUnProcessableException.class)
                .verify();
    }
    @Test
    void verifyAadhaarOtpSuccess2()
    {
        ReflectionTestUtils.setField(enrolUsingAadhaarService,"maxMobileLinkingCount",1);
        ReflectionTestUtils.setField(enrolUsingAadhaarService,"isTransactionManagementEnable",true);
        transactionDto.setMobile("******8510");
        when(redisService.getRedisOtp(any())).thenReturn(redisOtp);
        when(redisService.isMultipleOtpVerificationAllowed(any())).thenReturn(true);
        when(accountService.getMobileLinkedAccountCount(any()))
                .thenReturn(Mono.just(-1));
        when(rsaUtil.encrypt(any())).thenReturn(AADHAAR_NUMBER);

        when(aadhaarAppService.verifyOtp(any()))
                .thenReturn(Mono.just(aadhaarResponseDto));


        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.empty());
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(Collections.singletonList(lgdDistrictResponse)));

        when(accountService.prepareNewAccount(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());

        when(abhaAddressGenerator.generateDefaultAbhaAddress(any()))
                .thenReturn("76524587621574@abdm");
        when(transactionService.findTransactionDetailsFromDB(any()))
                .thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any()))
                .thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(accountAuthMethodService.addAccountAuthMethods(any()))
                .thenReturn(Mono.just(Collections.singletonList(authMethodsDto)));


        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any()))
                .thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any()))
                .thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime())
                .thenReturn(1296000L);

        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));

        Mono<EnrolByAadhaarResponseDto> responseDtoMono
                = enrolUsingAadhaarService.verifyOtp(enrolByAadhaarRequestDto,requestHeaders);

        when(transactionService.createTransactionEntity(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.empty());
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());
        //when(MapperUtils.mapKycDetails(any(),any())).thenReturn(new ABHAProfileDto());
        //when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(lgdDistrictResponses));
        when(accountService.prepareNewAccount(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("success"));
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));
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
    void verifyAadhaarOtpSuccesselse()
    {
        ReflectionTestUtils.setField(enrolUsingAadhaarService,"maxMobileLinkingCount",1);
        ReflectionTestUtils.setField(enrolUsingAadhaarService,"isTransactionManagementEnable",true);
        transactionDto.setMobile("******6789");
        when(redisService.getRedisOtp(any())).thenReturn(redisOtp);
        when(redisService.isMultipleOtpVerificationAllowed(any())).thenReturn(true);
        when(accountService.getMobileLinkedAccountCount(any()))
                .thenReturn(Mono.just(-1));
        when(rsaUtil.encrypt(any())).thenReturn(AADHAAR_NUMBER);

        when(aadhaarAppService.verifyOtp(any()))
                .thenReturn(Mono.just(aadhaarResponseDto));


        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.empty());
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(Collections.singletonList(lgdDistrictResponse)));

        when(accountService.prepareNewAccount(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());

        when(abhaAddressGenerator.generateDefaultAbhaAddress(any()))
                .thenReturn("76524587621574@abdm");
        when(transactionService.findTransactionDetailsFromDB(any()))
                .thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any()))
                .thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(accountAuthMethodService.addAccountAuthMethods(any()))
                .thenReturn(Mono.just(Collections.singletonList(authMethodsDto)));


        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any()))
                .thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any()))
                .thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime())
                .thenReturn(1296000L);

        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));

        Mono<EnrolByAadhaarResponseDto> responseDtoMono
                = enrolUsingAadhaarService.verifyOtp(enrolByAadhaarRequestDto,requestHeaders);

        when(transactionService.createTransactionEntity(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.empty());
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());
        //when(MapperUtils.mapKycDetails(any(),any())).thenReturn(new ABHAProfileDto());
        //when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(lgdDistrictResponses));
        when(accountService.prepareNewAccount(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("success"));
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));
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
    void verifyAadhaarOtpSuccessErrors()
    {
        ReflectionTestUtils.setField(enrolUsingAadhaarService,"maxMobileLinkingCount",1);
        ReflectionTestUtils.setField(enrolUsingAadhaarService,"isTransactionManagementEnable",true);
        transactionDto.setMobile("******6789");
        aadhaarResponseDto.setStatus("test");
        when(redisService.getRedisOtp(any())).thenReturn(redisOtp);
        when(redisService.isMultipleOtpVerificationAllowed(any())).thenReturn(true);
        when(accountService.getMobileLinkedAccountCount(any()))
                .thenReturn(Mono.just(-1));
        when(rsaUtil.encrypt(any())).thenReturn(AADHAAR_NUMBER);

        when(aadhaarAppService.verifyOtp(any()))
                .thenReturn(Mono.just(aadhaarResponseDto));


        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.empty());
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(Collections.singletonList(lgdDistrictResponse)));

        when(accountService.prepareNewAccount(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());

        when(abhaAddressGenerator.generateDefaultAbhaAddress(any()))
                .thenReturn("76524587621574@abdm");
        when(transactionService.findTransactionDetailsFromDB(any()))
                .thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any()))
                .thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(accountAuthMethodService.addAccountAuthMethods(any()))
                .thenReturn(Mono.just(Collections.singletonList(authMethodsDto)));


        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any()))
                .thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any()))
                .thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime())
                .thenReturn(1296000L);

        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));

        Mono<EnrolByAadhaarResponseDto> responseDtoMono
                = enrolUsingAadhaarService.verifyOtp(enrolByAadhaarRequestDto,requestHeaders);

        when(transactionService.createTransactionEntity(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.empty());
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());
        //when(MapperUtils.mapKycDetails(any(),any())).thenReturn(new ABHAProfileDto());
        //when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(lgdDistrictResponses));
        when(accountService.prepareNewAccount(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("success"));
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));
        StepVerifier.create(responseDtoMono)
                .expectError(AadhaarExceptions.class).verify();
    }
    @Test
    void verifyAadhaarOtpSuccessErrors2()
    {
        ReflectionTestUtils.setField(enrolUsingAadhaarService,"maxMobileLinkingCount",1);
        ReflectionTestUtils.setField(enrolUsingAadhaarService,"isTransactionManagementEnable",true);
        transactionDto.setMobile("******6789");
        aadhaarResponseDto.setAadhaarAuthOtpDto(new AadhaarAuthOtpDto());
        aadhaarResponseDto.setStatus("test");
        when(redisService.getRedisOtp(any())).thenReturn(redisOtp);
        when(redisService.isMultipleOtpVerificationAllowed(any())).thenReturn(true);
        when(accountService.getMobileLinkedAccountCount(any()))
                .thenReturn(Mono.just(-1));
        when(rsaUtil.encrypt(any())).thenReturn(AADHAAR_NUMBER);

        when(aadhaarAppService.verifyOtp(any()))
                .thenReturn(Mono.just(aadhaarResponseDto));


        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.empty());
        when(lgdUtility.getLgdData(any(),any()))
                .thenReturn(Mono.just(Collections.singletonList(lgdDistrictResponse)));

        when(accountService.prepareNewAccount(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(deDuplicationService.checkDeDuplication(any()))
                .thenReturn(Mono.empty());

        when(abhaAddressGenerator.generateDefaultAbhaAddress(any()))
                .thenReturn("76524587621574@abdm");
        when(transactionService.findTransactionDetailsFromDB(any()))
                .thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any()))
                .thenReturn(Mono.just(transactionDto));
        when(accountService.createAccountEntity(any(),any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any(),any()))
                .thenReturn(hidPhrAddressDto);
        when(hidPhrAddressService.createHidPhrAddressEntity(any()))
                .thenReturn(Mono.just(hidPhrAddressDto));
        when(accountAuthMethodService.addAccountAuthMethods(any()))
                .thenReturn(Mono.just(Collections.singletonList(authMethodsDto)));


        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any()))
                .thenReturn(TOKEN);
        when(jwtUtil.jwtTokenExpiryTime()).thenReturn(1800L);
        when(jwtUtil.generateRefreshToken(any()))
                .thenReturn(REFRESH_TOKEN);
        when(jwtUtil.jwtRefreshTokenExpiryTime())
                .thenReturn(1296000L);

        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));

        Mono<EnrolByAadhaarResponseDto> responseDtoMono
                = enrolUsingAadhaarService.verifyOtp(enrolByAadhaarRequestDto,requestHeaders);

        when(transactionService.createTransactionEntity(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any())).thenReturn(Mono.just(transactionDto));
        when(accountService.findByXmlUid(any())).thenReturn(Mono.empty());
        when(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(any(),any())).thenReturn(Flux.just(hidPhrAddressDto));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());
        //when(MapperUtils.mapKycDetails(any(),any())).thenReturn(new ABHAProfileDto());
        //when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(lgdDistrictResponses));
        when(accountService.prepareNewAccount(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("success"));
        when(aadhaarAppService.verifyDemographicDetails(any())).thenReturn(Mono.just(verifyDemographicResponse));
        receiverOtpTracker=new ReceiverOtpTracker("",1,1,true);
        when(redisService.isReceiverOtpTrackerAvailable(any())).thenReturn(true);
        when(redisService.getReceiverOtpTracker(any())).thenReturn(receiverOtpTracker);
        //when(redisService.)
        StepVerifier.create(responseDtoMono)
                .expectError(AadhaarExceptions.class).verify();
    }








}
