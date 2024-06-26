package in.gov.abdm.abha.enrollment.utilities;

import in.gov.abdm.abha.enrollment.configuration.XTokenContextHolder;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.utilities.jwt.JWTUtil;
import in.gov.abdm.abha.profile.utilities.Common;
import in.gov.abdm.abha.profile.utilities.GetKeys;
import in.gov.abdm.jwt.util.JWTToken;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.DEFAULT_CLIENT_ID;
import static in.gov.abdm.constant.ABDMConstant.CLIENT_ID;

@UtilityClass
public class RequestMapper {
    @Autowired
    JWTUtil jwtUtil;
    public static final String REALM_ACCESS = "realm_access";
    public static final String ROLES = "roles";
    public static final String APPLICATION = "application";
    public static final String NAME = "name";

    public static RequestHeaders prepareRequestHeaders(String benefitName, String authorization, String fToken, String xToken) {

        Map<String, Object> claims;
        String clientId = DEFAULT_CLIENT_ID;
        List<String> benefitRoles = null;
        Map<String, Object> fTokenClaims = null;
        XTokenContextHolder xTokenContextHolder = null;

        if (!StringUtils.isEmpty(authorization)) {
            authorization = authorization.substring("Bearer ".length());
            claims = JWTUtil.readJWTToken(authorization);
            if (claims.get(CLIENT_ID) != null) {
                clientId = claims.get(CLIENT_ID).toString();
            } else if (claims.get(APPLICATION) != null) {
                LinkedHashMap<String, String> application = (LinkedHashMap<String, String>) claims.get(APPLICATION);
                clientId = application.get(NAME) != null ? application.get(NAME) : DEFAULT_CLIENT_ID;
            }
            Map<String, List<String>> realmMap = (Map<String, List<String>>) claims.get(REALM_ACCESS);
            if (realmMap != null)
                benefitRoles = realmMap.get(ROLES);
        }
        if (fToken != null) {
            fToken = Common.getValidToken(fToken, "Bearer ");
            fTokenClaims = JWTToken.decodeJWTToken(fToken, GetKeys.getPrivateKey());
        }

        if (StringUtils.isNotEmpty(xToken) && xToken.startsWith("Bearer ")) {
            xTokenContextHolder = XTokenHelper.getXToken(xToken);
        }

        return RequestHeaders.builder()
                .roleList(benefitRoles != null ? benefitRoles : null)
                .clientId(clientId != null ? clientId : null)
                .benefitName(benefitName != null ? benefitName : null)
                .fTokenClaims(fTokenClaims)
                .xToken(xTokenContextHolder)
                .build();
    }
}
