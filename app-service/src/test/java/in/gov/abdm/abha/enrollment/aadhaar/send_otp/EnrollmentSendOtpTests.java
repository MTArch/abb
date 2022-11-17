package in.gov.abdm.abha.enrollment.aadhaar.send_otp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
public class EnrollmentSendOtpTests {

    @Autowired
    WebTestClient webTestClient;

}
