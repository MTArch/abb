package in.gov.abdm.abha.enrollment.model.aadhaar.otp;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LocalizedLabels {
    private String name;
    private String abhaNumber;
    private String abhaAddress;
    private String gender;
    private String dob;
    private String mobile;
}
