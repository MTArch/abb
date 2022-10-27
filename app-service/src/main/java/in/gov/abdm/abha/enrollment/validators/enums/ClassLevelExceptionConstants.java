package in.gov.abdm.abha.enrollment.validators.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * enum constant for key containing request body validations error message
 */
@Getter
@AllArgsConstructor
public enum ClassLevelExceptionConstants {
    scope(AbhaConstants.VALIDATION_ERROR_SCOPE_FIELD),
    loginHint(AbhaConstants.VALIDATION_ERROR_LOGIN_HINT_FIELD),
    loginId(AbhaConstants.VALIDATION_ERROR_LOGIN_ID_FIELD),
    consent(AbhaConstants.VALIDATION_ERROR_CONSENT_FIELD),
    otp(AbhaConstants.VALIDATION_ERROR_OTP_OBJECT),
    bio(AbhaConstants.VALIDATION_ERROR_BIO_OBJECT),
    demo(AbhaConstants.VALIDATION_ERROR_DEMO_OBJECT),
    authMethods(AbhaConstants.VALIDATION_EMPTY_AUTHMETHOD),
    txnId(AbhaConstants.VALIDATION_ERROR_TRANSACTION_FIELD),
    otpValue(AbhaConstants.VALIDATION_ERROR_OTP_VALUE_FIELD),
    aadhaarNumber(AbhaConstants.AADHAAR_NUMBER_INVALID),
    name(AbhaConstants.INVALID_NAME_FORMAT),
    gender(AbhaConstants.VALIDATION_ERROR_GENDER_FIELD),
    yob(AbhaConstants.PATTERN_MISMATCHED),
    mobile(AbhaConstants.MOBILE_NUMBER_MISSMATCH),
    timestamp(AbhaConstants.VALIDATION_ERROR_TIMESTAMP_FIELD);
    private final String value;


    @JsonCreator
    public static ClassLevelExceptionConstants fromText(String text){
        for(ClassLevelExceptionConstants r : ClassLevelExceptionConstants.values()){
            if(r.getValue().equals(text)){
                return r;
            }
        }
        throw new IllegalArgumentException();
    }
}
