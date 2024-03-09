package in.gov.abdm.abha.enrollment.security;

import in.gov.abdm.abha.enrollment.utilities.Common;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ServerWebExchange;
import reactor.test.StepVerifier;

import java.util.UUID;

@ExtendWith(SpringExtension.class)
public class RequestConverterTests {
    @InjectMocks
    RequestConverter requestConverter;
    @Mock
    ServerWebExchange serverWebExchange;
    @Mock
    ServerHttpRequest serverHttpRequest;
    @Mock
    RequestPath requestPath;

    @Test
    public void convertTest() throws AssertionError{

        Mockito.when(serverHttpRequest.getPath()).thenReturn(requestPath);
        Mockito.when(serverWebExchange.getRequest()).thenReturn(serverHttpRequest);
        StepVerifier.create(requestConverter.convert(serverWebExchange)).expectNextCount(0L).verifyComplete();
        Mockito.when(requestPath.toString()).thenReturn("/actuator/health");
      //  StepVerifier.create(requestConverter.convert(serverWebExchange)).expectNext().verifyComplete();

    }
}
