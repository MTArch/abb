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

    @JsonProperty("token")
    private String token;

    @JsonProperty("expiresIn")
    private Long expiresIn;

    @JsonProperty("refreshToken")
    private String refreshToken;

    @JsonProperty("refreshExpiresIn")
    private Long refreshExpiresIn;
}
