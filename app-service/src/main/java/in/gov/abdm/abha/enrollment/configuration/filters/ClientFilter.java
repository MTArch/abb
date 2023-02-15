package in.gov.abdm.abha.enrollment.configuration.filters;

import in.gov.abdm.abha.enrollment.configuration.ContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class ClientFilter implements WebFilter {

    public static final String REQUEST_ID = "REQUEST_ID";
    public static final String TIMESTAMP = "TIMESTAMP";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if(!exchange.getRequest().getMethod().equals(HttpMethod.OPTIONS)) {
            String clientId = exchange.getRequest().getHeaders().get(REQUEST_ID) != null ? exchange.getRequest().getHeaders().get(REQUEST_ID).get(0) : StringUtils.EMPTY;
            String timestamp = exchange.getRequest().getHeaders().get(TIMESTAMP) != null ? exchange.getRequest().getHeaders().get(TIMESTAMP).get(0) : StringUtils.EMPTY;
            if (StringUtils.isEmpty(clientId)) {
                //uncomment below code in case we didnt find clientId headers, once UI implemented in all Apis
//            ServerHttpResponse response = exchange.getResponse();
//            response.setStatusCode(HttpStatus.NOT_FOUND);
//            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
//            return response.writeWith(GeneralUtils.prepareFilterExceptionResponse(exchange, ABDMError.INVALID_OR_UNAVAILABLE_HEADER_REQUEST_ID_HEADER));
            }

            ContextHolder.setClientId(clientId);
            ContextHolder.setTimestamp(timestamp);
            ContextHolder.setClientIp((exchange.getRequest().getRemoteAddress() != null && exchange.getRequest().getRemoteAddress().getAddress() != null) ?
                    exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : null);
        }
        return chain.filter(exchange);
    }
}
