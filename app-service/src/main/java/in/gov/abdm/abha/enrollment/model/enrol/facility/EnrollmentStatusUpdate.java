package in.gov.abdm.abha.enrollment.model.enrol.facility;

import in.gov.abdm.abha.enrollment.enums.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentStatusUpdate {
    private String txnId;
    private EnrollmentStatus verificationStatus;
    private String message;
}
