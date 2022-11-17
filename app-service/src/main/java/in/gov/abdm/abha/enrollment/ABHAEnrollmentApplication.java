package in.gov.abdm.abha.enrollment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableDiscoveryClient
public class ABHAEnrollmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ABHAEnrollmentApplication.class, args);
    }
}
