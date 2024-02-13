package in.gov.abdm.abha.enrollment.utilities;

import in.gov.abdm.abha.enrollment.configuration.XTokenContextHolder;
import in.gov.abdm.abha.profile.constants.AbhaConstants;
import in.gov.abdm.abha.profile.constants.StringConstants;
import in.gov.abdm.abha.profile.exception.application.BadRequestException;
import in.gov.abdm.abha.profile.exception.application.UnAuthorizedException;
import in.gov.abdm.abha.profile.utilities.Common;
import in.gov.abdm.abha.profile.utilities.GetKeys;
import in.gov.abdm.jwt.util.JWTToken;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;

import java.util.Map;

import static in.gov.abdm.abha.profile.constants.AbhaConstants.*;
import static in.gov.abdm.abha.profile.constants.StringConstants.ABHA_NUMBER;

@UtilityClass
@Slf4j
public class XTokenHelper {

    public static final String BEARER = "Bearer ";
    public static final String KYC_VERIFIED = "isKycVerified";
    public static final String ACCOUNT_KYC_NOT_VERIFIED = "We're sorry, but you need to complete your KYC verification process to access your account.";

    public XTokenContextHolder getXToken(String xToken) {
        XTokenContextHolder xTokenContextHolder = new XTokenContextHolder();
        xToken = Common.getValidToken(xToken, BEARER);

        if (tokenValidation(xToken)) {
            if (expiryValidation(xToken)) {
                Map<String, Object> claim = JWTToken.decodeJWTToken(xToken, GetKeys.getPrivateKey());
                String abhaNumber = (String) claim.get(ABHA_NUMBER);
                if (TOKEN_TYPE_TRANSACTION.equals(claim.get(TOKEN_TYPE)) && abhaNumber != null
                        && Common.isValidAbha(abhaNumber)) {
                    xTokenContextHolder.setHealthIdNumber(abhaNumber);
                    xTokenContextHolder.setKycVerified(claim.get(KYC_VERIFIED).toString());
                } else {
                    xTokenContextHolder.setError(X_TOKEN_EXPIRED);
                    profileContextExceptionHandler(xTokenContextHolder);
                }
            } else {
                xTokenContextHolder.setError(X_TOKEN_EXPIRED);
                profileContextExceptionHandler(xTokenContextHolder);
            }
        } else {
            xTokenContextHolder.setError(INVALID_X_TOKEN);
            profileContextExceptionHandler(xTokenContextHolder);
        }
        return xTokenContextHolder;
    }

    public void profileContextExceptionHandler(XTokenContextHolder tokenContextHolder) {
        String error = tokenContextHolder.getError() != null ? tokenContextHolder.getError() : StringConstants.EMPTY;
        if (AbhaConstants.REDIS_CONNECTION_EXCEPTION.equalsIgnoreCase(error)) {
            throw new RedisConnectionFailureException(error);
        } else if (AbhaConstants.X_TOKEN_EXPIRED.equals(error) || AbhaConstants.R_TOKEN_EXPIRED.equals(error)
                || AbhaConstants.T_TOKEN_EXPIRED.equals(error) || ACCOUNT_KYC_NOT_VERIFIED.equals(error)) {
            throw new UnAuthorizedException(error);
        } else if (AbhaConstants.INVALID_X_TOKEN.equals(error) || AbhaConstants.INVALID_R_TOKEN.equals(error)
                || AbhaConstants.INVALID_T_TOKEN.equals(error)) {
            throw new BadRequestException(error);
        } else {
            throw new BadRequestException();
        }
    }

    private boolean expiryValidation(String token) {
        try {
            if (JWTToken.validateToken(token, GetKeys.getPrivateKey())) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } catch (Exception ex) {
            log.error(LOG_PREFIX + ex.getMessage(), ex);
            return false;
        }
    }

    private boolean tokenValidation(String token) {
        try {
            JWTToken.validateToken(token, GetKeys.getPrivateKey());
            return true;
        } catch (ExpiredJwtException e) {
            log.error(LOG_PREFIX + e.getMessage(), e);
            return true;
        } catch (Exception ex) {
            log.error(LOG_PREFIX + ex.getMessage(), ex);
            return false;
        }
    }
}
