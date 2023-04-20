package in.gov.abdm.abha.enrollment.aadhaar.send_otp.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TestRedisOtp {
    private String txnId;
    private String aadhaarTxnId;
    private String receiver;
    private String otpValue;
}
