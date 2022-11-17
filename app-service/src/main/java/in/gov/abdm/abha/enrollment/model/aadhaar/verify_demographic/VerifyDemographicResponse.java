package in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyDemographicResponse {
    private boolean isVerified;
    private String reason;
}
