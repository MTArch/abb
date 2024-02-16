
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
public class Demographic extends LdgRequest {
    @NotEmpty(message = AbhaConstants.AADHAAR_NUMBER_INVALID)
    @JsonProperty("aadhaar")
    private String aadhaarNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private String dayOfBirth;
    private String monthOfBirth;
    private String yearOfBirth;
    private String gender;
    private String mobile;
    private MobileType mobileType;
    private String state;
    private String district;
    private String pinCode;
    private String address;
    private String consentFormImage;
    private String healthWorkerName;
    private String healthWorkerMobile;
    private String validity;
}