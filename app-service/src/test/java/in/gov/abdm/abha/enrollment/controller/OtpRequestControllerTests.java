package in.gov.abdm.abha.enrollment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.enums.LoginHint;
import in.gov.abdm.abha.enrollment.enums.request.OtpSystem;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpResponseDto;
import in.gov.abdm.abha.enrollment.services.otp_request.OtpRequestService;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
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
import reactor.core.publisher.Mono;

import java.util.List;

import static in.gov.abdm.abha.enrollment.commontestdata.CommonTestData.*;
import static in.gov.abdm.constant.ABDMConstant.REQUEST_ID;
import static in.gov.abdm.constant.ABDMConstant.TIMESTAMP;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@WebFluxTest(OtpRequestController.class)
@ActiveProfiles(profiles = "test")
public class OtpRequestControllerTests {
    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    RSAUtil rsaUtil;
    @MockBean
    OtpRequestService otpRequestService;
    public static final String REQUEST_ID_VALUE  = "9aca0531-826e-40d3-8d74-a21a149c71d7";
    public static final String TIMESTAMP_HEADER_VALUE  = "2023-04-23T16:54:32.000Z";
    private MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto;
    private MobileOrEmailOtpResponseDto mobileOrEmailOtpResponseDto;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mobileOrEmailOtpRequestDto=new MobileOrEmailOtpRequestDto();
        mobileOrEmailOtpRequestDto.setTxnId(TRANSACTION_ID_VALID);
        mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.AADHAAR);
        mobileOrEmailOtpRequestDto.setOtpSystem(OtpSystem.AADHAAR);
        mobileOrEmailOtpRequestDto.setScope(List.of(Scopes.ABHA_ENROL,Scopes.MOBILE_VERIFY,Scopes.EMAIL_VERIFY));
        mobileOrEmailOtpRequestDto.setLoginId("ab7654321011");

        mobileOrEmailOtpResponseDto=new MobileOrEmailOtpResponseDto();
        mobileOrEmailOtpResponseDto.setTxnId("12345678-2234-100-8ab-777728282228");
        mobileOrEmailOtpResponseDto.setMessage("success");
    }
    @AfterEach
    public void tearDown() {
        mobileOrEmailOtpRequestDto=null;
        mobileOrEmailOtpResponseDto=null;

    }
    /*@Test
    @WithMockUser
    public void mobileOrEmailOtpTest() throws JsonProcessingException {
        ObjectMapper objMapper = new ObjectMapper();
        String jsonString = objMapper.writeValueAsString(mobileOrEmailOtpRequestDto);
        String request = "{\"txnId\":\"09afedef-34fe-51fc-89ab-0123456789ab\",\"scope\":[\"abha-enrol\",\"mobile-verify\",\"email-verify\"],\"loginHint\":\"aadhaar\",\"loginId\":\"ab7654321011\",\"otpSystem\":\"aadhaar\"}";
        Mockito.when(rsaUtil.decrypt(any())).thenReturn("421618181");
        //Mockito.when(linkParentService.linkDependentAccount(any())).thenReturn(Mono.just(linkParentResponseDto));
        webTestClient.mutateWith(csrf())
                .post()
                .uri(URIConstant.OTP_REQUEST_ENDPOINT + URIConstant.MOBILE_OR_EMAIL_OTP_ENDPOINT)
                .header(TIMESTAMP, TIMESTAMP_HEADER_VALUE )
                .header(REQUEST_ID, REQUEST_ID_VALUE)
                // .header(AUTHORIZATION,"Bearer AUTHORIZATION")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.txnId").isEqualTo(TRANSACTION_ID_VALID);
        //.jsonPath("$.message").isEqualTo(CommonTestData.SUCCESS_MESSAGE);

    }*/
}
