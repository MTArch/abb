package in.gov.abdm.abha.enrollment.model.authbyabdm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
/**
 * It is a AuthByAbdmResponse
 */
public class AuthByAbdmResponse {
    /**
     * It is a txnId
     */
    @JsonProperty("txnId")
    public String txnId;
    /**
     * It is a authResult
     */
    @JsonProperty("authResult")
    public String authResult;
    /**
     * It is a accounts
     */
    @JsonProperty("accounts")
    public Accounts accounts;
}
