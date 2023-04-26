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
public class AadhaarVerifyBioRequestDto {
    private String aadhaarNumber;
    private String fingerPrintAuthPid;
}
