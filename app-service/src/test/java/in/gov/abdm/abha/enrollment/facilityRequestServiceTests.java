package in.gov.abdm.abha.enrollment;

import in.gov.abdm.abha.enrollment.client.DocumentDBIdentityDocumentFClient;
import in.gov.abdm.abha.enrollment.enums.LoginHint;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.enums.request.OtpSystem;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarAuthOtpDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthData;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.OtpDto;
import in.gov.abdm.abha.enrollment.model.enrol.facility.EnrollmentStatusUpdate;
import in.gov.abdm.abha.enrollment.model.entities.AccountActionDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.IdentityDocumentsDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.notification.NotificationResponseDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.redis.otp.RedisOtp;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.accountaction.AccountActionService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.facility.FacilityEnrolByEnrollmentNumberService;
import in.gov.abdm.abha.enrollment.services.notification.NotificationService;
import in.gov.abdm.abha.enrollment.services.redis.RedisService;
import in.gov.abdm.abha.enrollment.utilities.EnrolmentCipher;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.PROVISIONAL;
import static in.gov.abdm.abha.enrollment.enums.AccountStatus.ACTIVE;
import static java.time.LocalDateTime.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyByte;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class facilityRequestServiceTests {

    @InjectMocks
    FacilityEnrolByEnrollmentNumberService facilityRequestService;
    @Mock
    RSAUtil rsaUtil;
    @Mock
    AccountService accountService;
    @Mock
    AccountActionService accountActionService;
    @Mock
    RedisService redisService;
    @Mock
    NotificationService notificationService;
    @Mock
    TransactionService transactionService;
    @Mock
    DocumentDBIdentityDocumentFClient documentClient;
    @Mock
    EnrolmentCipher enrolmentCipher;
    @Mock
    JWTUtil jwtUtil;

    private MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto;
    private AccountDto accountDto;
    private TransactionDto transactionDto;
    private NotificationResponseDto notificationResponseDto;
    private IdentityDocumentsDto identityDocumentsDto;
    private AuthRequestDto authRequestDto;
    private OtpDto otp;
    private AuthData authData;
    private RedisOtp redisOtp;
    private EnrollmentStatusUpdate enrollmentStatusUpdate;
    private AccountActionDto accountActionDto;

    @BeforeEach
    void setup()
    {
        MockitoAnnotations.openMocks(this);
        transactionDto = new TransactionDto();
        mobileOrEmailOtpRequestDto = new MobileOrEmailOtpRequestDto();
        notificationResponseDto = new NotificationResponseDto();
        accountDto = new AccountDto();
        identityDocumentsDto = new IdentityDocumentsDto();
        authRequestDto = new AuthRequestDto();
        otp = new OtpDto();
        authData = new AuthData();
        redisOtp = new RedisOtp();
        enrollmentStatusUpdate = new EnrollmentStatusUpdate();
        accountActionDto = new AccountActionDto();
    }

    @AfterEach
    void tearDown()
    {
        mobileOrEmailOtpRequestDto = null;
        notificationResponseDto = null;
        accountDto = null;
        identityDocumentsDto = null;
        authRequestDto = null;
        otp = null;
        authData = null;
        redisOtp = null;
        enrollmentStatusUpdate = null;
        accountActionDto = null;
    }

    @Test
    void sendOtpForEnrollmentNumber()
    {
        when(rsaUtil.decrypt("B/UXP3u3rKFCPZ1mFzrMf3ESdPsIHYZB/HdX+QMFeLDwOwtJb5oEE2lQ+Xh6oTApbSYp0slO4SZmpceuQCEU+cMCxoqBhIogCAaEVRhh0mpD936rIi/2KdTNryuHA7KB8r7sumsfaAfV3mndVoLonmNicX3cFXGzp/GZaGhuGv0kwwcj3J9qa1XYhTHHvhmcwuuNRc1MtmKseVHZShuqKn+Ex6aPrA3s9ken3mfWuhRGciscyrqN2evC+ibhGaRtP5o0EAmIi0ETeuiMNjepuZQ+Hm3noeAE6zbV/63ke9QpOkBgSS0J8CRRNcTSEZa2m8MZmuYE9odrSx2OlCWe3Xre9Tom3q21SDdFAp+3ql/aEHJp5Y8poq8pX4o5tOn1smUN+KtdI8qwv4v4/Gh6ZYfqtPxZSwreIKrzxAdhL771xkVdr1155+tiQeKI4kHr/3iUueV5edMqwKsfxovWdLSyftxV3D4yNjkv4G4qaEUk/VrQ47IisnXoV93gR91mHJfg/2ZTG5C/t0lXsh8fveVwmIKGRrwkIrXRBBJ9wI+BwUe0CtLhmIxjy5uIFHdWwXwpkxI2PL07jvFih3MynRa6ZpRPTmJgAvnk4WIsPCifMXu4corDsl9niTjbhntYupRvICGju5uYETDYayosMZniebbvrDEfZQVf75LUXPc="))
                .thenReturn("91-7367-5177-0131");
        accountDto.setVerificationStatus(PROVISIONAL);
        accountDto.setMobile("7065432456");
        when(accountService.getAccountByHealthIdNumber(any()))
                .thenReturn(Mono.just(accountDto));
        when(redisService.isResendOtpAllowed(any())).thenReturn(true);
        notificationResponseDto.setStatus("sent");
        when(notificationService.sendRegistrationOtp(any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));

        transactionDto.setMobile("7065432456");
        transactionDto.setHealthIdNumber("91-7367-5177-0131");
        transactionDto.setCreatedDate(LocalDateTime.now());
        transactionDto.setTxnId(UUID.fromString("cda04910-37ca-4f2e-84d9-4e5a970d3dc7"));
        when(transactionService.createTransactionEntity(any()))
                .thenReturn(Mono.just(transactionDto));

        List<Scopes> scopes = new ArrayList<>();
        scopes.add(Scopes.ABHA_ENROL);
        scopes.add(Scopes.VERIFY_ENROLLMENT);
        mobileOrEmailOtpRequestDto.setScope(scopes);
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.ENROLLMENT);
        mobileOrEmailOtpRequestDto.setLoginId("B/UXP3u3rKFCPZ1mFzrMf3ESdPsIHYZB/HdX+QMFeLDwOwtJb5oEE2lQ+Xh6oTApbSYp0slO4SZmpceuQCEU+cMCxoqBhIogCAaEVRhh0mpD936rIi/2KdTNryuHA7KB8r7sumsfaAfV3mndVoLonmNicX3cFXGzp/GZaGhuGv0kwwcj3J9qa1XYhTHHvhmcwuuNRc1MtmKseVHZShuqKn+Ex6aPrA3s9ken3mfWuhRGciscyrqN2evC+ibhGaRtP5o0EAmIi0ETeuiMNjepuZQ+Hm3noeAE6zbV/63ke9QpOkBgSS0J8CRRNcTSEZa2m8MZmuYE9odrSx2OlCWe3Xre9Tom3q21SDdFAp+3ql/aEHJp5Y8poq8pX4o5tOn1smUN+KtdI8qwv4v4/Gh6ZYfqtPxZSwreIKrzxAdhL771xkVdr1155+tiQeKI4kHr/3iUueV5edMqwKsfxovWdLSyftxV3D4yNjkv4G4qaEUk/VrQ47IisnXoV93gR91mHJfg/2ZTG5C/t0lXsh8fveVwmIKGRrwkIrXRBBJ9wI+BwUe0CtLhmIxjy5uIFHdWwXwpkxI2PL07jvFih3MynRa6ZpRPTmJgAvnk4WIsPCifMXu4corDsl9niTjbhntYupRvICGju5uYETDYayosMZniebbvrDEfZQVf75LUXPc=");
        mobileOrEmailOtpRequestDto.setOtpSystem(OtpSystem.ABDM);
        StepVerifier.create(facilityRequestService.sendOtpForEnrollmentNumberService(mobileOrEmailOtpRequestDto))
                .expectNextCount(1L).verifyComplete();
    }

    @Test
    void getDetailsByEnrolmentNumber()
    {
        accountDto.setStatus(ACTIVE.getValue());
        accountDto.setVerificationStatus(PROVISIONAL);
        when(accountService.getAccountByHealthIdNumber(any()))
                .thenReturn(Mono.just(accountDto));
        identityDocumentsDto.setPhoto("iVBORw0KGgoAAAANSUhEUgAABLUAAAP3CAIAAABVkOuGAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAEMFSURBVHhe7d3tgds4lgXQicsBOR5H42Q6mF096T6JVEkluCy5CPGcH7viJQB+NJrAG/f0/O//AAAA4P/+T30IAABAUR8CAABQ1IcAAAAU9SEAAABFfQgAAEBRHwIAAFDUhwAAABT1IQAAAEV9CAAAQFEfAgAAUNSHAAAAFPUhAAAARX0IAABAUR8CAABQ1IcAAAAU9SEAAABFfQgAAEBRHwIAAFDUhwAAABT1IQAAAEV9CAAAQFEfAgAAUNSHAAAAFPUhAAAARX0IAABAUR8CAABQ1IcAAAAU9SEAAABFfQgAAEBRHwIAAFDUhwAAABT1IQAAAEV9CAAAQFEfAgAAUNSHAAAAFPUhAAAARX0IAABAUR8CAABQ1IcAAAAU9SEAAABFfQgAAEBRHwIAAFDUhwAAABT1IQAAAEV9CAAAQFEfAgAAUNSHAAAAFPUhAAAARX0IAABAUR8CAABQ1IcAAAAU");
        identityDocumentsDto.setPhotoBack("DsMAAA7DAcdvqGQAAEMFSURBVHhe7d3tgds4lgXQicsBOR5H42Q6mF096T6JVEkluCy5CPGcH7viJQB+NJrAG/f0/O//AAAA4P/+T30IAABAUR8CAABQ1IcAAAAU9SEAAABFfQgAAEBRHwIAAFDUhwAAABT1IQAAAEV9CAAAQFEfAgAAUNSHAAAAFPUhAAAARX0IAABAUR8CAABQ1IcAAAAU9SEAAABFfQgAAEBRHwIAAFDUhwAAABT1IQAAAEV9CAAAQFEfAgAAUNSHAAAAFPUhAAAARX0IAABAUR8CAABQ1IcAAAAU9SEAAABFfQgAAEBRHwIAAFDUhwAAABT1IQAAAEV9CAAAQFEfAgAAUNSHAAAAFPUhAAAARX0IAABAUR8CAABQ1IcAAAAU9SEAAABFfQgAAEBRHwI");
        when(documentClient.getIdentityDocuments(any()))
                .thenReturn(Mono.just(identityDocumentsDto));
        when(enrolmentCipher.decrypt(any()))
                .thenReturn("UP1120920032359");

        StepVerifier.create(facilityRequestService.fetchDetailsByEnrollmentNumber("91-7367-5177-0131"))
                .expectNextCount(1L).verifyComplete();
    }

    @Test
    void verifyOtpViaNotification()
    {
        redisOtp.setOtpValue("$argon2id$v=19$m=8192,t=100,p=8$77+9Cw$lwsiQ4xSYcfd9OMS1tNKQfXdCnt8GO0/WiSxCZVYX7TvORWALjmU5zGbCiLIlh43liIZTsDUyJIxkw6HHA5CrvIV3PRS+55ny49iubxcY4t0SH1HNBOMlzh1xPeVt6sH6qd7wnaHCTJaBfyZiPvZYht5vshGXvDom/OP5FspUos");
        when(redisService.getRedisOtp(any()))
                .thenReturn(redisOtp);
        when(redisService.isMultipleOtpVerificationAllowed(any())).thenReturn(true);
        transactionDto.setTxnId(UUID.fromString("a95fba05-c367-420b-b404-d8debd7f574a"));
        transactionDto.setHealthIdNumber("91-2740-1852-4521");
        transactionDto.setName("Aman Verma");
        transactionDto.setOtp("$argon2id$v=19$m=8192,t=100,p=8$TB8$eQipZfvNNblsk3+subNK5vVqR5muO6vrc5G5L4RlkHU8c1oncmZWACWgjhaOu89GQ4TGkcbLA3Jzll54iHNY2aM7042nGKdIjFVF2OzK89/JFdIKrBK6eE2m006K4SIQWXO9pNzHv7fgmJcndr/YlTU7o4OKx3Qz8EBxZ0cUUGk");
        when(transactionService.findTransactionDetailsFromDB(any()))
                .thenReturn(Mono.just(transactionDto));
        when(transactionService.updateTransactionEntity(any(),any()))
                .thenReturn(Mono.just(transactionDto));

        ArrayList<Scopes> scopes = new ArrayList<>();
        scopes.add(Scopes.ABHA_ENROL);
        scopes.add(Scopes.VERIFY_ENROLLMENT);
        authRequestDto.setScope(scopes);
        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.OTP);
        otp.setTxnId("558737e6-677c-4fde-b945-c8e7ce1b3519");
        otp.setOtpValue("959740");
        otp.setTimeStamp("2023-03-29 14:20:13");
        authData.setAuthMethods(authMethods);
        authData.setOtp(otp);
        authRequestDto.setAuthData(authData);
        StepVerifier.create(facilityRequestService.verifyOtpViaNotificationFlow(authRequestDto))
                .expectNextCount(1L).verifyComplete();
    }

    @Test
    void verifyFacilityByEnroll()
    {
        transactionDto.setMobileVerified(true);
        transactionDto.setTxnId(UUID.fromString("a95fba05-c367-420b-b404-d8debd7f574a"));
        when(transactionService.findTransactionDetailsFromDB(any()))
                .thenReturn(Mono.just(transactionDto));
        accountDto.setVerificationStatus(PROVISIONAL);
        when(accountService.getAccountByHealthIdNumber(any()))
                .thenReturn(Mono.just(accountDto));
        accountDto.setStatus(ACTIVE.getValue());
        accountDto.setUpdateDate(now());
        accountDto.setKycVerified(true);
        when(accountService.updateAccountByHealthIdNumber(any(),any()))
                .thenReturn(Mono.just(accountDto));
        when(accountActionService.createAccountActionEntity(any()))
                .thenReturn(Mono.just(accountActionDto));
        notificationResponseDto.setStatus("sent");
        when(notificationService.sendABHACreationSMS(any(),any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));
        when(jwtUtil.generateToken(any(),any()))
                .thenReturn("eyJhbGciOiJSUzUxMiJ9.eyJpc0t5Y1ZlcmlmaWVkIjp0cnVlLCJzdWIiOiI5MS0yNzQwLTE4NTItNDUyMSIsImNsaWVudElkIjoiYWJoYS1wcm9maWxlLWFwcC1hcGkiLCJzeXN0ZW0iOiJBQkhBLU4iLCJtb2JpbGUiOiI5ODk3NTcwNjAzIiwiYWJoYU51bWJlciI6IjkxLTI3NDAtMTg1Mi00NTIxIiwicHJlZmVycmVkQWJoYUFkZHJlc3MiOiI5MTI3NDAxODUyNDUyMUBzYngiLCJ0eXAiOiJUcmFuc2FjdGlvbiIsImV4cCI6MTY4NTQ2NDcxOCwiaWF0IjoxNjg1NDYyOTE4LCJ0eG5JZCI6ImE5NWZiYTA1LWMzNjctNDIwYi1iNDA0LWQ4ZGViZDdmNTc0YSJ9.MRkDhH6G9htrfyF813IpSyK3_wlWq7oX4avAWhWiV58mFZsuCochLHiI7Fg74eKeOKKkkF-j-amRGW56_V1TD1fwtyuUNYG1Qgc3PCxZEbGVVBkd5488Kb3KW1TJRMWcK3NY5sr1Vi-kmQHZBavSXH1PM5AfHQkYSlLS4YB74SBUG_yyF8ku2TQloOX-NnQ3YDSMeWT4giYqTvKq8u-L7999Wp9wzbEDoS9--GMEbuaHTeYRRMs3cGOZDB-5t_-RB0bp97idGHEfDv2qrWpysYsnrrGsLvX_Uts8DNr8_g-3Df743k2JskD0cfB-kKp8RDhw6Ysv8Mg50tG6e35E-72UOBnl2njc72k1ov2Rscj_vXfJuCQb2cWnXNZttGCpiIVheg42SDqHraPQw3CUu_2BAAsuG9IciPLy-kz2uOo5eeK-BVTpzUe4tsbiuKZiSeaSu09hQOC8SuiLPznS29rp3VFXohmL4YB6mrbeiQT8Yt7Rdz-9VEavA6SYty2WBlMxsVEIqMi_8aPLtgamtAcDkdfwG4BaGnIqJEGEUoRRdELHMOBraaGDMnHl9_Jma8Xjhex1YxMMjFsTAzVJtLjro3JNjXAVR0THL55yyjoFNyf3agw7SItWowe5c57TqnAp4XIiJztofmYblvXdBf2qtVmSCwF5xslwUqkHGe0");
        enrollmentStatusUpdate.setTxnId("a95fba05-c367-420b-b404-d8debd7f574a");
        enrollmentStatusUpdate.setVerificationStatus("ACCEPT");
        enrollmentStatusUpdate.setMessage("valid");
        StepVerifier.create(facilityRequestService.verifyFacilityByEnroll(enrollmentStatusUpdate))
                .expectNextCount(1L).verifyComplete();
    }
}
