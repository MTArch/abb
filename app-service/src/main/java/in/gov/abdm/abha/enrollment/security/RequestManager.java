package in.gov.abdm.abha.enrollment.security;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RequestManager implements ReactiveAuthenticationManager {
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        Request request = (Request) authentication;
        if(request.getRequestId() !=null && request.getTimestamp()!=null) {
            authentication.setAuthenticated(true);
        }
        return Mono.just(authentication);
    }
}
