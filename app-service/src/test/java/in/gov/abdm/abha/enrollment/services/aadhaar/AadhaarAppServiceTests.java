package in.gov.abdm.abha.enrollment.services.aadhaar;

import in.gov.abdm.abha.enrollment.client.AadhaarFClient;
import in.gov.abdm.abha.enrollment.exception.aadhaar.AadhaarGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarAuthOtpDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarUserKycDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicRequest;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicResponse;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AadhaarVerifyBioRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AadhaarVerifyFaceAuthRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AadhaarVerifyOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.services.aadhaar.AadhaarAppService;
import in.gov.abdm.abha.enrollment.services.aadhaar.AadhaarService;
import in.gov.abdm.abha.enrollment.services.aadhaar.impl.AadhaarAppServiceImpl;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.naming.OperationNotSupportedException;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class AadhaarAppServiceTests {
    @InjectMocks
    AadhaarAppServiceImpl aadhaarAppServiceImpl;
    @Mock
    AadhaarAppService aadhaarAppService;

    @Mock
    AadhaarFClient aadhaarFClient;

    private AadhaarResponseDto aadhaarResponseDto;
    private AadhaarOtpRequestDto aadhaarOtpRequestDto;
    private AadhaarVerifyOtpRequestDto aadhaarVerifyOtpRequestDto;
    private VerifyDemographicRequest verifyDemographicRequest;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        AadhaarVerifyBioRequestDto a = new AadhaarVerifyBioRequestDto();
        a.setAadhaarNumber("");
        a.setPid("");
        a.setAadhaarLogType("");
        AadhaarVerifyBioRequestDto a2 = new AadhaarVerifyBioRequestDto(a.getAadhaarNumber(),a.getPid(),a.getAadhaarLogType());
        AadhaarVerifyFaceAuthRequestDto faceAuthRequestDto=new AadhaarVerifyFaceAuthRequestDto();
        faceAuthRequestDto.setAadhaarNumber("");
        faceAuthRequestDto.setFaceAuthPid("");
        faceAuthRequestDto.setAadhaarLogType("");
        AadhaarVerifyFaceAuthRequestDto faceAuthRequestDto1=new AadhaarVerifyFaceAuthRequestDto(faceAuthRequestDto.getAadhaarNumber(),faceAuthRequestDto.getFaceAuthPid(),faceAuthRequestDto.getAadhaarLogType());
        aadhaarResponseDto= new AadhaarResponseDto("","","","","","","","",new AadhaarAuthOtpDto(),new AadhaarUserKycDto());
        String res =aadhaarResponseDto.getResponseCode();
        res =aadhaarResponseDto.getDeviceType();
        res =aadhaarResponseDto.getResponse();
        res =aadhaarResponseDto.getRequestXml();
        res =aadhaarResponseDto.getCode();
        res=aadhaarResponseDto.getErrorCodeInternal();
        aadhaarResponseDto=new AadhaarResponseDto();
        aadhaarOtpRequestDto=new AadhaarOtpRequestDto();
        aadhaarVerifyOtpRequestDto=new AadhaarVerifyOtpRequestDto();
        aadhaarVerifyOtpRequestDto.setOtp("");
        aadhaarVerifyOtpRequestDto.setAadhaarLogType("");
        aadhaarVerifyOtpRequestDto.setAadhaarNumber("");
        aadhaarVerifyOtpRequestDto.setAadhaarTransactionId("");
        aadhaarVerifyOtpRequestDto.setFaceAuthPid("");
        AadhaarVerifyOtpRequestDto verifyOtpRequestDto=new AadhaarVerifyOtpRequestDto(aadhaarVerifyOtpRequestDto.getAadhaarNumber(),aadhaarVerifyOtpRequestDto.getAadhaarTransactionId(),aadhaarVerifyOtpRequestDto.getOtp(),aadhaarVerifyOtpRequestDto.getFaceAuthPid(),aadhaarVerifyOtpRequestDto.getAadhaarLogType());


        VerifyDemographicRequest verifyDemographicRequest=new VerifyDemographicRequest();
        verifyDemographicRequest.setName("name");
        verifyDemographicRequest.setEmail("email");
        verifyDemographicRequest.setGender("M");
        verifyDemographicRequest.setAadhaarNumber("asd");
        verifyDemographicRequest.setDob("12-12-2000");
        verifyDemographicRequest.setPhone("9872632882");
        verifyDemographicRequest.setAadhaarLogType("log");
        verifyDemographicRequest.getEmail();
        verifyDemographicRequest.getAadhaarNumber();
        verifyDemographicRequest.getName();
        verifyDemographicRequest.getDob();
        verifyDemographicRequest.getGender();
        verifyDemographicRequest.getPhone();
        verifyDemographicRequest.getAadhaarLogType();
        aadhaarOtpRequestDto.setAadhaarLogType("");
        aadhaarOtpRequestDto.setAadhaarNumber("");
        String logNum = aadhaarOtpRequestDto.getAadhaarLogType();
        String aadharNum = aadhaarOtpRequestDto.getAadhaarNumber();

    }

    @AfterEach
    void tearDown(){
        aadhaarResponseDto=null;
        aadhaarOtpRequestDto=null;
        aadhaarVerifyOtpRequestDto=null;



    }
    @Test
    void sendOtpSuccess(){
        Mockito.when(aadhaarFClient.sendOtp(any()))
                .thenReturn(Mono.just(aadhaarResponseDto));
        StepVerifier.create(aadhaarAppServiceImpl.sendOtp(aadhaarOtpRequestDto)).expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyOtpSuccess(){
        Mockito.when(aadhaarFClient.verifyOtp(any()))
                .thenReturn(Mono.just(aadhaarResponseDto));
        StepVerifier.create(aadhaarAppServiceImpl.verifyOtp(aadhaarVerifyOtpRequestDto)).expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyDemographicDetailsSuccess(){
        Mockito.when(aadhaarFClient.verifyDemographicDetails(any()))
                .thenReturn(Mono.just(new VerifyDemographicResponse()));
        StepVerifier.create(aadhaarAppServiceImpl.verifyDemographicDetails(verifyDemographicRequest)).expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void faceAuthSuccess(){
        Mockito.when(aadhaarFClient.faceAuth(any()))
                .thenReturn(Mono.just(aadhaarResponseDto));
        StepVerifier.create(aadhaarAppServiceImpl.faceAuth(new AadhaarVerifyFaceAuthRequestDto())).expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyBioSuccess(){
        Mockito.when(aadhaarFClient.verifyBio(any(), any(), any()))
                .thenReturn(Mono.just(aadhaarResponseDto));
        StepVerifier.create(aadhaarAppServiceImpl.verifyBio(new RequestHeaders(),new AadhaarVerifyBioRequestDto())).expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void verifyIrisSuccess(){
        Mockito.when(aadhaarFClient.verifyIris(any()))
                .thenReturn(Mono.just(aadhaarResponseDto));
        StepVerifier.create(aadhaarAppServiceImpl.verifyIris(new AadhaarVerifyBioRequestDto())).expectNextCount(1L)
                .verifyComplete();
    }
    @Test
    void sendOtpFail(){
        Mockito.when(aadhaarFClient.sendOtp(any())).thenReturn(Mono.error(Exception::new));
        StepVerifier.create(aadhaarAppServiceImpl.sendOtp(new AadhaarOtpRequestDto())).expectError().verify();
    }
    @Test
    void verifyOtpFail(){
        Mockito.when(aadhaarFClient.verifyOtp(any())).thenReturn(Mono.error(Exception::new));
        StepVerifier.create(aadhaarAppServiceImpl.verifyOtp(new AadhaarVerifyOtpRequestDto())).expectError().verify();
    }
    @Test
    void verifyDemographicDetailsFail(){
        Mockito.when(aadhaarFClient.verifyDemographicDetails(any())).thenReturn(Mono.error(Exception::new));
        StepVerifier.create(aadhaarAppServiceImpl.verifyDemographicDetails(new VerifyDemographicRequest())).expectError().verify();
    }
    @Test
    void faceAuthFail(){
        Mockito.when(aadhaarFClient.faceAuth(any())).thenReturn(Mono.error(Exception::new));
        StepVerifier.create(aadhaarAppServiceImpl.faceAuth(new AadhaarVerifyFaceAuthRequestDto())).expectError().verify();
    }
    @Test
    void verifyBioFail(){
        Mockito.when(aadhaarFClient.verifyBio(any(),any(),any())).thenReturn(Mono.error(Exception::new));
        StepVerifier.create(aadhaarAppServiceImpl.verifyBio(new RequestHeaders(), new AadhaarVerifyBioRequestDto())).expectError().verify();
    }
    @Test
    void verifyIrisFail(){
        Mockito.when(aadhaarFClient.verifyIris(any())).thenReturn(Mono.error(Exception::new));
        StepVerifier.create(aadhaarAppServiceImpl.verifyIris(new AadhaarVerifyBioRequestDto())).expectError().verify();
    }
}
