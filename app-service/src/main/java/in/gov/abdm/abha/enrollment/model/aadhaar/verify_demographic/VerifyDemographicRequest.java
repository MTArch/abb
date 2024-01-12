package in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyDemographicRequest {
    private String aadhaarNumber;
    private String name;
    private String phone;
    private String gender;
    private String dob;
    private String email;
    private String aadhaarLogType;
}
