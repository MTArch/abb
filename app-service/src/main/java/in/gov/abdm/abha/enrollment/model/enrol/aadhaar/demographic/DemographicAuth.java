
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
    @NotEmpty(message = AbhaConstants.AADHAAR_NUMBER_INVALID)
    private String aadhaarNumber;
    @NotEmpty(message = AbhaConstants.INVALID_NAME_FORMAT)
    private String name;
    private String dateOfBirth;
    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_GENDER_FIELD)
    private String gender;
    private String address;
    private String profilePhoto;
    private String mobile;

}