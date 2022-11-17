package in.gov.abdm.abha.enrollment.aadhaar.verify_otp;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Map;

@AutoConfigureWebTestClient
@SpringBootTest
public class EnrollmentVerifyOtpTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void verifyEmptyConsent() {
        try {
            String jsonStringResponse = new String(webTestClient.post()
                    .uri("/api/v3/enrollment/enrol/byAadhaar")
                    .body(BodyInserters.fromValue(new EnrolByAadhaarRequestDto()))
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody().returnResult().getResponseBody());

            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> response = mapper.readValue(jsonStringResponse, Map.class);

            Assert.isTrue(response.get("consent").equals("Consent cannot be null or empty"), "test");
        }catch (Exception e){

        }
    }
}
