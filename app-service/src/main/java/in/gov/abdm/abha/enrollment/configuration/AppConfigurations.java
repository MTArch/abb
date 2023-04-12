package in.gov.abdm.abha.enrollment.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import reactivefeign.ReactiveOptions;
import reactivefeign.webclient.WebReactiveOptions;

@Configuration
public class AppConfigurations implements WebFluxConfigurer {

    @Bean
    public ReactiveOptions reactiveOptions() {
        return new WebReactiveOptions.Builder()
                .setReadTimeoutMillis(10000)
                .setWriteTimeoutMillis(10000)
                .setResponseTimeoutMillis(10000)
                .build();
    }

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024);
    }
    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedOrigins("http://wso2gw.abdm.gov.in")
                .allowedMethods("*")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
