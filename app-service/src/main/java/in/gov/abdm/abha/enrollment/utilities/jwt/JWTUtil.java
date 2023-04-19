package in.gov.abdm.abha.enrollment.utilities.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import in.gov.abdm.jwt.util.JWTToken;
import in.gov.abdm.jwt.util.JWTTokenRequest;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static in.gov.abdm.abha.enrollment.constants.PropertyConstants.JWT_TOKEN_REFRESH_VALIDITY_IN_SEC;
import static in.gov.abdm.abha.enrollment.constants.PropertyConstants.JWT_TOKEN_VALIDITY_IN_SEC;

@Slf4j
@Service
public class JWTUtil {
    private static final String TXN_ID = "txnId";
    private static final String PREFERRED_ABHA_ADDRESS = "preferredAbhaAddress";
    private static final String CLIENT_ID = "clientId";
    private static final String SYSTEM = "system";
    private static final String MOBILE = "mobile";
    private static final String KYC_VERIFIED = "isKycVerified";
    private static final String ABHA_NUMBER = "abhaNumber";
    private static final String TYPE = "typ";

    @Value(JWT_TOKEN_VALIDITY_IN_SEC) // Defaults to 30 min
    private long JWT_USER_TOKEN_VALIDITY_IN_SEC;

    @Value(JWT_TOKEN_REFRESH_VALIDITY_IN_SEC) // Defaults to 15 days
    private long JWT_USER_REFRESH_TOKEN_VALIDITY_IN_SEC;

    @Autowired
    RSAUtil rsaUtil;

    private JWTTokenRequest prepareClaimsForToken(String txnId, AccountDto account) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TXN_ID, txnId);
        claims.put(PREFERRED_ABHA_ADDRESS, account.getPreferredAbhaAddress() != null ? account.getPreferredAbhaAddress() : account.getHealthId());
        claims.put(CLIENT_ID, AbhaConstants.CLIENT_ID_VALUE);
        claims.put(SYSTEM, AbhaConstants.SYSTEM_VALUE);
        claims.put(TYPE, AbhaConstants.TOKEN_TYPE_TRANSACTION);
        claims.put(ABHA_NUMBER, account.getHealthIdNumber());
        claims.put(MOBILE, account.getMobile());
        claims.put(KYC_VERIFIED,account.isKycVerified());

        return new JWTTokenRequest(account.getHealthIdNumber(), JWT_USER_TOKEN_VALIDITY_IN_SEC / 60, claims);
    }

    private JWTTokenRequest prepareClaimsForRefreshToken(String subject) {
        Map<String, Object> refreshClaims = new HashMap<>();
        refreshClaims.put(CLIENT_ID, AbhaConstants.CLIENT_ID_VALUE);
        refreshClaims.put(SYSTEM, AbhaConstants.SYSTEM_VALUE);
        refreshClaims.put(TYPE, AbhaConstants.TOKEN_TYPE_REFRESH);

        return new JWTTokenRequest(subject, JWT_USER_REFRESH_TOKEN_VALIDITY_IN_SEC / 60, refreshClaims);
    }

    public String generateToken(String txnId, AccountDto accountDto) {
        return JWTToken.generateToken(prepareClaimsForToken(txnId, accountDto), rsaUtil.getJWTPrivateKey());
    }

    public String generateRefreshToken(String abhaNumber) {
        return JWTToken.generateToken(prepareClaimsForRefreshToken(abhaNumber), rsaUtil.getJWTPrivateKey());
    }

    public long jwtTokenExpiryTime() {
        return JWT_USER_TOKEN_VALIDITY_IN_SEC;
    }

    public long jwtRefreshTokenExpiryTime() {
        return JWT_USER_REFRESH_TOKEN_VALIDITY_IN_SEC;
    }

    public boolean isTokenExpired(String token) {
        token = token.split(" ")[1];
        try {
            JWTToken.validateToken(token, rsaUtil.getJWTPrivateKey());
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public boolean isValidToken(String token) {
        token = token.split(" ")[1];
        try {
            JWTToken.validateToken(token, rsaUtil.getJWTPrivateKey());
            return true;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return false;
        }
    }

    public Map<String, Object> getTokenClaims(String token) {
        token = token.split(" ")[1];
        return JWTToken.decodeJWTToken(token, rsaUtil.getJWTPrivateKey());
    }



    public static Map<String, Object> readJWTToken(String token) {
        try {
            if(Arrays.stream(token.split("\\.")).count() != 3){
                return Collections.emptyMap();
            }
            return new ObjectMapper().readValue(new String(Base64.getDecoder().decode(token.split("\\.")[1])), Map.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return Collections.emptyMap();
    }
}
