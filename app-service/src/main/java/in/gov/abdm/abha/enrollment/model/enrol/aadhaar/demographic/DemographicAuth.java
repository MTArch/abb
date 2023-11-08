
package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.demographic;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.MobileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DemographicAuth extends LdgRequest {
    @JsonProperty("aadhaar")
    private String aadhaarNumber;
    private String name;
    private String birthOfDay;
    private String gender;
    private String pinCode;
    private String address;
    private String consentFormImage;
    private String mobile;

}