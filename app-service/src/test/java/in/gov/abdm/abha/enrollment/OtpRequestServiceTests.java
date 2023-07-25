package in.gov.abdm.abha.enrollment;
import in.gov.abdm.abha.enrollment.enums.LoginHint;
import in.gov.abdm.abha.enrollment.enums.request.OtpSystem;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarAuthOtpDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.notification.NotificationResponseDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpResponseDto;
import in.gov.abdm.abha.enrollment.services.aadhaar.impl.AadhaarAppServiceImpl;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.notification.NotificationService;
import in.gov.abdm.abha.enrollment.services.otp_request.OtpRequestService;
import in.gov.abdm.abha.enrollment.services.redis.RedisService;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class OtpRequestServiceTests {

    public static final String TEST_AADHAAR_NUMBER = "853889831963";
    public static final String aadhaarNumber = "QYhr7tdzsYyYVfFjnv/fRApJBixLC2xt1Xv1Sk/AU1SQfmagNEyamwg6o3/kiD9cKuyG3D1M1aNvVMgBCLYnuKhglQgnivzcIVVR3icwxjlJp/7gOEEB76OJzl7EG8AyqB1omC4KpgcAPjq/436t1150Mn4sDrbomJVgYZbDECD2808cxJ8ygs3iv0n/FQldvaU3bdDkKnHAE/XMnxVm7KGzn7/XI7ylxvGNmnVfwzfdnFlHnaL0oFbs0X9G9hDhI370BclD22qo8yh5y4jhbYYShtBKdB9E93CWt94Cx4y43fNAjAJJq2caMvR3M1vxFumTkc/Gn2IAtPxWMHi/e8qloF0oGO3I+j2ktWTRR6qbh7JyigcQLdOg8QPa95cpn4AyiRgXh0KyACtYFiakI3T1PsVx+GO/RrzuWIX5eYBe4TRbvD7DfuXJFrrxOhn8NOmTVOTf6OgYhAtL9OiLNLeplL1MFhZmnyqNJIAfyKZvKlQfFFMtugRZBJVmzTzI1qit1hAODI/X+0o4NMllfEMeFW6GWLnBCjl3ZM37+IS8YXLwsTYHvVd0s8zvPI6MohEdM6GyqpGnXyBYSTjblE3RG+pFvdovhRbH74yem1IdQKmFHWAoJXgWeCy9BQfBxv1SCI13IrpuvZhJ9I+/9SUX/Sl1gpg8iFHMXTqFEns=";
    public static final String SUCCESS = "success";
    public static final String MOBILE_NUMBER = "******8510";
    public static final String HID_NDHM_H_1_2023_02_17_T_17_02_27_013 = "HID-NDHM-H1-2023-02-17T17:02:27.013";
    public static final String phoneNumber = "A1gfy5yroEf258qHwwLPBUCeGzZC/1GAk/uUtJbkZoaQjHzQ9tmz5+hy2yWRNpDM3lI/x8iSbh5e3ON0jTrCK8NcbeqtGWysRep6Ym6tjmsm4yE8WRjDvgg4DPJpiDDS5eXoolFg2JnLuNjgP9md6mm/2caKkcdDk/WRzuiTK+S1hF/kMmCTNDS2X+sLAalM8WNFXUTna7G2o3CnRO4dYALAdWXO8TytyTmCm1S3apouKYPMkH/dbKOrc8Q8mzdQiLvOakxRIGEPQ7TISZvMZmJ51rWJGZEuq8yVbTXrjGKTTJBZbUtB+YTHNwfMN3g0us8ivIIJhjmfIToLzeq603ZlOv4JsWIGVIK0s8QB5t2WJUoCoAtc+6VTGGL0+nILc2PIuFg9lom/1wqCvstgHAQkaxa3U8uuzvjVlDwTa851WrDQtCrqlLRuztBLGL1140gQYrEzsbMzEgmoJ0TEphrSt0ftrZkPozrfmEAb3bqZyuIIGJzikiGBv54AqiRdvuy2bs/u9MUmitgV58J6VOC1OZdrhlo5JkUup499o8XtDkHTQyqHN6/TccY6W3Pp7AKse0p9CvuDPY1GMruyfU24zUedPLGmB2vzSedBPr3oiVMgYp5paVDXciHOO3FxVXU9I6eoPFlWcfYgKyUckJEiNPlMI3CEcJXu8H2I2FQ=";
    public static final String TEST_MOBILE_NUMBER = "7037248510";

    @InjectMocks
    OtpRequestService otpRequestService;
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
    NotificationService notificationService;
    private MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto;
    private AadhaarResponseDto aadhaarResponseDto;
    private  AadhaarAuthOtpDto aadhaarAuthOtpDto;
    private TransactionDto transactionDto;
    private NotificationResponseDto notificationResponseDto;

    @BeforeEach
    void setup()
    {
        MockitoAnnotations.openMocks(this);
        aadhaarResponseDto = new AadhaarResponseDto();
        aadhaarAuthOtpDto = new AadhaarAuthOtpDto();
        transactionDto = new TransactionDto();
        mobileOrEmailOtpRequestDto = new MobileOrEmailOtpRequestDto();
        notificationResponseDto = new NotificationResponseDto();
       }

    @AfterEach
    void tearDown()
    {
        mobileOrEmailOtpRequestDto = null;
        aadhaarResponseDto=null;
        aadhaarAuthOtpDto = null;
        notificationResponseDto = null;
    }

    @Test
    void sendAadhaarOtpSuccess()
    {
        when(redisService.isResendOtpAllowed(any())).thenReturn(true);
        when(rsaUtil.decrypt(aadhaarNumber)).thenReturn(TEST_AADHAAR_NUMBER);

        aadhaarResponseDto.setStatus(SUCCESS);
        aadhaarAuthOtpDto.setUidtkn(HID_NDHM_H_1_2023_02_17_T_17_02_27_013);
        aadhaarAuthOtpDto.setStatus(SUCCESS);
        aadhaarAuthOtpDto.setMobileNumber(MOBILE_NUMBER);
        aadhaarResponseDto.setAadhaarAuthOtpDto(aadhaarAuthOtpDto);
        when(aadhaarAppService.sendOtp(any()))
                .thenReturn(Mono.just(aadhaarResponseDto));

        transactionDto.setMobile("7037248510");
        transactionDto.setAadharTxn("HID-NDHM-H1-2023-04-07T10:04:46.042");
        transactionDto.setCreatedDate(LocalDateTime.now());
        transactionDto.setTxnId(UUID.fromString("cda04910-37ca-4f2e-84d9-4e5a970d3dc7"));
        when(transactionService.createTransactionEntity(any()))
                .thenReturn(Mono.just(transactionDto));

        mobileOrEmailOtpRequestDto.setScope(Arrays.asList(Scopes.ABHA_ENROL));
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.AADHAAR);
        mobileOrEmailOtpRequestDto.setLoginId(aadhaarNumber);
        mobileOrEmailOtpRequestDto.setOtpSystem(OtpSystem.AADHAAR);
        StepVerifier.create(otpRequestService.sendAadhaarOtp(mobileOrEmailOtpRequestDto))
                .expectNextCount(0L).verifyComplete();
    }

    @Test
    void sendOtpForDLSuccess()
    {
        when(rsaUtil.decrypt(phoneNumber)).thenReturn(TEST_MOBILE_NUMBER);
        when(accountService.getMobileLinkedAccountCount(TEST_MOBILE_NUMBER))
                .thenReturn(Mono.just(-1));
        when(redisService.isResendOtpAllowed(TEST_MOBILE_NUMBER)).thenReturn(true);

        aadhaarResponseDto.setStatus(SUCCESS);
        aadhaarAuthOtpDto.setUidtkn(HID_NDHM_H_1_2023_02_17_T_17_02_27_013);
        aadhaarAuthOtpDto.setStatus(SUCCESS);
        aadhaarAuthOtpDto.setMobileNumber(MOBILE_NUMBER);
        aadhaarResponseDto.setAadhaarAuthOtpDto(aadhaarAuthOtpDto);

        notificationResponseDto.setStatus("sent");
        when(notificationService.sendRegistrationOtp(any(),any()))
                .thenReturn(Mono.just(notificationResponseDto));
        when(transactionService.createTransactionEntity(any()))
                .thenReturn(Mono.just(transactionDto));

        mobileOrEmailOtpRequestDto.setScope(Arrays.asList(Scopes.ABHA_ENROL,Scopes.MOBILE_VERIFY,Scopes.DL_FLOW));
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.MOBILE);
        mobileOrEmailOtpRequestDto.setLoginId(phoneNumber);
        mobileOrEmailOtpRequestDto.setOtpSystem(OtpSystem.ABDM);
        Mono<MobileOrEmailOtpResponseDto> responseDtoMono
                = otpRequestService.sendOtpViaNotificationServiceDLFlow(mobileOrEmailOtpRequestDto);
        StepVerifier.create(responseDtoMono)
                .assertNext(response->{
                    assert response!=null;
                    assert !response.getTxnId().isEmpty();
                    assert !response.getMessage().isEmpty();
                }).verifyComplete();
    }
}
