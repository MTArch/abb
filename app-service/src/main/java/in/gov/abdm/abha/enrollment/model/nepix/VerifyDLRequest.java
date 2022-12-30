package in.gov.abdm.abha.enrollment.model.nepix;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyDLRequest {
    private String documentType;
    private String documentId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String dob;
    private String gender;
}
