package in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AccountResponseDto;
import in.gov.abdm.abha.enrollment.model.idp.sendotp.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

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
//    public Kyc kyc;
    private List<Kyc> kyc;

    /**
     * It is Response
     */
    @JsonProperty("response")
    public Response response;

    private ErrorResponse error;
}
