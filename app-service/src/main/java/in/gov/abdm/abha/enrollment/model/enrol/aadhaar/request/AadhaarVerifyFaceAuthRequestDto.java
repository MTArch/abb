package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AadhaarVerifyFaceAuthRequestDto {
    private String aadhaarNumber;
    private String faceAuthPid;
    private String aadhaarLogType;
}
