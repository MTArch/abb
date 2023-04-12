package in.gov.abdm.abha.enrollment.aadhaar.send_otp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.abdm.abha.enrollment.aadhaar.send_otp.dto.TestMobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.enums.LoginHint;
import in.gov.abdm.abha.enrollment.enums.request.OtpSystem;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarAuthOtpDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarResponseDto;
import in.gov.abdm.abha.enrollment.services.aadhaar.impl.AadhaarAppServiceImpl;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Map;

import static org.mockito.Mockito.when;

@AutoConfigureWebTestClient
@SpringBootTest
@Slf4j
public class EnrolByAadhaarTests {

    public static final String TEST_AADHAAR_NUMBER = "812790762182";
    public static final String SUCCESS = "success";
    public static final String MOBILE_NUMBER = "******7890";
    public static final String API_V_3_ENROLLMENT_REQUEST_OTP = "/api/v3/enrollment/request/otp";
    public static final String TXN_ID = "txnId";
    public static final String TXN_ID_IS_EMPTY = "txnId is empty";
    public static final String MESSAGE = "message";
    public static final String OTP_IS_SENT_TO_AADHAAR_REGISTERED_MOBILE_ENDING = "OTP is sent to Aadhaar registered mobile ending *******";
    public static final String MESSAGE_IS_EMPTY = "message is empty";
    public static final String HID_NDHM_H_1_2023_02_17_T_17_02_27_013 = "HID-NDHM-H1-2023-02-17T17:02:27.013";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    AadhaarAppServiceImpl aadhaarAppService;

    @Autowired
    RSAUtil rsaUtil;

    @Test
    void sendAadhaarOTP() {

        AadhaarResponseDto aadhaarResponseDto = new AadhaarResponseDto();
        aadhaarResponseDto.setStatus(SUCCESS);
        AadhaarAuthOtpDto aadhaarAuthOtpDto = new AadhaarAuthOtpDto();
        aadhaarAuthOtpDto.setUidtkn(HID_NDHM_H_1_2023_02_17_T_17_02_27_013);
        aadhaarAuthOtpDto.setStatus(SUCCESS);
        aadhaarAuthOtpDto.setMobileNumber(MOBILE_NUMBER);
        aadhaarResponseDto.setAadhaarAuthOtpDto(aadhaarAuthOtpDto);

        String aadhaarNumber = rsaUtil.encrypt(TEST_AADHAAR_NUMBER);

        when(aadhaarAppService.sendOtp(new AadhaarOtpRequestDto(aadhaarNumber)))
                .thenReturn(Mono.just(aadhaarResponseDto));

        try {
            TestMobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto = new TestMobileOrEmailOtpRequestDto();
            mobileOrEmailOtpRequestDto.setScope(Arrays.asList(Scopes.ABHA_ENROL.getValue()));
            mobileOrEmailOtpRequestDto.setLoginHint(LoginHint.AADHAAR.getValue());
            mobileOrEmailOtpRequestDto.setLoginId(aadhaarNumber);
            mobileOrEmailOtpRequestDto.setOtpSystem(OtpSystem.AADHAAR.getValue());

            String jsonStringResponse = new String(webTestClient.post().uri(API_V_3_ENROLLMENT_REQUEST_OTP)
                    .body(BodyInserters.fromValue(mobileOrEmailOtpRequestDto))
                    .exchange()
                    .expectStatus().is2xxSuccessful()
                    .expectBody().returnResult().getResponseBody());

            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> response = mapper.readValue(jsonStringResponse, Map.class);

            Assert.isTrue(!response.get(TXN_ID).isEmpty(), TXN_ID_IS_EMPTY);
            Assert.isTrue(!response.get(MESSAGE).contains(OTP_IS_SENT_TO_AADHAAR_REGISTERED_MOBILE_ENDING), MESSAGE_IS_EMPTY);

        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

}
