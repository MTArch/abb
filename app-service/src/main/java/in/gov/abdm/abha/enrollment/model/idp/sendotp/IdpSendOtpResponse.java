package in.gov.abdm.abha.enrollment.model.idp.sendotp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 *It is an POJO class IdpSendOtpResponse
 */
public class IdpSendOtpResponse {
    /**
     * It is a TransactionId
     */
    @JsonProperty("transactionId")
    private String transactionId;
    /**
     * It is a Response
     */
    @JsonProperty("response")
    private Response response;
}
