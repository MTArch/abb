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
    consentCode(AbhaConstants.VALIDATION_ERROR_CONSENT_CODE_FIELD),
    consentVersion(AbhaConstants.VALIDATION_ERROR_CONSENT_VERSION_FIELD),
    otp(AbhaConstants.VALIDATION_ERROR_OTP_OBJECT),
    bio(AbhaConstants.VALIDATION_ERROR_BIO_OBJECT),
    demo(AbhaConstants.VALIDATION_ERROR_DEMO_OBJECT),
    authData(AbhaConstants.VALIDATION_ERROR_AUTH_DATA_FIELD),
    authMethods(AbhaConstants.VALIDATION_EMPTY_AUTHMETHOD),
    txnId(AbhaConstants.VALIDATION_ERROR_TRANSACTION_FIELD),
    otpValue(AbhaConstants.VALIDATION_ERROR_OTP_VALUE_FIELD),
    Timestamp(AbhaConstants.VALIDATION_ERROR_TIMESTAMP_FIELD),
    name(AbhaConstants.INVALID_NAME_FORMAT),
    gender(AbhaConstants.VALIDATION_ERROR_GENDER_FIELD),
    yob(AbhaConstants.YEAR_OF_BIRTH_INVALID),
    mobile(AbhaConstants.MOBILE_NUMBER_MISSMATCH),
    aadhaarNumber(AbhaConstants.AADHAAR_NUMBER_INVALID),
    otpSystem(AbhaConstants.VALIDATION_ERROR_OTP_SYSTEM_FIELD),
    relationship(AbhaConstants.VALIDATION_ERROR_RELATIONSHIP_FIELD),
    document(AbhaConstants.VALIDATION_ERROR_DOCUMENT_FIELD),
    abhaNumber(AbhaConstants.INVALID_PARENT_ABHA_NUMBER),
    AbhaNumber(AbhaConstants.INVALID_CHILD_ABHA_NUMBER),
    email(AbhaConstants.INVALID_EMAIL_ID);
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
