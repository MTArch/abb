package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AadhaarVerifyOtpRequestDto {
    private String aadhaarNumber;
    @JsonProperty("txnId")
    private String aadhaarTransactionId;
    private String otp;
    private String faceAuthPid;
}
