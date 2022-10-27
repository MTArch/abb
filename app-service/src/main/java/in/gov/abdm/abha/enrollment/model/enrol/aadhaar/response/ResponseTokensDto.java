package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseTokensDto {

    /**
     * It is token id
     */
    @JsonProperty("id_token")
    private String tokenId;
    /**
     * It is token type
     */
    @JsonProperty("token_type")
    private String tokenType;
    /**
     * It is Token expiration
     */
    @JsonProperty("expires_in")
    private int expiresIn;
    /**
     * It is refresh token
     */
    @JsonProperty("refresh_token")
    private String refreshToken;
}
