package in.gov.abdm.abha.enrollment.services.enrol;

import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarUserKycDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.LocalizedDetails;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicResponse;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.*;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentRequestDto;
import in.gov.abdm.abha.enrollment.model.entities.*;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import in.gov.abdm.abha.enrollment.model.nepix.VerifyDLResponse;
import in.gov.abdm.abha.enrollment.model.notification.NotificationResponseDto;
import in.gov.abdm.abha.enrollment.model.notification.SendNotificationRequestDto;
import in.gov.abdm.abha.enrollment.model.redis.otp.ReceiverOtpTracker;
import in.gov.abdm.abha.enrollment.model.redis.otp.RedisOtp;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.account_auth_methods.AccountAuthMethodService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.de_duplication.DeDuplicationService;
import in.gov.abdm.abha.enrollment.services.document.DocumentAppService;
import in.gov.abdm.abha.enrollment.services.document.IdentityDocumentDBService;
import in.gov.abdm.abha.enrollment.services.enrol.document.EnrolUsingDrivingLicence;
import in.gov.abdm.abha.enrollment.services.notification.NotificationService;
import in.gov.abdm.abha.enrollment.utilities.EnrolmentCipher;
import in.gov.abdm.abha.enrollment.utilities.LgdUtility;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaAddressGenerator;
import in.gov.abdm.abha.enrollment.utilities.jwt.JWTUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class EnrolUsingDrivingLicenceTests {
    @InjectMocks
    EnrolUsingDrivingLicence enrolUsingDrivingLicence;
    @Mock
    TransactionService transactionService;
    @Mock
    AccountService accountService;
    @Mock
    HidPhrAddressService hidPhrAddressService;
    @Mock
    AccountAuthMethodService accountAuthMethodService;
    @Mock
    AbhaAddressGenerator abhaAddressGenerator;
    @Mock
    DocumentAppService documentAppService;
    @Mock
    IdentityDocumentDBService identityDocumentDBService;
    @Mock
    NotificationService notificationService;
    @Mock
    EnrolmentCipher enrolmentCipher;
    @Mock
    JWTUtil jwtUtil;
    @Mock
    LgdUtility lgdUtility;
    @Mock
    DeDuplicationService deDuplicationService;
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
        aadhaarResponseDto.setAadhaarUserKycDto(aadhaarUserKycDto);
        transactionDto.setTxnId(UUID.fromString("cda04910-37ca-4f2e-84d9-4e5a970d3dc7"));
        transactionDto.setMobile("7084248510");
        accountDto.setHealthIdNumber("12321");
        accountDto.setStatus(AccountStatus.DELETED.getValue());
        accountDto.setDayOfBirth("12");
        accountDto.setMonthOfBirth("05");
        accountDto.setYearOfBirth("2000");
        accountDto.setStateCode("1");
        accountDto.setDistrictCode("2");
        hidPhrAddressDto.setHealthIdNumber("76-5245-8762-1574");
        hidPhrAddressDto.setPhrAddress("76524587621574@abdm");
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
        notificationResponseDto.setStatus("sent");
        accountDto.setVerificationStatus("VERIFIED");
        transactionDto.setMobileVerified(true);
        transactionDto.setMobile("1234567890");
        verifyDLResponse.setAuthResult("success");
        lgdDistrictResponse.setDistrictCode("177");
        lgdDistrictResponse.setDistrictName("SAHARANPUR");
        lgdDistrictResponse.setStateCode("9");
        lgdDistrictResponse.setStateName("UTTAR PRADESH");
        lgdDistrictResponse.setPinCode("432727");
        String s = lgdDistrictResponse.getPinCode();
        transactionDto.setTxnId(UUID.fromString("672e449e-c05a-4a28-be31-37ed60c8b6d6"));

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
        requestHeaders.setFTokenClaims(null);



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
    void verifyAndCreateAccountSuccessfacilityService()
    {

        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.getAccountByDocumentCode(any())).thenReturn(Mono.empty());
        when(documentAppService.verify(any())).thenReturn(Mono.just(verifyDLResponse));
        when(deDuplicationService.checkDeDuplication(any())).thenReturn(Mono.empty());
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(Collections.singletonList(lgdDistrictResponse)));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(enrolmentCipher.encrypt(any())).thenReturn("");
        when(identityDocumentDBService.addIdentityDocuments(any())).thenReturn(Mono.just(identityDocumentsDto));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(Collections.singletonList(authMethodsDto)));
        when(transactionService.deleteTransactionEntity(String.valueOf(transactionDto.getTxnId()))).thenReturn(Mono.just(new ResponseEntity<>(HttpStatus.OK)));
        when(accountService.getAccountByDocumentCode(any())).thenReturn(Mono.just(accountDto));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());
        when(notificationService.sendEnrollCreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        StepVerifier.create(enrolUsingDrivingLicence.verifyAndCreateAccount(enrolByDocumentRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyAndCreateAccountSuccessfacilityServicetrue()
    {
        Map<String,Object> ftkn = new HashMap<>();
        ftkn.put("a","OFFLaINE_HID");
        ftkn.put("sub","OFFLINE_HID");
        requestHeaders.setFTokenClaims(ftkn);
        accountDto.setVerificationStatus("VERIFIED");
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.getAccountByDocumentCode(any())).thenReturn(Mono.empty());
        when(documentAppService.verify(any())).thenReturn(Mono.just(verifyDLResponse));
        when(deDuplicationService.checkDeDuplication(any())).thenReturn(Mono.empty());
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(Collections.singletonList(lgdDistrictResponse)));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(enrolmentCipher.encrypt(any())).thenReturn("");
        when(identityDocumentDBService.addIdentityDocuments(any())).thenReturn(Mono.just(identityDocumentsDto));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(Collections.singletonList(authMethodsDto)));
        when(transactionService.deleteTransactionEntity(String.valueOf(transactionDto.getTxnId()))).thenReturn(Mono.just(new ResponseEntity<>(HttpStatus.OK)));
        when(accountService.getAccountByDocumentCode(any())).thenReturn(Mono.just(accountDto));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());
        when(notificationService.sendEnrollCreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(notificationService.sendABHACreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        StepVerifier.create(enrolUsingDrivingLicence.verifyAndCreateAccount(enrolByDocumentRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyAndCreateAccountSuccessfacilityServiceFalse()
    {
        Map<String,Object> ftkn = new HashMap<>();

        requestHeaders.setFTokenClaims(null);
        ReflectionTestUtils.setField(enrolUsingDrivingLicence,"isDLTransactionManagementEnable",true);
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.getAccountByDocumentCode(any())).thenReturn(Mono.empty());
        when(documentAppService.verify(any())).thenReturn(Mono.just(verifyDLResponse));
        when(deDuplicationService.checkDeDuplication(any())).thenReturn(Mono.empty());
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(Collections.singletonList(lgdDistrictResponse)));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(enrolmentCipher.encrypt(any())).thenReturn("");
        when(identityDocumentDBService.addIdentityDocuments(any())).thenReturn(Mono.just(identityDocumentsDto));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(Collections.singletonList(authMethodsDto)));
        when(transactionService.deleteTransactionEntity(String.valueOf(transactionDto.getTxnId()))).thenReturn(Mono.just(new ResponseEntity<>(HttpStatus.OK)));
        when(accountService.getAccountByDocumentCode(any())).thenReturn(Mono.just(accountDto));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.empty());
        when(notificationService.sendEnrollCreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(notificationService.sendABHACreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any())).thenReturn(hidPhrAddressDto);
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("saved"));

        StepVerifier.create(enrolUsingDrivingLicence.verifyAndCreateAccount(enrolByDocumentRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyAndCreateAccountSuccessfacilityServiceFalse2()
    {
        Map<String,Object> ftkn = new HashMap<>();
        ftkn.put("a","OFFLaINE_HID");
        ftkn.put("sub","OFFLINE_HID");
        requestHeaders.setFTokenClaims(ftkn);
        accountDto.setStatus(AccountStatus.DEACTIVATED.toString());
        ReflectionTestUtils.setField(enrolUsingDrivingLicence,"isDLTransactionManagementEnable",true);
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.getAccountByDocumentCode(any())).thenReturn(Mono.empty());
        when(documentAppService.verify(any())).thenReturn(Mono.just(verifyDLResponse));
        when(deDuplicationService.checkDeDuplication(any())).thenReturn(Mono.empty());
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(Collections.singletonList(lgdDistrictResponse)));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(enrolmentCipher.encrypt(any())).thenReturn("");
        when(identityDocumentDBService.addIdentityDocuments(any())).thenReturn(Mono.just(identityDocumentsDto));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(Collections.singletonList(authMethodsDto)));
        when(transactionService.deleteTransactionEntity(String.valueOf(transactionDto.getTxnId()))).thenReturn(Mono.just(new ResponseEntity<>(HttpStatus.OK)));
        when(accountService.getAccountByDocumentCode(any())).thenReturn(Mono.just(accountDto));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.error(Exception::new));
        when(notificationService.sendEnrollCreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(notificationService.sendABHACreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any())).thenReturn(hidPhrAddressDto);
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("saved"));

        StepVerifier.create(enrolUsingDrivingLicence.verifyAndCreateAccount(enrolByDocumentRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyAndCreateAccountSuccessfacilityServiceFalse3()
    {
        Map<String,Object> ftkn = new HashMap<>();

        requestHeaders.setFTokenClaims(null);
        accountDto.setStatus(AccountStatus.DEACTIVATED.toString());
        ReflectionTestUtils.setField(enrolUsingDrivingLicence,"isDLTransactionManagementEnable",true);
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.getAccountByDocumentCode(any())).thenReturn(Mono.empty());
        when(documentAppService.verify(any())).thenReturn(Mono.just(verifyDLResponse));
        when(deDuplicationService.checkDeDuplication(any())).thenReturn(Mono.empty());
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(Collections.singletonList(lgdDistrictResponse)));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(enrolmentCipher.encrypt(any())).thenReturn("");
        when(identityDocumentDBService.addIdentityDocuments(any())).thenReturn(Mono.just(identityDocumentsDto));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(Collections.singletonList(authMethodsDto)));
        when(transactionService.deleteTransactionEntity(String.valueOf(transactionDto.getTxnId()))).thenReturn(Mono.just(new ResponseEntity<>(HttpStatus.OK)));
        when(accountService.getAccountByDocumentCode(any())).thenReturn(Mono.just(accountDto));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.error(Exception::new));
        when(notificationService.sendEnrollCreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(notificationService.sendABHACreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any())).thenReturn(hidPhrAddressDto);
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("saved"));

        StepVerifier.create(enrolUsingDrivingLicence.verifyAndCreateAccount(enrolByDocumentRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyAndCreateAccountSuccessAccStatusActive()
    {
        Map<String,Object> ftkn = new HashMap<>();

        requestHeaders.setFTokenClaims(null);
        accountDto.setStatus(AccountStatus.ACTIVE.toString());
        ReflectionTestUtils.setField(enrolUsingDrivingLicence,"isDLTransactionManagementEnable",true);
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.getAccountByDocumentCode(any())).thenReturn(Mono.empty());
        when(documentAppService.verify(any())).thenReturn(Mono.just(verifyDLResponse));
        when(deDuplicationService.checkDeDuplication(any())).thenReturn(Mono.empty());
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(Collections.singletonList(lgdDistrictResponse)));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(enrolmentCipher.encrypt(any())).thenReturn("");
        when(identityDocumentDBService.addIdentityDocuments(any())).thenReturn(Mono.just(identityDocumentsDto));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(Collections.singletonList(authMethodsDto)));
        when(transactionService.deleteTransactionEntity(String.valueOf(transactionDto.getTxnId()))).thenReturn(Mono.just(new ResponseEntity<>(HttpStatus.OK)));
        when(accountService.getAccountByDocumentCode(any())).thenReturn(Mono.just(accountDto));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.error(Exception::new));
        when(notificationService.sendEnrollCreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(notificationService.sendABHACreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any())).thenReturn(hidPhrAddressDto);
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("saved"));

        StepVerifier.create(enrolUsingDrivingLicence.verifyAndCreateAccount(enrolByDocumentRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyAndCreateAccountSuccessAccStatus1()
    {
        Map<String,Object> ftkn = new HashMap<>();

        requestHeaders.setFTokenClaims(null);
        accountDto.setStatus(AccountStatus.ACTIVE.toString());
        ReflectionTestUtils.setField(enrolUsingDrivingLicence,"isDLTransactionManagementEnable",true);
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.getAccountByDocumentCode(any())).thenReturn(Mono.empty());
        when(documentAppService.verify(any())).thenReturn(Mono.just(verifyDLResponse));
        when(deDuplicationService.checkDeDuplication(any())).thenReturn(Mono.empty());
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(Collections.singletonList(lgdDistrictResponse)));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(enrolmentCipher.encrypt(any())).thenReturn("");
        when(identityDocumentDBService.addIdentityDocuments(any())).thenReturn(Mono.just(identityDocumentsDto));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(Collections.singletonList(authMethodsDto)));
        when(transactionService.deleteTransactionEntity(String.valueOf(transactionDto.getTxnId()))).thenReturn(Mono.just(new ResponseEntity<>(HttpStatus.OK)));
        //when(accountService.getAccountByDocumentCode(any())).thenReturn(Mono.just(accountDto));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.error(Exception::new));
        when(notificationService.sendEnrollCreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(notificationService.sendABHACreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any())).thenReturn(hidPhrAddressDto);
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("saved"));

        StepVerifier.create(enrolUsingDrivingLicence.verifyAndCreateAccount(enrolByDocumentRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyAndCreateAccountSuccessAccStatus2()
    {
        verifyDLResponse.setAuthResult("unsuccessful");
        requestHeaders.setFTokenClaims(null);
        accountDto.setStatus(AccountStatus.ACTIVE.toString());
        ReflectionTestUtils.setField(enrolUsingDrivingLicence,"isDLTransactionManagementEnable",true);
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.getAccountByDocumentCode(any())).thenReturn(Mono.empty());
        when(documentAppService.verify(any())).thenReturn(Mono.just(verifyDLResponse));
        when(deDuplicationService.checkDeDuplication(any())).thenReturn(Mono.empty());
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(Collections.singletonList(lgdDistrictResponse)));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(enrolmentCipher.encrypt(any())).thenReturn("");
        when(identityDocumentDBService.addIdentityDocuments(any())).thenReturn(Mono.just(identityDocumentsDto));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(Collections.singletonList(authMethodsDto)));
        when(transactionService.deleteTransactionEntity(String.valueOf(transactionDto.getTxnId()))).thenReturn(Mono.just(new ResponseEntity<>(HttpStatus.OK)));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.error(Exception::new));
        when(notificationService.sendEnrollCreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(notificationService.sendABHACreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any())).thenReturn(hidPhrAddressDto);
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("saved"));

        StepVerifier.create(enrolUsingDrivingLicence.verifyAndCreateAccount(enrolByDocumentRequestDto,requestHeaders))
                .expectError()
                .verify();
    }
    @Test
    void verifyAndCreateAccountSuccessAccStatus3()
    {
        Map<String,Object> ftkn = new HashMap<>();
        requestHeaders.setFTokenClaims(null);
        accountDto.setStatus(AccountStatus.ACTIVE.toString());
        ReflectionTestUtils.setField(enrolUsingDrivingLicence,"isDLTransactionManagementEnable",true);
        when(transactionService.findTransactionDetailsFromDB(any())).thenReturn(Mono.just(transactionDto));
        when(accountService.getAccountByDocumentCode(any())).thenReturn(Mono.empty());
        when(documentAppService.verify(any())).thenReturn(Mono.just(verifyDLResponse));
        when(deDuplicationService.checkDeDuplication(any())).thenReturn(Mono.empty());
        when(lgdUtility.getLgdData(any(),any())).thenReturn(Mono.just(Collections.EMPTY_LIST));
        when(accountService.createAccountEntity(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(hidPhrAddressService.createHidPhrAddressEntity(any())).thenReturn(Mono.just(hidPhrAddressDto));
        when(enrolmentCipher.encrypt(any())).thenReturn("");
        when(identityDocumentDBService.addIdentityDocuments(any())).thenReturn(Mono.just(identityDocumentsDto));
        when(accountAuthMethodService.addAccountAuthMethods(any())).thenReturn(Mono.just(Collections.singletonList(authMethodsDto)));
        when(transactionService.deleteTransactionEntity(String.valueOf(transactionDto.getTxnId()))).thenReturn(Mono.just(new ResponseEntity<>(HttpStatus.OK)));
        when(accountService.reAttemptedAbha(any(),any(),any())).thenReturn(Mono.error(Exception::new));
        when(notificationService.sendEnrollCreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(notificationService.sendABHACreationSMS(any(),any(),any())).thenReturn(Mono.just(notificationResponseDto));
        when(hidPhrAddressService.prepareNewHidPhrAddress(any())).thenReturn(hidPhrAddressDto);
        when(accountService.settingClientIdAndOrigin(any(),any(),any())).thenReturn(Mono.just(accountDto));
        when(accountService.saveAllData(any())).thenReturn(Mono.just("saved"));

        StepVerifier.create(enrolUsingDrivingLicence.verifyAndCreateAccount(enrolByDocumentRequestDto,requestHeaders))
                .expectNextCount(1L)
                .verifyComplete();
    }


}
