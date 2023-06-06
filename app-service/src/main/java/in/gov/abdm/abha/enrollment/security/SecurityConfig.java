package in.gov.abdm.abha.enrollment.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

import in.gov.abdm.abha.enrollment.configuration.filters.ClientFilter;

import java.time.Duration;

@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, RequestConverter converter, RequestManager manager) {
        AuthenticationWebFilter webFilter = new AuthenticationWebFilter(manager);
        webFilter.setServerAuthenticationConverter(converter);
        http.csrf().disable().cors();
        http.headers().contentSecurityPolicy("form-action 'self'").and()
                .permissionsPolicy().policy("geolocation=(self)");
		
        return http.authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().authenticated()
                        .and()
                        .addFilterAfter(webFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                        .addFilterAfter(new ClientFilter(), SecurityWebFiltersOrder.HTTP_HEADERS_WRITER)
                        .httpBasic().disable()
                        .formLogin().disable())
                .build();
    }
}
