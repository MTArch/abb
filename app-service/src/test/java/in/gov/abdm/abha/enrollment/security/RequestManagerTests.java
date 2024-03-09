package in.gov.abdm.abha.enrollment.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
public class RequestManagerTests {
    @InjectMocks
    RequestManager requestManager;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    Authentication authentication;
    @Mock
    Request request;
    /*@Test
    public void authenticateTest(){
        authentication.setAuthenticated(true);
        StepVerifier.create(requestManager.authenticate(authentication)).expectNextCount(0L).verifyComplete();
    }*/
}
