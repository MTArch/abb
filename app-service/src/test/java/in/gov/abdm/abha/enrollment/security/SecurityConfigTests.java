package in.gov.abdm.abha.enrollment.security;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
public class SecurityConfigTests {
    @InjectMocks
    SecurityConfig securityConfig;
    @Mock
    ServerHttpSecurity serverHttpSecurity;
    @Mock
    RequestManager requestManager;
    @Mock
    RequestConverter requestConverter;

    //ServerHttpSecurity.CsrfSpec c = ServerHttpSecurity.CsrfSpec.;
    /*@Test
    public void springSecurityFilterChainTest(){
        Mockito.when(serverHttpSecurity.csrf()).thenReturn(c);
        securityConfig.springSecurityFilterChain(serverHttpSecurity,requestConverter,requestManager);
        Assert.assertEquals("","");
    }*/
}
