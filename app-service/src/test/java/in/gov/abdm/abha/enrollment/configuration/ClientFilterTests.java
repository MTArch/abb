package in.gov.abdm.abha.enrollment.configuration;

import feign.Request;
import in.gov.abdm.abha.enrollment.configuration.filters.ClientFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class ClientFilterTests {
    @InjectMocks
    ClientFilter clientFilter;
    @Mock
    ServerHttpRequest serverHttpRequest;
    @Mock
    WebFilterChain webFilterChain;
    @Mock
    ServerWebExchange serverWebExchange;
    //private ServerWebExchange

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

    }
    @AfterEach
    void teardown() {

    }
    @Test
    public void filterTest(){
       /* Mockito.when(Mockito.mock(HttpMethod.class).equals(any())).thenReturn(true);
        Mockito.when(Mockito.mock(ServerWebExchange.class).getRequest()).thenReturn(Mockito.mock(ServerHttpRequest.class));
        Mockito.when(Mockito.mock(ServerHttpRequest.class).getMethod()).thenReturn(HttpMethod.GET);
        Mockito.when(Mockito.mock(ServerHttpRequest.class).getPath()).thenReturn(Mockito.mock(RequestPath.class));
        Mockito.when(Mockito.mock(RequestPath.class).toString()).thenReturn("test");
        StepVerifier.create(clientFilter.filter(serverWebExchange,webFilterChain)).expectNextCount(1L).verifyComplete();
*/
    }
}
