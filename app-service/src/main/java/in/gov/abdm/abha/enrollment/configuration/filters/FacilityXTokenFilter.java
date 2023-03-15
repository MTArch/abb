package in.gov.abdm.abha.enrollment.configuration.filters;

import in.gov.abdm.abha.enrollment.configuration.FacilityContextHolder;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import in.gov.abdm.abha.enrollment.utilities.jwt.JWTUtil;
import in.gov.abdm.error.ABDMError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class FacilityXTokenFilter implements WebFilter {

    @Autowired
    JWTUtil jwtUtil;

    private static final String X_TOKEN = "X-Token";
    private static final String F_TOKEN = "F-Token";
    private static final String CLIENT_ID = "clientId";
    private static final String SYSTEM = "system";
    private static final String SUB = "sub";
    private static final String USER_TYPE = "USER_TYPE";
    private static final String ROLES = "roles";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (!exchange.getRequest().getMethod().equals(HttpMethod.OPTIONS)) {
            String facilityXToken = exchange.getRequest().getHeaders().get(F_TOKEN) != null ? exchange.getRequest().getHeaders().get(F_TOKEN).get(0) : StringUtils.EMPTY;
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.NOT_FOUND);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            if (facilityXToken != null && !facilityXToken.isBlank()) {
                if (jwtUtil.isTokenExpired(facilityXToken)) {
                    return response.writeWith(GeneralUtils.prepareFilterExceptionResponse(exchange, ABDMError.X_TOKEN_EXPIRED));
                }
                if (!jwtUtil.isValidToken(facilityXToken)) {
                    return response.writeWith(GeneralUtils.prepareFilterExceptionResponse(exchange, ABDMError.INVALID_X_TOKEN));
                }
                Map<String, Object> claims = jwtUtil.getTokenClaims(facilityXToken);
                FacilityContextHolder.setClientId(claims.get(CLIENT_ID).toString());
                FacilityContextHolder.setSystem(claims.get(SYSTEM).toString());
                FacilityContextHolder.setSubject(claims.get(SUB).toString());
                FacilityContextHolder.setUserType(claims.get(USER_TYPE).toString());
                FacilityContextHolder.setRole(claims.get(ROLES) != null ? claims.get(ROLES).toString() : null);
            }
        }
        return chain.filter(exchange);
    }
}
