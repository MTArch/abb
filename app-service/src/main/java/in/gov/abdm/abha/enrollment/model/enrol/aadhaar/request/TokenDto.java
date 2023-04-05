package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * It is Data Transfer Object for Token
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto {

    /**
     * It is an authentication token
     */
    @JsonProperty("id_token")
    private String tokenId;
}
