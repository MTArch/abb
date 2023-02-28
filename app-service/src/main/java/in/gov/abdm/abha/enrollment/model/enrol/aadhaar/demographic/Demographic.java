
package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.demographic;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Demographic {
    @NotEmpty(message = AbhaConstants.AADHAAR_NUMBER_INVALID)
    @JsonProperty("aadhaar")
    private String aadhaarNumber;
    @NotEmpty(message = AbhaConstants.INVALID_FIRST_NAME)
    private String firstName;
    private String middleName;
    private String lastName;
    private String dayOfBirth;
    private String monthOfBirth;
    @NotEmpty(message = AbhaConstants.INVALID_YEAR_OF_BIRTH)
    private String yearOfBirth;
    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_GENDER_FIELD)
    private String gender;
    @NotEmpty(message = AbhaConstants.INVALID_MOBILE_NUMBER)
    private String mobile;
    @NotEmpty(message = AbhaConstants.INVALID_STATE)
    private String state;
    @NotEmpty(message = AbhaConstants.INVALID_DISTRICT)
    private String district;
    @NotEmpty(message = AbhaConstants.INVALID_PIN_CODE)
    private String pinCode;
    @NotEmpty(message = AbhaConstants.INVALID_ADDRESS)
    private String address;
    @NotEmpty(message = AbhaConstants.INVALID_CONSENT_FORM_IMAGE)
    private String consentFormImage;
    @NotEmpty(message = AbhaConstants.INVALID_HEALTH_WORKER_NAME)
    private String healthWorkerName;
    @NotEmpty(message = AbhaConstants.INVALID_HEALTH_WORKER_MOBILE_NUMBER)
    private String healthWorkerMobile;
}