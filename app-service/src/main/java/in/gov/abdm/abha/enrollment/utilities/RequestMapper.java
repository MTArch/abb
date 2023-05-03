package in.gov.abdm.abha.enrollment.utilities;

import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.utilities.jwt.JWTUtil;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Autowired;

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

        if(authorization!=null) {
            claims = jwtUtil.readJWTToken(authorization);
            clientId = claims.get(CLIENT_ID) == null ? StringConstants.EMPTY : claims.get(CLIENT_ID).toString();
            Map<String, List<String>> realmMap = (Map<String, List<String>>) claims.get("realm_access");
            benefitRoles = realmMap.get("roles");
        }
        if(fToken!=null) {
            fTokenClaims = jwtUtil.getTokenClaims(fToken);
        }

        return RequestHeaders.builder()
                .roleList(benefitRoles !=null ? benefitRoles : null)
                .clientId(clientId !=null ? clientId : null)
                .benefitName(benefitName !=null ? benefitName : null)
                .fTokenClaims(fTokenClaims)
                .build();
    }
}
