package in.gov.abdm.abha.enrollment.model.enrol.facility;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.enums.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentStatusUpdate {
    @NotNull(message = AbhaConstants.VALIDATION_ERROR_TXN_ID)
    @NotBlank(message = VALIDATION_ERROR_TXN_ID)
    private String txnId;
    @Pattern(regexp = VERIFICATION_STATUS_REGEX, flags = Pattern.Flag.CASE_INSENSITIVE, message = INVALID_VERIFICATION_STATUS)
    private String verificationStatus;
    @Size(max = 255,message = INVALID_REASON)
    private String message;
}
