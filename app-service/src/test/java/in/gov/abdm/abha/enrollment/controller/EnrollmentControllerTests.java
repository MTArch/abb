package in.gov.abdm.abha.enrollment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.abdm.abha.enrollment.commontestdata.CommonTestData;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.enums.childabha.AbhaType;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.Gender;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.MobileType;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.demographic.Demographic;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.demographic.DemographicAuth;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.*;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.HidBenefitRequestPayload;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ResponseTokensDto;
import in.gov.abdm.abha.enrollment.model.enrol.abha_address.request.AbhaAddressRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.abha_address.response.AbhaAddressResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.abha_address.response.SuggestAbhaResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolProfileDto;
import in.gov.abdm.abha.enrollment.model.enrol.facility.EnrollmentResponse;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.model.notification.NotificationType;
import in.gov.abdm.abha.enrollment.model.notification.SendNotificationRequestDto;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.EnrolUsingAadhaarService;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.bio.EnrolByBioService;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.demographic.EnrolByDemographicService;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.iris.EnrolByIrisService;
import in.gov.abdm.abha.enrollment.services.enrol.abha_address.AbhaAddressService;
import in.gov.abdm.abha.enrollment.services.enrol.document.EnrolByDocumentValidatorService;
import in.gov.abdm.abha.enrollment.services.enrol.document.EnrolUsingDrivingLicence;
import in.gov.abdm.abha.enrollment.utilities.BenefitMapper;
import in.gov.abdm.abha.enrollment.utilities.RequestMapper;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static in.gov.abdm.abha.enrollment.commontestdata.CommonTestData.*;
import static in.gov.abdm.abha.enrollment.constants.URIConstant.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@WebFluxTest(EnrollmentController.class)
@ActiveProfiles(profiles = "test")
public class EnrollmentControllerTests {
    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    EnrolUsingAadhaarService enrolUsingAadhaarService;

    @MockBean
    EnrolUsingDrivingLicence enrolUsingDrivingLicence;

    @MockBean
    EnrolByDocumentValidatorService enrolByDocumentValidatorService;

    @MockBean
    AbhaAddressService abhaAddressService;

    @MockBean
    EnrolByDemographicService enrolByDemographicService;
    @MockBean
    EnrolByBioService enrolByBioService;
    @MockBean
    EnrolByIrisService enrolByIrisService;
    @MockBean
    RSAUtil rsaUtil;
    private EnrolByAadhaarRequestDto enrolByAadhaarRequestDto;
    private EnrolByAadhaarResponseDto enrolByAadhaarResponseDto;
    private AuthData authData;
    private OtpDto otp;
    private TokenDto tokenDto;
    private Demographic demographic;
    private DemographicAuth demographicAuth;
    private FaceDto face;
    private BioDto bio;
    private IrisDto iris;
    private ConsentDto consentDto;
    private EnrolProfileDto enrolProfileDto;
    private EnrollmentResponse enrollmentResponse;
    private ResponseTokensDto responseTokensDto;
    private ABHAProfileDto abhaProfileDto;
    private SuggestAbhaResponseDto suggestAbhaResponseDto;
    private EnrolByDocumentRequestDto enrolByDocumentRequestDto;
    private EnrolByDocumentResponseDto enrolByDocumentResponseDto;
    private AbhaAddressRequestDto abhaAddressRequestDto;
    private AbhaAddressResponseDto abhaAddressResponseDto;
    private SendNotificationRequestDto sendNotificationRequestDto;
    public static final String REQUEST_ID_VALUE  = "9aca0531-826e-40d3-8d74-a21a149c71d7";
    public static final String TIMESTAMP_HEADER_VALUE  = "2023-04-23T16:54:32.000Z";
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        consentDto=new ConsentDto();
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        enrolProfileDto=new EnrolProfileDto();
        enrollmentResponse=new EnrollmentResponse();
        enrolProfileDto.setEnrolmentNumber("1");
        enrolProfileDto.setEnrolmentState("state");
        enrollmentResponse.setToken("1");
        enrollmentResponse.setStatus("status");
        enrollmentResponse.setMessage("msg");
        enrollmentResponse.setExpiresIn(1L);
        enrollmentResponse.setRefreshToken("refTkn");
        enrollmentResponse.setRefreshExpiresIn(1L);
        responseTokensDto = new ResponseTokensDto();
        responseTokensDto.setRefreshToken("1");
        responseTokensDto.setToken("1");
        responseTokensDto.setExpiresIn(1L);
        responseTokensDto.setRefreshExpiresIn(1L);
        enrolByAadhaarRequestDto=new EnrolByAadhaarRequestDto();
        enrolByAadhaarResponseDto=new EnrolByAadhaarResponseDto();
        enrolByDocumentRequestDto=new EnrolByDocumentRequestDto("1","docType","ab123 ab123","name","midName","lastName","10-10-2000","M","img.png","img.png","add","state","dist","123122",consentDto);
        enrolByDocumentResponseDto=new EnrolByDocumentResponseDto();
        enrolByDocumentResponseDto.setNew(true);
        enrolByDocumentResponseDto.setMessage("success");
        enrolByDocumentResponseDto.setEnrolProfileDto(enrolProfileDto);
        enrolByDocumentResponseDto.setEnrolmentResponse(new EnrollmentResponse(enrollmentResponse.getStatus(),enrollmentResponse.getMessage(),enrollmentResponse.getToken(),enrollmentResponse.getExpiresIn(),enrollmentResponse.getRefreshExpiresIn(),enrollmentResponse.getRefreshToken()));
        enrolByDocumentResponseDto.setResponseTokensDto(responseTokensDto);
        tokenDto=new TokenDto();
        abhaProfileDto=ABHAProfileDto.builder().build();
        tokenDto=new TokenDto("1");
        demographic =new Demographic("421619680693","NAme","MidName","LastName","","","","","9878667865", MobileType.FAMILY,"","","","","","","","");
        demographicAuth=new DemographicAuth("421619680693","Name","12-12-2000", Gender.FEMALE.getCode(), "","photo","9878667865","");

        authData=new AuthData();
        otp= new OtpDto();
        otp.setOtpValue("12ab12");
        otp.setTxnId(UUID.randomUUID().toString());
        otp.setMobile("9876899005");
        tokenDto.setTokenId(tokenDto.getTokenId());
        face=new FaceDto();
        face.setRdPidData("rdPidData");
        face.setAadhaar("182822828882");
        face.setMobile("9872822929");
        bio=BioDto.builder().build();
        bio = new BioDto("182822828882","11","9872822929");
        iris=IrisDto.builder().build();
        iris=new IrisDto("182822828882","11","9872822929");
        suggestAbhaResponseDto=new SuggestAbhaResponseDto();
        suggestAbhaResponseDto.setAbhaAddressList(Arrays.asList("add"));
        suggestAbhaResponseDto.setTxnId(TRANSACTION_ID_VALID);
        abhaAddressRequestDto=AbhaAddressRequestDto.builder().build();
        abhaAddressRequestDto=new AbhaAddressRequestDto();
        abhaAddressRequestDto.setTxnId(UUID.randomUUID().toString());
        abhaAddressRequestDto.setPreferred("1");
        abhaAddressRequestDto.setPreferredAbhaAddress("abc_abc.abc");
        abhaAddressResponseDto = new AbhaAddressResponseDto();
        abhaAddressResponseDto.setPreferredAbhaAddress(abhaAddressRequestDto.getPreferredAbhaAddress());
        abhaAddressResponseDto.setHealthIdNumber(HEALTH_ID_NUMBER);
        abhaAddressResponseDto.setTxnId(abhaAddressRequestDto.getTxnId());
        sendNotificationRequestDto=new SendNotificationRequestDto(ABHA_NUMBER_VALID,Arrays.asList(NotificationType.SMS),"SMS");

    }
    @AfterEach
    void tearDown(){
        sendNotificationRequestDto=null;
        abhaAddressResponseDto=null;
        abhaAddressRequestDto=null;
        suggestAbhaResponseDto=null;
        iris=null;
        bio=null;
        otp=null;
        face=null;
        demographic=null;
        demographicAuth=null;
        consentDto=null;
        authData=null;
        enrolByAadhaarRequestDto=null;
        enrollmentResponse=null;
        enrolByAadhaarResponseDto=null;
        enrolProfileDto=null;
        enrolByDocumentResponseDto=null;
        enrolByDocumentRequestDto=null;
        tokenDto=null;
        abhaProfileDto=null;
        responseTokensDto=null;



    }
    @Test
    @WithMockUser
    public void enrolUsingAadhaarOTPTests() throws JsonProcessingException {
        ArrayList<AuthMethods> listAuthMethods=new ArrayList<>();
        listAuthMethods.add(AuthMethods.OTP);
        authData.setAuthMethods(listAuthMethods);
        authData.setOtp(otp);
        authData.setToken(tokenDto);

        ArrayList<Scopes> scopes=new ArrayList<>();
        scopes.add(Scopes.ABHA_ENROL);
        scopes.add(Scopes.DL_FLOW);
        scopes.add(Scopes.MOBILE_VERIFY);
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        enrolByAadhaarResponseDto.setMessage("success");
        ObjectMapper obj = new ObjectMapper();
        String a= obj.writeValueAsString(enrolByAadhaarRequestDto);
        String key = "{\"authData\":{\"authMethods\":[\"otp\"],\"token\":{\"id_token\":\"1\"},\"otp\":{\"txnId\":\"dba863aa-fd2f-4813-a31d-211ba0a796f4\",\"otpValue\":\"12ab12\",\"mobile\":\"9876899005\"},\"demo\":null,\"demo_auth\":null,\"face\":null,\"bio\":null,\"iris\":null},\"consent\":{\"code\":\"abha-enrollment\",\"version\":\"1.4\"}}";
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("121212");
        Mockito.when(enrolUsingAadhaarService.validateHeaders(any(),any(),any())).thenReturn(Mono.just(true));
        Mockito.when(enrolUsingAadhaarService.verifyOtp(any(),any())).thenReturn(Mono.just(enrolByAadhaarResponseDto));
        webTestClient.mutateWith(csrf())
                .post()
                .uri(URIConstant.ENROL_ENDPOINT+URIConstant.BY_ENROL_AADHAAR_ENDPOINT)
                .header(TIMESTAMP,TIMESTAMP_HEADER_VALUE)
                .header("REQUEST-ID",REQUEST_ID_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(key)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo(enrolByAadhaarResponseDto.getMessage());
    }
    @Test
    @WithMockUser
    public void enrolUsingAadhaarDEMOTests() throws JsonProcessingException {
        ArrayList<AuthMethods> listAuthMethods=new ArrayList<>();
        listAuthMethods.add(AuthMethods.DEMO);
        authData.setAuthMethods(listAuthMethods);
        authData.setToken(tokenDto);
        authData.setDemographic(demographic);
        authData.setDemographicAuth(demographicAuth);

        consentDto=new ConsentDto();
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        enrolByAadhaarResponseDto.setMessage("success");
        ObjectMapper obj = new ObjectMapper();
        String a= obj.writeValueAsString(enrolByAadhaarRequestDto);
        String key = "{\"authData\":{\"authMethods\":[\"demo\"],\"token\":{\"id_token\":\"1\"},\"otp\":null,\"demo\":{\"stateCode\":null,\"districtCode\":null,\"firstName\":\"\",\"middleName\":\"\",\"lastName\":\"\",\"dayOfBirth\":\"\",\"monthOfBirth\":\"\",\"yearOfBirth\":\"\",\"gender\":\"\",\"mobile\":\"\",\"mobileType\":\"FAMILY\",\"state\":\"\",\"district\":\"\",\"pinCode\":\"\",\"address\":\"\",\"consentFormImage\":\"\",\"healthWorkerName\":\"\",\"healthWorkerMobile\":\"\",\"validity\":\"\",\"aadhaar\":\"421619680693\"},\"demo_auth\":{\"stateCode\":null,\"districtCode\":null,\"aadhaarNumber\":\"421619680693\",\"name\":\"Name\",\"dateOfBirth\":\"\",\"gender\":\"F\",\"address\":\"\",\"profilePhoto\":\"\",\"mobile\":\"\",\"validity\":\"\"},\"face\":null,\"bio\":null,\"iris\":null},\"consent\":{\"code\":\"abha-enrollment\",\"version\":\"1.4\"}}";
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("121212");
        Mockito.when(enrolUsingAadhaarService.validateHeaders(any(),any(),any())).thenReturn(Mono.just(true));
        Mockito.when(enrolByDemographicService.validateAndEnrolByDemoAuth(any(EnrolByAadhaarRequestDto.class),any())).thenReturn(Mono.just(enrolByAadhaarResponseDto));
        webTestClient.mutateWith(csrf())
                .post()
                .uri(URIConstant.ENROL_ENDPOINT+URIConstant.BY_ENROL_AADHAAR_ENDPOINT)
                .header(TIMESTAMP,TIMESTAMP_HEADER_VALUE)
                .header("REQUEST-ID",REQUEST_ID_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(key)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo(enrolByAadhaarResponseDto.getMessage());
    }
    @Test
    @WithMockUser
    public void enrolUsingAadhaarFACETests() throws JsonProcessingException {
        ArrayList<AuthMethods> listAuthMethods=new ArrayList<>();
        listAuthMethods.add(AuthMethods.FACE);

        authData.setAuthMethods(listAuthMethods);
        authData.setToken(tokenDto);
        authData.setFace(face);
        consentDto=new ConsentDto();
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        enrolByAadhaarResponseDto.setMessage("success");
        ObjectMapper obj = new ObjectMapper();
        String a= obj.writeValueAsString(enrolByAadhaarRequestDto);
        String key = "{\"authData\":{\"authMethods\":[\"face\"],\"token\":{\"id_token\":\"1\"},\"otp\":null,\"demo\":null,\"demo_auth\":null,\"face\":{\"aadhaar\":\"ab2822828882\",\"rdPidData\":\"rdPidData\",\"mobile\":\"9872822929\"},\"bio\":null,\"iris\":null},\"consent\":{\"code\":\"abha-enrollment\",\"version\":\"1.4\"}}";
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("421619680693");
        Mockito.when(enrolUsingAadhaarService.validateHeaders(any(),any(),any())).thenReturn(Mono.just(true));
        Mockito.when(enrolUsingAadhaarService.faceAuth(any(EnrolByAadhaarRequestDto.class),any())).thenReturn(Mono.just(enrolByAadhaarResponseDto));
        webTestClient.mutateWith(csrf())
                .post()
                .uri(URIConstant.ENROL_ENDPOINT+URIConstant.BY_ENROL_AADHAAR_ENDPOINT)
                .header(TIMESTAMP,TIMESTAMP_HEADER_VALUE)
                .header("REQUEST-ID",REQUEST_ID_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(key)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo(enrolByAadhaarResponseDto.getMessage());
    }
    @Test
    @WithMockUser
    public void enrolUsingAadhaarBIOTests() throws JsonProcessingException {
        ArrayList<AuthMethods> listAuthMethods=new ArrayList<>();
        listAuthMethods.add(AuthMethods.BIO);
        authData.setAuthMethods(listAuthMethods);
        authData.setToken(tokenDto);
        authData.setBio(bio);

        consentDto=new ConsentDto();
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        enrolByAadhaarResponseDto.setMessage("success");
        ObjectMapper obj = new ObjectMapper();
        String a= obj.writeValueAsString(enrolByAadhaarRequestDto);
        String key = "{\"authData\":{\"authMethods\":[\"bio\"],\"token\":{\"id_token\":\"1\"},\"otp\":null,\"demo\":null,\"demo_auth\":null,\"face\":null,\"bio\":{\"aadhaar\":\"ab2822828882\",\"fingerPrintAuthPid\":\"11\",\"mobile\":\"9872822929\"},\"iris\":null},\"consent\":{\"code\":\"abha-enrollment\",\"version\":\"1.4\"}}";
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("421619680693");
        Mockito.when(enrolUsingAadhaarService.validateHeaders(any(),any(),any())).thenReturn(Mono.just(true));
        Mockito.when(enrolByBioService.verifyBio(any(EnrolByAadhaarRequestDto.class),any())).thenReturn(Mono.just(enrolByAadhaarResponseDto));
        webTestClient.mutateWith(csrf())
                .post()
                .uri(URIConstant.ENROL_ENDPOINT+URIConstant.BY_ENROL_AADHAAR_ENDPOINT)
                .header(TIMESTAMP,TIMESTAMP_HEADER_VALUE)
                .header("REQUEST-ID",REQUEST_ID_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(key)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo(enrolByAadhaarResponseDto.getMessage());
    }
    @Test
    @WithMockUser
    public void enrolUsingAadhaarIRISTests() throws JsonProcessingException {
        ArrayList<AuthMethods> listAuthMethods=new ArrayList<>();
        listAuthMethods.add(AuthMethods.IRIS);
        authData.setAuthMethods(listAuthMethods);
        authData.setToken(tokenDto);
        authData.setIris(iris);

        consentDto=new ConsentDto();
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        enrolByAadhaarResponseDto.setMessage("success");
        ObjectMapper obj = new ObjectMapper();
        String a= obj.writeValueAsString(enrolByAadhaarRequestDto);
        String key = "{\"authData\":{\"authMethods\":[\"iris\"],\"token\":{\"id_token\":\"1\"},\"otp\":null,\"demo\":null,\"demo_auth\":null,\"face\":null,\"bio\":null,\"iris\":{\"aadhaar\":\"ab1619680693\",\"pid\":\"11\",\"mobile\":\"9872822929\"}},\"consent\":{\"code\":\"abha-enrollment\",\"version\":\"1.4\"}}";
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("421619680693");
        Mockito.when(enrolUsingAadhaarService.validateHeaders(any(),any(),any())).thenReturn(Mono.just(true));
        Mockito.when(enrolByIrisService.verifyIris(any(EnrolByAadhaarRequestDto.class),any())).thenReturn(Mono.just(enrolByAadhaarResponseDto));
        webTestClient.mutateWith(csrf())
                .post()
                .uri(URIConstant.ENROL_ENDPOINT+URIConstant.BY_ENROL_AADHAAR_ENDPOINT)
                .header(TIMESTAMP,TIMESTAMP_HEADER_VALUE)
                .header("REQUEST-ID",REQUEST_ID_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(key)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo(enrolByAadhaarResponseDto.getMessage());
    }
    @Test
    @WithMockUser
    public void enrolUsingAadhaarDEMO_AuthTests() throws JsonProcessingException {
        ArrayList<AuthMethods> listAuthMethods=new ArrayList<>();
        listAuthMethods.add(AuthMethods.DEMO_AUTH);
        authData.setAuthMethods(listAuthMethods);
        authData.setToken(tokenDto);
        authData.setDemographic(demographic);
        authData.setDemographicAuth(demographicAuth);

        consentDto=new ConsentDto();
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        enrolByAadhaarResponseDto.setAbhaProfileDto(new ABHAProfileDto(ABHA_NUMBER_VALID, AccountStatus.ACTIVE,"name","MidName","LastName","12-12-2000","M","photo","9876543235",EMAIL_VALID, Arrays.asList("add"),"add","1","1","234322", AbhaType.STANDARD,"",""));
        enrolByAadhaarResponseDto.setResponseTokensDto(new ResponseTokensDto("1",1L,"1",1L));
        enrolByAadhaarResponseDto.setTxnId(TRANSACTION_ID_VALID);
        enrolByAadhaarResponseDto.setNew(true);
        enrolByAadhaarResponseDto.setMessage("success");
        ObjectMapper obj = new ObjectMapper();
        String a= obj.writeValueAsString(enrolByAadhaarRequestDto);
        String key = "{\"authData\":{\"authMethods\":[\"demo_auth\"],\"token\":{\"id_token\":\"1\"},\"otp\":null,\"demo\":{\"stateCode\":null,\"districtCode\":null,\"firstName\":\"NAme\",\"middleName\":\"MidName\",\"lastName\":\"LastName\",\"dayOfBirth\":\"\",\"monthOfBirth\":\"\",\"yearOfBirth\":\"\",\"gender\":\"\",\"mobile\":\"9878667865\",\"mobileType\":\"FAMILY\",\"state\":\"\",\"district\":\"\",\"pinCode\":\"\",\"address\":\"\",\"consentFormImage\":\"\",\"healthWorkerName\":\"\",\"healthWorkerMobile\":\"\",\"validity\":\"\",\"aadhaar\":\"ab1619680693\"},\"demo_auth\":{\"stateCode\":null,\"districtCode\":null,\"aadhaarNumber\":\"ab1619680693\",\"name\":\"Name\",\"dateOfBirth\":\"12-12-2000\",\"gender\":\"F\",\"address\":\"\",\"profilePhoto\":\"photo\",\"mobile\":\"9878667865\",\"validity\":\"\"},\"face\":null,\"bio\":null,\"iris\":null},\"consent\":{\"code\":\"abha-enrollment\",\"version\":\"1.4\"}}";
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("421619680693");
        Mockito.when(enrolUsingAadhaarService.validateHeaders(any(),any(),any())).thenReturn(Mono.just(true));
        Mockito.when(enrolByDemographicService.validateAndEnrolByDemoAuth(any(EnrolByAadhaarRequestDto.class),any())).thenReturn(Mono.just(enrolByAadhaarResponseDto));
       // Mockito.when(BenefitMapper.mapHidBenefitRequestPayload(any())).thenReturn(new HidBenefitRequestPayload());
        webTestClient.mutateWith(csrf())
                .post()
                .uri(URIConstant.ENROL_ENDPOINT+URIConstant.BY_ENROL_AADHAAR_ENDPOINT)
                .header(TIMESTAMP,TIMESTAMP_HEADER_VALUE)
                .header("REQUEST-ID",REQUEST_ID_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(key)
                .exchange()
                .expectStatus().isOk()
                .expectBody();
    }
    @Test
    @WithMockUser
    public void enrolUsingAadhaarDEMO_AuthTests2() throws JsonProcessingException {
        ArrayList<AuthMethods> listAuthMethods=new ArrayList<>();
        listAuthMethods.add(AuthMethods.DEMO_AUTH);
        authData.setAuthMethods(listAuthMethods);
        authData.setToken(tokenDto);
        demographicAuth.setDateOfBirth("2000");
        authData.setDemographic(demographic);
        authData.setDemographicAuth(demographicAuth);

        consentDto=new ConsentDto();
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        enrolByAadhaarResponseDto.setAbhaProfileDto(new ABHAProfileDto(ABHA_NUMBER_VALID, AccountStatus.ACTIVE,"name","MidName","LastName","2000","M","photo","9876543235",EMAIL_VALID, Arrays.asList("add"),"add","1","1","234322", AbhaType.STANDARD,"",""));
        enrolByAadhaarResponseDto.setResponseTokensDto(new ResponseTokensDto("1",1L,"1",1L));
        enrolByAadhaarResponseDto.setTxnId(TRANSACTION_ID_VALID);
        enrolByAadhaarResponseDto.setNew(true);
        enrolByAadhaarResponseDto.setMessage("success");
        ObjectMapper obj = new ObjectMapper();
        String a= obj.writeValueAsString(enrolByAadhaarRequestDto);
        String key = "{\"authData\":{\"authMethods\":[\"demo_auth\"],\"token\":{\"id_token\":\"1\"},\"otp\":null,\"demo\":{\"stateCode\":null,\"districtCode\":null,\"firstName\":\"NAme\",\"middleName\":\"MidName\",\"lastName\":\"LastName\",\"dayOfBirth\":\"\",\"monthOfBirth\":\"\",\"yearOfBirth\":\"\",\"gender\":\"\",\"mobile\":\"9878667865\",\"mobileType\":\"FAMILY\",\"state\":\"\",\"district\":\"\",\"pinCode\":\"\",\"address\":\"\",\"consentFormImage\":\"\",\"healthWorkerName\":\"\",\"healthWorkerMobile\":\"\",\"validity\":\"\",\"aadhaar\":\"ab1619680693\"},\"demo_auth\":{\"stateCode\":null,\"districtCode\":null,\"aadhaarNumber\":\"ab1619680693\",\"name\":\"Name\",\"dateOfBirth\":\"2000\",\"gender\":\"F\",\"address\":\"\",\"profilePhoto\":\"photo\",\"mobile\":\"9878667865\",\"validity\":\"\"},\"face\":null,\"bio\":null,\"iris\":null},\"consent\":{\"code\":\"abha-enrollment\",\"version\":\"1.4\"}}";
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("421619680693");
        Mockito.when(enrolUsingAadhaarService.validateHeaders(any(),any(),any())).thenReturn(Mono.just(true));
        Mockito.when(enrolByDemographicService.validateAndEnrolByDemoAuth(any(EnrolByAadhaarRequestDto.class),any())).thenReturn(Mono.just(enrolByAadhaarResponseDto));
        webTestClient.mutateWith(csrf())
                .post()
                .uri(URIConstant.ENROL_ENDPOINT+URIConstant.BY_ENROL_AADHAAR_ENDPOINT)
                .header(TIMESTAMP,TIMESTAMP_HEADER_VALUE)
                .header("REQUEST-ID",REQUEST_ID_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(key)
                .exchange()
                .expectStatus().isOk()
                .expectBody();
    }
    @Test
    @WithMockUser
    public void enrolByDocumentTestErr() throws JsonProcessingException {

        ObjectMapper obj = new ObjectMapper();
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("421619680693");
        Mockito.when(enrolUsingAadhaarService.validateHeaders(any(),any(),any())).thenReturn(Mono.just(true));
        Mockito.when(enrolUsingDrivingLicence.verifyAndCreateAccount(any(EnrolByDocumentRequestDto.class),any())).thenReturn(Mono.just(enrolByDocumentResponseDto));
        webTestClient.mutateWith(csrf())
                .post()
                .uri(URIConstant.ENROL_ENDPOINT+URIConstant.ENROL_BY_DOCUMENT_ENDPOINT)
                .header(TIMESTAMP,TIMESTAMP_HEADER_VALUE)
                .header("REQUEST-ID",REQUEST_ID_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(enrolByDocumentRequestDto))
                .exchange()
                .expectStatus().isBadRequest();
    }
    @Test
    @WithMockUser
    public void enrolByDocumentTest() throws JsonProcessingException {
        enrolByDocumentRequestDto.setDocumentType(AbhaConstants.DRIVING_LICENCE);
        ObjectMapper obj = new ObjectMapper();
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("421619680693");
        Mockito.when(enrolUsingAadhaarService.validateHeaders(any(),any(),any())).thenReturn(Mono.just(true));
        Mockito.when(enrolUsingDrivingLicence.verifyAndCreateAccount(any(EnrolByDocumentRequestDto.class),any())).thenReturn(Mono.just(enrolByDocumentResponseDto));
        webTestClient.mutateWith(csrf())
                .post()
                .uri(URIConstant.ENROL_ENDPOINT+URIConstant.ENROL_BY_DOCUMENT_ENDPOINT)
                .header(TIMESTAMP,TIMESTAMP_HEADER_VALUE)
                .header("REQUEST-ID",REQUEST_ID_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(enrolByDocumentRequestDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo(enrolByDocumentResponseDto.getMessage());
    }
    @Test
    @WithMockUser
    public void getAbhaAddressSuggestionTest() throws JsonProcessingException {

        Mockito.when(rsaUtil.decrypt(any())).thenReturn("421619680693");
        //Mockito.when(enrolUsingAadhaarService.validateHeaders(any(),any(),any())).thenReturn(Mono.just(true));
        Mockito.when(abhaAddressService.getAbhaAddress(any())).thenReturn(Mono.just(new SuggestAbhaResponseDto(suggestAbhaResponseDto.getTxnId(),suggestAbhaResponseDto.getAbhaAddressList())));
        webTestClient.mutateWith(csrf())
                .get()
                .uri(URIConstant.ENROL_ENDPOINT+URIConstant.ENROL_SUGGEST_ABHA_ENDPOINT)
                .header(TIMESTAMP,TIMESTAMP_HEADER_VALUE)
                .header("REQUEST-ID",REQUEST_ID_VALUE)
                .header("TRANSACTION_ID",TRANSACTION_ID_VALID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.txnId").isEqualTo(suggestAbhaResponseDto.getTxnId());
    }
    @Test
    @WithMockUser
    public void createAbhaAddressTest() {

        Mockito.when(rsaUtil.decrypt(any())).thenReturn("421619680693");
        Mockito.when(abhaAddressService.createAbhaAddress(any())).thenReturn(Mono.just(new AbhaAddressResponseDto(abhaAddressResponseDto.getTxnId(),abhaAddressResponseDto.getHealthIdNumber(),abhaAddressResponseDto.getPreferredAbhaAddress())));
        webTestClient.mutateWith(csrf())
                .post()
                .uri(URIConstant.ENROL_ENDPOINT+URIConstant.ENROL_ABHA_ADDRESS_ENDPOINT)
                .header(TIMESTAMP,TIMESTAMP_HEADER_VALUE)
                .header("REQUEST-ID",REQUEST_ID_VALUE)
                .header("TRANSACTION_ID",TRANSACTION_ID_VALID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(abhaAddressRequestDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.txnId").isEqualTo(abhaAddressRequestDto.getTxnId());
    }
    @Test
    @WithMockUser
    public void requestNotificationTest() {

        Mockito.when(rsaUtil.decrypt(any())).thenReturn("421619680693");
        Mockito.when(enrolUsingAadhaarService.requestNotification(any(),any())).thenReturn(Mono.just("success"));
        webTestClient.mutateWith(csrf())
                .post()
                .uri(URIConstant.ENROL_ENDPOINT+URIConstant.ENROL_REQUEST_NOTIFICATION_ENDPOINT)
                .header(TIMESTAMP,TIMESTAMP_HEADER_VALUE)
                .header("REQUEST-ID",REQUEST_ID_VALUE)
                .header("TRANSACTION_ID",TRANSACTION_ID_VALID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(sendNotificationRequestDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody();
    }


}
