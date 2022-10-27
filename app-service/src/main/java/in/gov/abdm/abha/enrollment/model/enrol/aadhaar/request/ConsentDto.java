package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.annotations.ConsentCode;
import in.gov.abdm.abha.enrollment.validators.annotations.ConsentVersion;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * It is Data Transfer Object for Consent
 */
@Data
public class ConsentDto {
    /**
     * It is code
     */
    @JsonProperty("code")
    @ConsentCode
    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_CONSENT_CODE_FIELD)
    private String code;
    /**
     * It is consent version( 1.4)
     */
    @JsonProperty("version")
    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_CONSENT_VERSION_FIELD)
    @ConsentVersion
    private String version;
}
