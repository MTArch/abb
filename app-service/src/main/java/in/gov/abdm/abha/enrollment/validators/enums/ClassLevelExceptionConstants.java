package in.gov.abdm.abha.enrollment.validators.enums;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ClassLevelExceptionConstants {
    txnId(AbhaConstants.VALIDATION_ERROR_TRANSACTION_FIELD),
    scope(AbhaConstants.VALIDATION_ERROR_SCOPE_FIELD),
    loginHint(AbhaConstants.VALIDATION_ERROR_LOGIN_HINT_FIELD),
    loginId(AbhaConstants.VALIDATION_ERROR_LOGIN_ID_FIELD),
    consent(AbhaConstants.VALIDATION_ERROR_CONSENT_FIELD),
    otp(AbhaConstants.VALIDATION_ERROR_OTP_FIELD);
    private final String value;
}
