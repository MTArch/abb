package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * It is Data Transfer Object for Token
 */
public class TokenDto {

    /**
     * It is an authentication token
     */
    @JsonProperty("id_token")
    private String tokenId;
}
