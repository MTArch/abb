package in.gov.abdm.abha.enrollment.model.redis.otp;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class RedisOtp implements Serializable {
    private String txnId;
    private String aadhaarTxnId;
    private String receiver;
    private String otpValue;
}
