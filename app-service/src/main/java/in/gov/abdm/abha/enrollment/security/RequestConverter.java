package in.gov.abdm.abha.enrollment.security;

import in.gov.abdm.abha.profile.constants.StringConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static in.gov.abdm.abha.enrollment.constants.URIConstant.excludedList;
import static in.gov.abdm.constant.ABDMConstant.REQUEST_ID;
import static in.gov.abdm.constant.ABDMConstant.TIMESTAMP;

@Slf4j
@Component
public class RequestConverter implements ServerAuthenticationConverter {
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        if (excludedList.contains(exchange.getRequest().getPath().toString())) {
            return Mono.justOrEmpty(exchange.getRequest())
                    .map(httpRequest -> new Request(StringConstants.UNKNOWN, StringConstants.UNKNOWN));
        } else {
            return Mono.justOrEmpty(exchange
                    .getRequest()
                    .getHeaders()
            ).map(httpHeaders -> new Request(httpHeaders.getFirst(REQUEST_ID), httpHeaders.getFirst(TIMESTAMP)));
        }
    }
}
