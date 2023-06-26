
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
    private String yearOfBirth;
    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_GENDER_FIELD)
    private String gender;
    private String mobile;
    @NotNull(message = AbhaConstants.INVALID_MOBILE_TYPE)
    private MobileType mobileType;
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