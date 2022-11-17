package in.gov.abdm.abha.enrollment.model.aadhaar.otp;

import in.gov.abdm.abha.enrollment.constants.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * dto for auth otp details
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AadhaarAuthOtpDto {

    private String status;
    private String reason;
    private String errorCode;
    private String actionErrorCode;
    private String code;
    private String uidtkn;
    private String mobileNumber;
    private String email;

    public boolean isAuthenticated() {
        return status.equalsIgnoreCase(StringConstants.Y) || status.equalsIgnoreCase(StringConstants.SUCCESS);
    }
}
