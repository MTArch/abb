package in.gov.abdm.abha.enrollment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
@EnableWebFlux
@EnableReactiveFeignClients
public class ABHAEnrollmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ABHAEnrollmentApplication.class, args);
    }

}
