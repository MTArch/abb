package in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse;

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
 * It is IdpVerifyOtpResponse class for	http://global2dev.abdm.gov.internal/api/v3/identity/verify?otp=213382 api
 */
public class IdpVerifyOtpResponse {
    /**
     * it is preferredAbhaAddress
     */
    @JsonProperty("preferredAbhaAddress")
    public String preferredAbhaAddress;
    /**
     * It is Kyc
     */
    @JsonProperty("kyc")
    public Kyc kyc;
    /**
     * It is Response
     */
    @JsonProperty("response")
    public Response response;
}
