package in.gov.abdm.abha.enrollment.security;

import lombok.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

@Value
public class Request extends AbstractAuthenticationToken {
    String requestId;
    String timestamp;

    Request(String requestId, String timestamp){
        super(AuthorityUtils.NO_AUTHORITIES);
        this.requestId=requestId;
        this.timestamp=timestamp;
    }

    @Override
    public Request getCredentials() {
        return this;
    }

    @Override
    public Request getPrincipal() {
        return this;
    }
}
