package in.gov.abdm.abha.enrollment.model.hidbenefit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestHeaders {

    private List<String> roleList;
    private String clientId;
    private String benefitName;
    private Map<String, Object> fTokenClaims;
}
