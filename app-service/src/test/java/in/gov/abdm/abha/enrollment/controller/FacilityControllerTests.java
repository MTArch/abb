package in.gov.abdm.abha.enrollment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.enums.LoginHint;
import in.gov.abdm.abha.enrollment.enums.request.OtpSystem;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.facility.document.EnrolProfileDetailsDto;
import in.gov.abdm.abha.enrollment.model.facility.document.GetByDocumentResponseDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpResponseDto;
import in.gov.abdm.abha.enrollment.services.auth.abdm.AuthByAbdmService;
import in.gov.abdm.abha.enrollment.services.facility.FacilityEnrolByEnrollmentNumberService;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import in.gov.abdm.jwt.util.JWTToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.security.PrivateKey;
import java.util.Arrays;
import java.util.List;

import static in.gov.abdm.abha.enrollment.commontestdata.CommonTestData.*;
import static in.gov.abdm.abha.enrollment.constants.URIConstant.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@WebFluxTest(FacilityController.class)
@ActiveProfiles(profiles = "test")
public class FacilityControllerTests {
    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    FacilityEnrolByEnrollmentNumberService facilityRequestService;

    @MockBean
    RSAUtil rsaUtil;
    @MockBean
    JWTToken jwtToken;

    @MockBean
    AuthByAbdmService authByAbdmService;
    public static final String REQUEST_ID_VALUE  = "9aca0531-826e-40d3-8d74-a21a149c71d7";
    public static final String TIMESTAMP_HEADER_VALUE  = "2023-04-23T16:54:32.000Z";
    private MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto;
    private MobileOrEmailOtpResponseDto mobileOrEmailOtpResponseDto;
    private GetByDocumentResponseDto getByDocumentResponseDto;
    private EnrolProfileDetailsDto enrolProfileDetailsDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mobileOrEmailOtpRequestDto =new MobileOrEmailOtpRequestDto(TRANSACTION_ID_VALID,List.of(Scopes.ABHA_ENROL, Scopes.VERIFY_ENROLLMENT), LoginHint.AADHAAR,"ab1619680693", OtpSystem.AADHAAR);
        mobileOrEmailOtpResponseDto=new MobileOrEmailOtpResponseDto();
        getByDocumentResponseDto=new GetByDocumentResponseDto();
        enrolProfileDetailsDto=new EnrolProfileDetailsDto();
        enrolProfileDetailsDto.setEnrolmentNumber("1");
        enrolProfileDetailsDto.setDocumentNumber("");
        enrolProfileDetailsDto.setEnrolmentState("");
        enrolProfileDetailsDto.setFirstName("");
        enrolProfileDetailsDto.setMiddleName("");
        enrolProfileDetailsDto.setLastName("");
        enrolProfileDetailsDto.setDob("");
        enrolProfileDetailsDto.setGender("");
        enrolProfileDetailsDto.setMobile("");
        enrolProfileDetailsDto.setEmail("");
        enrolProfileDetailsDto.setAddress("");
        enrolProfileDetailsDto.setDistrictCode("");
        enrolProfileDetailsDto.setDistrict("");
        enrolProfileDetailsDto.setStateCode("");
        enrolProfileDetailsDto.setState("");
        enrolProfileDetailsDto.setAbhaType("");
        enrolProfileDetailsDto.setPinCode("");
        enrolProfileDetailsDto.setAbhaStatus("");
        enrolProfileDetailsDto.setPhoto("");
        enrolProfileDetailsDto.setPhotoFront("");
        enrolProfileDetailsDto.setPhotoBack("");
        enrolProfileDetailsDto.setPhrAddress(Arrays.asList(""));

        getByDocumentResponseDto.setEnrolProfileDto(new EnrolProfileDetailsDto(enrolProfileDetailsDto.getEnrolmentNumber(),enrolProfileDetailsDto.getDocumentNumber(),enrolProfileDetailsDto.getEnrolmentState(),enrolProfileDetailsDto.getFirstName(),enrolProfileDetailsDto.getMiddleName(),enrolProfileDetailsDto.getLastName(),enrolProfileDetailsDto.getDob(),enrolProfileDetailsDto.getGender(),enrolProfileDetailsDto.getMobile(),enrolProfileDetailsDto.getEmail(),enrolProfileDetailsDto.getAddress(),enrolProfileDetailsDto.getDistrictCode(),enrolProfileDetailsDto.getDistrict(),enrolProfileDetailsDto.getStateCode(),enrolProfileDetailsDto.getState(),enrolProfileDetailsDto.getAbhaType(),enrolProfileDetailsDto.getPinCode(),enrolProfileDetailsDto.getAbhaStatus(),enrolProfileDetailsDto.getPhoto(),enrolProfileDetailsDto.getPhotoFront(),enrolProfileDetailsDto.getPhotoBack(),enrolProfileDetailsDto.getPhrAddress()));

        mobileOrEmailOtpResponseDto.setTxnId(TRANSACTION_ID_VALID);
        mobileOrEmailOtpResponseDto.setMessage("success");
    }
    @AfterEach
    public void tearDown() {
        mobileOrEmailOtpRequestDto=null;
        mobileOrEmailOtpResponseDto=null;

    }
    @Test
    @WithMockUser
    public void mobileOrEmailOtpTests() throws JsonProcessingException {
        ObjectMapper obj = new ObjectMapper();
        String s = obj.writeValueAsString(mobileOrEmailOtpRequestDto);
        String request = "{\"txnId\":\"\",\"scope\":[\"abha-enrol\"],\"loginHint\":\"aadhaar\",\"loginId\":\"ab7654321011\",\"otpSystem\":\"aadhaar\"}"; //defaultJwtParser = new DefaultJwtParser();
       // Mockito.when(JWTToken.validateToken(anyString(),any(PrivateKey.class))).thenReturn(true);
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("421619680693");
        Mockito.when(facilityRequestService.sendOtpForEnrollmentNumberService(any())).thenReturn(Mono.just(mobileOrEmailOtpResponseDto));
        /*webTestClient.mutateWith(csrf())
                .post()
                .uri(FACILITY_ENDPOINT+FACILITY_OTP_ENDPOINT)
                .header(TIMESTAMP,TIMESTAMP_HEADER_VALUE)
                .header("REQUEST-ID",REQUEST_ID_VALUE)
                .header(AbhaConstants.F_TOKEN,"Bearer "+JWT_TOKEN_VALID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.txnId").isEqualTo(mobileOrEmailOtpResponseDto.getTxnId())
                .jsonPath("$.message").isEqualTo(mobileOrEmailOtpResponseDto.getMessage());
*/
    }
    @Test
    @WithMockUser
    public void getDetailsByEnrolmentNumberTests() throws JsonProcessingException {
        ObjectMapper obj = new ObjectMapper();
        String s = obj.writeValueAsString(mobileOrEmailOtpRequestDto);
        String request = "{\"txnId\":\"\",\"scope\":[\"abha-enrol\"],\"loginHint\":\"aadhaar\",\"loginId\":\"ab7654321011\",\"otpSystem\":\"aadhaar\"}"; //defaultJwtParser = new DefaultJwtParser();
        // Mockito.when(JWTToken.validateToken(anyString(),any(PrivateKey.class))).thenReturn(true);
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("421619680693");
        Mockito.when(facilityRequestService.fetchDetailsByEnrollmentNumber(any())).thenReturn(Mono.just(getByDocumentResponseDto));
        /*webTestClient.mutateWith(csrf())
                .get()
                .uri(FACILITY_ENDPOINT+FACILITY_PROFILE_DETAILS_BY_ENROLLMENT_NUMBER_ENDPOINT,"1")
                .header(TIMESTAMP,TIMESTAMP_HEADER_VALUE)
                .header("REQUEST-ID",REQUEST_ID_VALUE)
                .header(AbhaConstants.F_TOKEN,"Bearer "+"eyJhbGciOiJSUzUxMiA9.eyJwaW5jb2RlIjoiNjM4MDAzIiwic3ViIjoiZ2fuYXBhdGh5X3AxMjVAc2J4IiwiY2xpZW50SWQiOiJURVNUX1BIUiIsInJlcXVlc3RlcklkIjoiUEhSLVdFQiIsImdlbmRlciI6Ik0iLCJkaXN0cmljdE5hbWUiOiJFUk9ERSIsIm1vYmlsZSI6IjcwMTkzNDUxNzIiLCJmdWxsTmFtZSI6IkdhbmFwYXRoeSBQIiwiYWRkcmVzc0xpbmUiOiJDL08gUGFyYXN1cmFtYW4gNjggY2hpbm5hIG1hcml5YW1tYW4ga292aWwgc3RyZWV0IGthcnVuZ2FsIHBhbGF5YW0gRXJvZGUiLCJoZWFsdGhJZE51bWJlciI6IjkxLTE3NDMtNTU0My02NzAzIiwibW9udGhPZkJpcnRoIjoiNSIsInN5c3RlbSI6IkFCSEEtQSIsInN0YXRlTmFtZSI6IlRBTUlMIE5BRFUiLCJkYXlPZkJpcnRoIjoiMTIiLCJwaHJNb2JpbGUiOiI3MDE5MzQ1MTcyIiwiZXhwIjoxNzA5ODAxNTk1LCJpYXQiOjE3MDkxOTY3OTUsInBockFkZHJlc3MiOiJnYW5hcGF0aHlfcDEyNUBzYngiLCJlbWFpbCI6bnVsbCwieWVhck9mQmlydGgiOiIxOTg1In0.SPsYMLa30KGzhLklFBobpk6f8C4X1U07EsboC7v6IzqMxzuZdZfdQAbukSai1dllTYsrOHabDKPmsLmFsIKQ0taf7CxEqj6BhIMvW8dd5ngp9Av1ymHeGIRFbznwb7ov5yiAhdQI8nTbG5GsV8unICs_23L_VhUuobyFEEhpF4sMdi1qHY4LgKsQKqWXdruK9F6U092-0GLC1hLWJKyiK6oXltumyfmPCymIIm9whnOF6HKUf8W12BBUq90d4oU8PrQmSb3KP9Fps21QJjkSdADHKYeBiGXuYOrSJ__sx0HwCj-lvgHAv81y_ljWJ9_f4yOtNKB0Ly6e4dqcpBnS4A")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody();*/
    }


}
