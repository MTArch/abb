package in.gov.abdm.abha.enrollment.aadhaar.send_otp.dto;

import in.gov.abdm.abha.enrollment.validators.annotations.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

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
public class TestMobileOrEmailOtpRequestDto {

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
    private List<String> scope;

    /**
     * refers to the way user logs in to the system
     * Existing loginHint values: abha-number or mobile or phr-address
     * LoginHint will be empty for abha creation using aadhaar
     */
    private String loginHint;

    /**
     * refers to the way user logs in to the system
     * Possible login id values : aadhaar number or mobile number
     */

    private String loginId;

    /**
     * refers to the system used to send verification otp
     * Possible values : aadhaar or abdm
     */
    private String otpSystem;
}
