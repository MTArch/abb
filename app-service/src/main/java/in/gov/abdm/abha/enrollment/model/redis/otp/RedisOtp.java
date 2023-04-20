package in.gov.abdm.abha.enrollment.model.redis.otp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RedisOtp implements Serializable {
    private String txnId;
    private String aadhaarTxnId;
    private String receiver;
    private String otpValue;
}
