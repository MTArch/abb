package in.gov.abdm.abha.enrollment.validators.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * enum constant for key containing request body validation error message
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
    authMethods(AbhaConstants.VALIDATION_EMPTY_AUTH_METHOD),
    txnId(AbhaConstants.VALIDATION_ERROR_TRANSACTION_FIELD),
    txnID(AbhaConstants.VALIDATION_ERROR_TXN_ID),
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
    documentType(AbhaConstants.INVALID_DOCUMENT_TYPE),
    documentId(AbhaConstants.INVALID_DOCUMENT_ID),
    email(AbhaConstants.INVALID_EMAIL_ID),
    dob(AbhaConstants.INVALID_DOB),
    frontSidePhoto(AbhaConstants.INVALID_FRONT_SIDE_PHOTO),
    backSidePhoto(AbhaConstants.INVALID_BACK_SIDE_PHOTO),
    state(AbhaConstants.INVALID_STATE),
    district(AbhaConstants.INVALID_DISTRICT),
    pinCode(AbhaConstants.INVALID_PIN_CODE),
    firstName(AbhaConstants.INVALID_FIRST_NAME),
    firstNameExceed(AbhaConstants.FIRST_NAME_EXCEED),
    middleName(AbhaConstants.INVALID_MIDDLE_NAME),
    middleNameExceed(AbhaConstants.MIDDLE_NAME_EXCEED),
    lastName(AbhaConstants.INVALID_LAST_NAME),
    lastNameExceed(AbhaConstants.LAST_NAME_EXCEED),
    address(AbhaConstants.INVALID_ADDRESS),
    addressExceed(AbhaConstants.ADDRESS_EXCEED),
    abhaAddress(AbhaConstants.VALIDATION_ERROR_ABHA_ADDRESS_FIELD),
    healthWorkerMobile(AbhaConstants.INVALID_HEALTH_WORKER_MOBILE_NUMBER),
    healthWorkerName(AbhaConstants.INVALID_HEALTH_WORKER_NAME),
    consentFormImage(AbhaConstants.INVALID_CONSENT_FORM_IMAGE),
    yearOfBirth(AbhaConstants.INVALID_YEAR_OF_BIRTH),
    mobileType(AbhaConstants.INVALID_MOBILE_TYPE),
    verificationStatus(AbhaConstants.INVALID_VERIFICATION_STATUS),
    message(AbhaConstants.INVALID_REASON),
    preferred(AbhaConstants.VALIDATION_ERROR_PREFERRED_FLAG);
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
