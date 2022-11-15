package in.gov.abdm.abha.enrollment.model.idp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//It is an POJO class IdpSendOtpResponse
public class IdpSendOtpResponse {
    /**
     * It is a TransactionId
     */
    private String transactionId;
    /**
     * It is a Response
     */
    private Response response;
}
