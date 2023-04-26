package in.gov.abdm.abha.enrollment.configuration.filters;

import in.gov.abdm.abha.enrollment.configuration.ContextHolder;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.jwt.JWTUtil;
import in.gov.abdm.error.ABDMError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import static in.gov.abdm.constant.ABDMConstant.*;

@Slf4j
@Component
public class ClientFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authorization = StringUtils.EMPTY;
        if (!HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
            ContextHolder.removeAll();
            HttpHeaders requestHeaders = exchange.getRequest().getHeaders();

            List<String> authorizationHeaders = requestHeaders.get(AbhaConstants.AUTHORIZATION);
            if (authorizationHeaders != null && !authorizationHeaders.isEmpty() && authorizationHeaders.get(0) != null) {
                authorization = authorizationHeaders.get(0);
            }

            String requestId = requestHeaders.getFirst(REQUEST_ID);
            String timestamp = requestHeaders.getFirst(TIMESTAMP);

            if (!Common.isValidateISOTimeStamp(timestamp)) {
                return Common.throwFilterBadRequestException(exchange, ABDMError.INVALID_TIMESTAMP);
            }
            if (!Common.isValidRequestId(requestId)) {
                return Common.throwFilterBadRequestException(exchange, ABDMError.INVALID_REQUEST_ID);
            }

            if (!StringUtils.isEmpty(authorization)) {
                Map<String, Object> claims = JWTUtil.readJWTToken(authorization);
                ContextHolder.setClientId(claims.get(CLIENT_ID) == null ? StringConstants.EMPTY : claims.get(CLIENT_ID).toString());
            }
            ContextHolder.setRequestId(requestId);
            ContextHolder.setTimestamp(timestamp);

            InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
            String clientIp = null;
            if (remoteAddress != null && remoteAddress.getAddress() != null) {
                clientIp = remoteAddress.getAddress().getHostAddress();
            }
            ContextHolder.setClientIp(clientIp);
        }
        return chain.filter(exchange);
    }
}
