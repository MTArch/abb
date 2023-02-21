package in.gov.abdm.abha.enrollment.utilities.jwt;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import in.gov.abdm.jwt.util.JWTToken;
import in.gov.abdm.jwt.util.JWTTokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class JWTUtil {
    private static final String TXN_ID = "txnId";
    private static final String PREFERRED_ABHA_ADDRESS = "preferredAbhaAddress";
    private static final String CLIENT_ID = "clientId";
    private static final String SYSTEM = "system";
    private static final String MOBILE = "mobile";
    private static final String ABHA_NUMBER = "abhaNumber";
    private static final String TYPE = "typ";

    @Value("${jwt.token.validityInSec: 1800}") // Defaults to 30 min
    private long JWT_USER_TOKEN_VALIDITY_IN_SEC;

    @Value("${jwt.token.refresh.validityInSec: 1296000}") // Defaults to 15 days
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
}
