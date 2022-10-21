package in.gov.abdm.abha.enrollmentdb.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This is a one-stop class for having this microservice configurations.
 */
@Configuration
public class ABHAEnrollmentDBConfiguration {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
