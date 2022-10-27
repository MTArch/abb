package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * It is Data Transfer Object for Token
 */
public class TokenDto {

    /**
     * It is an authentication token
     */
    //@NotNull(message = FIELD_BLANK_ERROR_MSG)
    // @Pattern(regexp = GRANT_TYPE_REGEX_PATTERN,message = GRANT_TYPE_INVALID_ERROR_MSG)
    @JsonProperty("id_token")
    private String tokenId;
}
