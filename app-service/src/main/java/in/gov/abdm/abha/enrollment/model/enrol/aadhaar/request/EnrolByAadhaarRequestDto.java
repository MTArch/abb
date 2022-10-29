package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.validators.annotations.Consent;
import lombok.Data;

import javax.validation.Valid;

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
    @Valid
    AuthData authData;

    /**
     * It contains Consents
     */
    @JsonProperty("consent")
    @Valid
    ConsentDto consent;
}
