package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.annotations.Consent;
import lombok.Data;

/**
 * Data transfer object for enrol by aadhaar request
 */
@Consent
@Data
public class EnrolByAadhaarRequestDto {

    /**
     * It contains authData
     */
    @JsonProperty("authData")
    @NotNull(message = AbhaConstants.VALIDATION_ERROR_AUTH_DATA_FIELD)
    @Valid
    AuthData authData;

    /**
     * It contains Consents
     */
    @JsonProperty("consent")
    @Valid
    ConsentDto consent;
}
