package in.gov.abdm.abha.enrollment.model.otp_request;
import java.util.List;

import in.gov.abdm.abha.enrollment.enums.LoginHint;
import in.gov.abdm.abha.enrollment.enums.request.OtpSystem;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidLoginHint;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidLoginId;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidOtpSystem;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidScope;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidTransactionId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entity class which enables OTP auth/verfication using either Aadhaar or ABDM OTP systems.
 * If otpSystem is aadhaar then uses Aadhaar service for OTP
 * If otpSystem is ABDM then user linked mobile against the provided loginId is used for generating OTP
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ValidTransactionId
@ValidLoginHint
@ValidOtpSystem
@ValidLoginId
@ToString
public class MobileOrEmailOtpRequestDto {

    /**
     * field to log transaction
     */
    private String txnId;

    /**
     * refers to the Scope to which particular transaction belongs to
     * Existing scopes : abha-enrol,mobile-update,mobile-verify,email-update
     * Scope will be abha-enrol for abha creation using aadhaar
     */
    @ValidScope
    private List<Scopes> scope;

    /**
     * refers to the way user logs in to the system
     * Existing loginHint values: abha-number or mobile or phr-address
     * LoginHint will be empty for abha creation using aadhaar
     */
    private LoginHint loginHint;

    /**
     * refers to the way user logs in to the system
     * Possible login id values : aadhaar number or mobile number
     */

    private String loginId;

    /**
     * refers to the system used to send verification otp
     * Possible values : aadhaar or abdm
     */
    private OtpSystem otpSystem;
}
