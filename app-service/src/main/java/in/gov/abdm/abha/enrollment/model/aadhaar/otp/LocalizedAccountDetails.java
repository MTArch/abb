package in.gov.abdm.abha.enrollment.model.aadhaar.otp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocalizedAccountDetails {
    private String name;
    private String stateName;
    private String districtName;
    private String subDistrictName;
    private String villageName;
    private String wardName;
    private String townName;
    private String address;
}