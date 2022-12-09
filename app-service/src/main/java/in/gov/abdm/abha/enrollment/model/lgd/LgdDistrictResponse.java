package in.gov.abdm.abha.enrollment.model.lgd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LgdDistrictResponse {
    private String pinCode;
    private String districtCode;
    private String districtName;
    private String stateCode;
    private String stateName;
}
