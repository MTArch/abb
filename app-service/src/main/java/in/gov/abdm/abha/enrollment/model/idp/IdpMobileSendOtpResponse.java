package in.gov.abdm.abha.enrollment.model.idp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

/**
 * It is IdpMobileSendOtpResponse pojo class
 */
public class IdpMobileSendOtpResponse {

    private String transactionId;

    private Response response;
}
