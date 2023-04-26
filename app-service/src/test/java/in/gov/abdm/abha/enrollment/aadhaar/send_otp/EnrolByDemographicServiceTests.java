package in.gov.abdm.abha.enrollment.aadhaar.send_otp;

import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarUserKycDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicRequest;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicResponse;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AuthData;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.ConsentDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.OtpDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentRequestDto;
import in.gov.abdm.abha.enrollment.model.entities.*;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import in.gov.abdm.abha.enrollment.model.nepix.VerifyDLResponse;
import in.gov.abdm.abha.enrollment.model.notification.NotificationResponseDto;
import in.gov.abdm.abha.enrollment.model.redis.otp.RedisOtp;
import in.gov.abdm.abha.enrollment.services.aadhaar.AadhaarAppService;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.demographic.EnrolByDemographicService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class EnrolByDemographicServiceTests {

    @InjectMocks
    EnrolByDemographicService enrolByDemographicService;

    @Mock
    RSAUtil rsaUtils;
    @Mock
    AadhaarAppService aadhaarAppService;
    @Mock
    AccountService accountService;

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
    }


    @Test
    void validateAndEnrolByDemoAuthSuccess()
    {
        when(rsaUtils.decrypt(any()))
                .thenReturn("");
        VerifyDemographicResponse verifyDemographicResponse = new VerifyDemographicResponse();
        verifyDemographicResponse.setVerified(true);
        when(aadhaarAppService.verifyDemographicDetails(any()))
                .thenReturn(Mono.just(verifyDemographicResponse));
        when(accountService.findByXmlUid(any()))
                .thenReturn(Mono.just(accountDto));

    }
}
