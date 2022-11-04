package in.gov.abdm.abha.enrollment.model.otp_request;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.enums.request.OtpSystem;
import in.gov.abdm.abha.enrollment.enums.request.ScopeEnum;
import in.gov.abdm.abha.enrollment.validators.annotations.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
@ValidScope
@ValidLoginHint
@ValidLoginId
public class MobileOrEmailOtpRequestDto {

    /**
     * field to log transaction
     */
    @NotNull(message = AbhaConstants.VALIDATION_ERROR_TRANSACTION_FIELD)
    private String txnId;

    /**
     * refers to the Scope to which particular transaction belongs to
     * Existing scopes : abha-enrol,mobile-update,mobile-verify,email-update
     * Scope will be abha-enrol for abha creation using aadhaar
     */
    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_SCOPE_FIELD)
    private List<ScopeEnum> scope;

    /**
     * refers to the way user logs in to the system
     * Existing loginHint values: abha-number or mobile or phr-address
     * LoginHint will be empty for abha creation using aadhaar
     */
    @NotNull(message = AbhaConstants.VALIDATION_ERROR_LOGIN_HINT_FIELD)
    private String loginHint;

    /**
     * refers to the way user logs in to the system
     * Possible login id values : aadhaar number or mobile number
     */
    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_LOGIN_ID_FIELD)
    private String loginId;

    /**
     * refers to the system used to send verification otp
     * Possible values : aadhaar or abdm
     */
    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_OTP_SYSTEM_FIELD)
    @ValidOtpSystem
    private String otpSystem;
}
