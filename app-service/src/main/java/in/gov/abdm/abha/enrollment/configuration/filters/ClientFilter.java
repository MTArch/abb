package in.gov.abdm.abha.enrollment.configuration.filters;

import in.gov.abdm.abha.enrollment.configuration.ContextHolder;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.exception.application.AbhaUnAuthorizedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class ClientFilter implements WebFilter {

    public static final String REQUEST_ID = "REQUEST_ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String clientId = exchange.getRequest().getHeaders().get(REQUEST_ID) != null ? exchange.getRequest().getHeaders().get(REQUEST_ID).get(0) : StringUtils.EMPTY;
        if(StringUtils.isEmpty(clientId)){
            //todo throw exception in case we didnt find clientId headers, once UI implemented in all Apis
            //throw new AbhaUnAuthorizedException(AbhaConstants.INVALID_CLIENT_ID_IN_HEADERS);
        }

        ContextHolder.setClientId(clientId);
        ContextHolder.setClientIp((exchange.getRequest().getRemoteAddress() != null && exchange.getRequest().getRemoteAddress().getAddress() != null) ?
                exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : null);

        return chain.filter(exchange);
    }
}
