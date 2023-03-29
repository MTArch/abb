package in.gov.abdm.abha.enrollment.configuration.filters;

import in.gov.abdm.abha.enrollment.configuration.ContextHolder;
import in.gov.abdm.abha.enrollment.configuration.FacilityContextHolder;
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

import java.util.Map;

import static in.gov.abdm.constant.ABDMConstant.*;

@Slf4j
@Component
public class ClientFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (!exchange.getRequest().getMethod().equals(HttpMethod.OPTIONS)) {
            ContextHolder.removeAll();
            HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
            String authorization = requestHeaders.get(AbhaConstants.AUTHORIZATION) != null ? requestHeaders.get(HttpHeaders.AUTHORIZATION).get(0) : StringUtils.EMPTY;
            String requestId = requestHeaders.get(REQUEST_ID) != null ? requestHeaders.get(REQUEST_ID).get(0) : StringUtils.EMPTY;
            String timestamp = requestHeaders.get(TIMESTAMP) != null ? requestHeaders.get(TIMESTAMP).get(0) : StringUtils.EMPTY;

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
            ContextHolder.setClientIp((exchange.getRequest().getRemoteAddress() != null && exchange.getRequest().getRemoteAddress().getAddress() != null) ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : null);
        }
        return chain.filter(exchange);
    }
}
