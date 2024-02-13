package in.gov.abdm.abha.enrollment.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class XTokenContextHolder {
    private String healthIdNumber;
    private String mobileNumber;
    private String email;
    private String clientId;
    private String clientIp;
    private String kycVerified;
    private String error;
}
