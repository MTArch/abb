package in.gov.abdm.abha.enrollment.utilities;

import in.gov.abdm.abha.enrollment.configuration.ContextHolder;
import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.utilities.jwt.JWTUtil;
import in.gov.abdm.abha.profile.utilities.Common;
import in.gov.abdm.abha.profile.utilities.GetKeys;
import in.gov.abdm.jwt.util.JWTToken;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static in.gov.abdm.constant.ABDMConstant.CLIENT_ID;

@UtilityClass
public class RequestMapper {
    @Autowired
    JWTUtil jwtUtil;

    public static RequestHeaders prepareRequestHeaders(String benefitName, String authorization,String fToken) {

        Map<String, Object> claims;
        String clientId="";
        List<String> benefitRoles = null;
        Map<String, Object> fTokenClaims = null;

        if (authorization != null) {
            claims = JWTUtil.readJWTToken(authorization);
            if (claims.get(CLIENT_ID) != null) {
                clientId = claims.get(CLIENT_ID).toString();
            } else if (claims.get("application") != null) {
                LinkedHashMap<String, String> application = (LinkedHashMap<String, String>) claims.get("application");
                clientId = application.get("name") != null ? application.get("name") : null;
            }
            Map<String, List<String>> realmMap = (Map<String, List<String>>) claims.get("realm_access");
            if (realmMap != null)
                benefitRoles = realmMap.get("roles");
        }
        if(fToken!=null) {
            fToken = Common.getValidToken(fToken, "Bearer ");
            fTokenClaims = JWTToken.decodeJWTToken(fToken, GetKeys.getPrivateKey());
        }

        return RequestHeaders.builder()
                .roleList(benefitRoles !=null ? benefitRoles : null)
                .clientId(clientId !=null ? clientId : null)
                .benefitName(benefitName !=null ? benefitName : null)
                .fTokenClaims(fTokenClaims)
                .build();
    }
}
