package in.gov.abdm.abha.enrollment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.abdm.abha.constant.ABHAConstants;
import in.gov.abdm.abha.enrollment.commontestdata.CommonTestData.*;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthData;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.OtpDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AccountResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AuthResponseDto;
import in.gov.abdm.abha.enrollment.services.auth.aadhaar.AuthByAadhaarService;
import in.gov.abdm.abha.enrollment.services.auth.abdm.AuthByAbdmService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import in.gov.abdm.abha.enrollment.validators.annotations.AuthMethod;
import in.gov.abdm.abha.profile.utilities.GetKeys;
import in.gov.abdm.jwt.util.JWTToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJwtParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.security.PrivateKey;
import java.util.*;

import static in.gov.abdm.abha.enrollment.commontestdata.CommonTestData.*;
import static in.gov.abdm.abha.enrollment.constants.URIConstant.*;
import static in.gov.abdm.constant.ABDMConstant.REQUEST_ID;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@WebFluxTest(AuthController.class)
@ActiveProfiles(profiles = "test")
public class AuthControllerTests {
    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    AuthByAbdmService authByAbdmService;
    @MockBean
    AuthByAadhaarService authByAadhaarService;
    @MockBean
    RSAUtil rsaUtil;
    private AuthData authData;
    private AuthRequestDto authRequestDto;
    private AccountResponseDto accountResponseDto;
    private AuthResponseDto authResponseDto;
    private OtpDto otp;
    public static final String REQUEST_ID_VALUE  = "9aca0531-826e-40d3-8d74-a21a149c71d7";
    public static final String TIMESTAMP_HEADER_VALUE  = "2023-04-23T16:54:32.000Z";
    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        authData = new AuthData();
        otp= new OtpDto();
        otp.setOtpValue("12ab12");
        otp.setTxnId(UUID.randomUUID().toString());
        accountResponseDto=AccountResponseDto.builder().ABHANumber(ABHA_NUMBER_VALID).email(EMAIL_VALID).build();

        authRequestDto=new AuthRequestDto();
        authResponseDto= new AuthResponseDto();

        ArrayList<AuthMethods> listAuthMethods=new ArrayList<>();
        listAuthMethods.add(AuthMethods.BIO);
        listAuthMethods.add(AuthMethods.DEMO_AUTH);
        listAuthMethods.add(AuthMethods.DEMO);
        listAuthMethods.add(AuthMethods.FACE);
        listAuthMethods.add(AuthMethods.OTP);
        authData.setAuthMethods(listAuthMethods);
        authData.setOtp(otp);
        ArrayList<Scopes> scopes=new ArrayList<>();
        scopes.add(Scopes.ABHA_ENROL);
        scopes.add(Scopes.DL_FLOW);
        scopes.add(Scopes.MOBILE_VERIFY);
        authRequestDto.setAuthData(authData);
        authRequestDto.setScope(scopes);
        authResponseDto.setTxnId(TRANSACTION_ID_VALID);
        authResponseDto.setMessage(SUCCESS_MESSAGE);
        authResponseDto.setAuthResult("SUCCESS");
        authResponseDto.setAccounts(List.of(accountResponseDto));
    }
    @AfterEach
    public void tearDown(){
        authData=null;
        otp=null;
        authResponseDto=null;
        authRequestDto=null;
    }
    @Test
    @WithMockUser
    public void authByABDMTests() throws JsonProcessingException {
        String key = "{\"scope\":[\"abha-enrol\",\"dl-flow\",\"mobile-verify\"],\"authData\":{\"authMethods\":[\"otp\"],\"otp\":{\"txnId\":\"2d1df739-2877-464b-be64-80ac3b5de320\",\"otpValue\":\"12ab12\"}}}"; //defaultJwtParser = new DefaultJwtParser();
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("121212");
        Mockito.when(authByAbdmService.verifyOtpViaNotificationDLFlow(any())).thenReturn(Mono.just(authResponseDto));
        webTestClient.mutateWith(csrf())
                .post()
                .uri(AUTH_ENDPOINT+AUTH_BY_ABDM_ENDPOINT)
                .header(TIMESTAMP,TIMESTAMP_HEADER_VALUE)
                .header("REQUEST-ID",REQUEST_ID_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(key)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.txnId").isEqualTo(authResponseDto.getTxnId())
                .jsonPath("$.message").isEqualTo(authResponseDto.getMessage());

    }
    @Test
    @WithMockUser
    public void authByABDMTests2() throws JsonProcessingException {
        String key = "{\"scope\":[\"abha-enrol\",\"mobile-verify\"],\"authData\":{\"authMethods\":[\"otp\"],\"otp\":{\"txnId\":\"2d1df739-2877-464b-be64-80ac3b5de320\",\"otpValue\":\"12ab12\"}}}"; //defaultJwtParser = new DefaultJwtParser();
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("121212");
        Mockito.when(authByAbdmService.verifyOtpViaNotification(any(),anyBoolean())).thenReturn(Mono.just(authResponseDto));
        webTestClient.mutateWith(csrf())
                .post()
                .uri(AUTH_ENDPOINT+AUTH_BY_ABDM_ENDPOINT)
                .header(TIMESTAMP,TIMESTAMP_HEADER_VALUE)
                .header("REQUEST-ID",REQUEST_ID_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(key)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.txnId").isEqualTo(authResponseDto.getTxnId())
                .jsonPath("$.message").isEqualTo(authResponseDto.getMessage());

    }
    @Test
    @WithMockUser
    public void authByABDMTests3() throws JsonProcessingException {
        String key = "{\"scope\":[\"child-abha-enrol\"],\"authData\":{\"authMethods\":[\"otp\"],\"otp\":{\"txnId\":\"2d1df739-2877-464b-be64-80ac3b5de320\",\"otpValue\":\"12ab12\"}}}"; //defaultJwtParser = new DefaultJwtParser();
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("121212");
        Mockito.when(authByAbdmService.verifyOtp(any())).thenReturn(Mono.just(authResponseDto));
        webTestClient.mutateWith(csrf())
                .post()
                .uri(AUTH_ENDPOINT+AUTH_BY_ABDM_ENDPOINT)
                .header(TIMESTAMP,TIMESTAMP_HEADER_VALUE)
                .header("REQUEST-ID",REQUEST_ID_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(key)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.txnId").isEqualTo(authResponseDto.getTxnId())
                .jsonPath("$.message").isEqualTo(authResponseDto.getMessage());

    }
    @Test
    @WithMockUser
    public void authByABDMTests4() throws JsonProcessingException {
        String key = "{\"scope\":[\"abha-enrol\",\"email-verify\"],\"authData\":{\"authMethods\":[\"otp\"],\"otp\":{\"txnId\":\"2d1df739-2877-464b-be64-80ac3b5de320\",\"otpValue\":\"12ab12\"}}}"; //defaultJwtParser = new DefaultJwtParser();
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("121212");
        Mockito.when(authByAbdmService.verifyOtpViaNotification(any(),anyBoolean())).thenReturn(Mono.just(authResponseDto));
        webTestClient.mutateWith(csrf())
                .post()
                .uri(AUTH_ENDPOINT+AUTH_BY_ABDM_ENDPOINT)
                .header(TIMESTAMP,TIMESTAMP_HEADER_VALUE)
                .header("REQUEST-ID",REQUEST_ID_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(key)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.txnId").isEqualTo(authResponseDto.getTxnId())
                .jsonPath("$.message").isEqualTo(authResponseDto.getMessage());

    }
    @Test
    @WithMockUser
    public void authByABDMTests5() throws JsonProcessingException {
        String key = "{\"scope\":[\"abha-enrol\"],\"authData\":{\"authMethods\":[\"otp\"],\"otp\":{\"txnId\":\"2d1df739-2877-464b-be64-80ac3b5de320\",\"otpValue\":\"12ab12\"}}}"; //defaultJwtParser = new DefaultJwtParser();
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("121212");
        Mockito.when(authByAbdmService.verifyOtpViaNotification(any(),anyBoolean())).thenReturn(Mono.just(authResponseDto));
        webTestClient.mutateWith(csrf())
                .post()
                .uri(AUTH_ENDPOINT+AUTH_BY_ABDM_ENDPOINT)
                .header(TIMESTAMP,TIMESTAMP_HEADER_VALUE)
                .header("REQUEST-ID",REQUEST_ID_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(key)
                .exchange()
                .expectStatus().isBadRequest();


    }
    @Test
    @WithMockUser
    public void authByAadhaarTests() throws JsonProcessingException {
        AuthData authData1= new AuthData(authData.getAuthMethods(),authData.getOtp());
        String key = "{\"scope\":[\"abha-enrol\",\"dl-flow\",\"mobile-verify\"],\"authData\":{\"authMethods\":[\"otp\"],\"otp\":{\"txnId\":\"2d1df739-2877-464b-be64-80ac3b5de320\",\"otpValue\":\"12ab12\"}}}"; //defaultJwtParser = new DefaultJwtParser();
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("121212");
        Mockito.when(authByAadhaarService.verifyOtpChildAbha(any())).thenReturn(Mono.just(authResponseDto));
        webTestClient.mutateWith(csrf())
                .post()
                .uri(AUTH_ENDPOINT+AUTH_BY_AADHAAR_ENDPOINT)
                .header(TIMESTAMP,TIMESTAMP_HEADER_VALUE)
                .header("REQUEST-ID",REQUEST_ID_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(key)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.txnId").isEqualTo(authResponseDto.getTxnId())
                .jsonPath("$.message").isEqualTo(authResponseDto.getMessage());

    }





}
