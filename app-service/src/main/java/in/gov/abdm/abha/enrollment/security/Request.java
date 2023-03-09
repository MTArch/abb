package in.gov.abdm.abha.enrollment.security;

import lombok.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

import java.sql.Timestamp;
import java.util.UUID;

@Value
public class Request extends AbstractAuthenticationToken {
    UUID requestId;
    Timestamp timestamp;

    Request(UUID requestId, java.sql.Timestamp timestamp){
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
