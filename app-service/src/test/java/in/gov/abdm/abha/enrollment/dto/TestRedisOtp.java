package in.gov.abdm.abha.enrollment.dto;

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
