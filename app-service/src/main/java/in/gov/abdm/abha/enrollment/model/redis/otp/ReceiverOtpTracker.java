package in.gov.abdm.abha.enrollment.model.redis.otp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiverOtpTracker implements Serializable {
    private String receiver;
    private Integer sentOtpCount;
    private Integer verifyOtpCount;
    private boolean blocked;
}
