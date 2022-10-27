package in.gov.abdm.abha.enrollment.constants;

public interface AbhaConstants {
    String VALIDATION_EMPTY_SCOPE_FIELD = "Scope cannot be null or empty";
    String VALIDATION_EMPTY_LOGIN_ID_FIELD = "LoginId cannot be null or Empty";
    String VALIDATION_EMPTY_OTP_SYSTEM_FIELD = "Otp System cannot be null or Empty";
    String VALIDATION_NULL_LOGIN_HINT_FIELD = "Login Hint cannot not be null";

    String VALIDATION_ERROR_TRANSACTION_FIELD = "Invalid Transaction Id";
    String VALIDATION_ERROR_SCOPE_FIELD = "Invalid Scope";
    String VALIDATION_ERROR_LOGIN_HINT_FIELD = "Invalid Login Hint";
    String VALIDATION_ERROR_LOGIN_ID_FIELD = "Invalid LoginId";
    String VALIDATION_ERROR_OTP_SYSTEM_FIELD = "Invalid Otp System";


    String FIELD_BLANK_ERROR_MSG = "Please enter a Valid value for the specified field. " +
            "Valid Format Reference: 1. The specified field is mandatory (not null), 2. The specified field shouldn't be Blank, 3. The specified field shouldn't be Empty.";

    String MISSING_QUOTES_IN_KEY_ERROR_CONDITION = "was expecting double-quote to start field name";
    String MISSING_QUOTES_IN_KEY_ERROR_MESSAGE = "JSON Syntax Error: Missing double quotes in the key part of JSON on line:";
    String MISSING_QUOTES_IN_VALUE_ERROR_CONDITION = "was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')";
    String MISSING_QUOTES_IN_VALUE_ERROR_MESSAGE = "JSON Syntax Error: Missing double quotes in the value part of JSON on line: ";
    String MISSING_COLON_ERROR_CONDITION = "was expecting a colon to separate field name and value";
    String MISSING_COLON_ERROR_MESSAGE = "JSON Syntax Error: Missing colon to separate the key part and the value part at line: ";
    String MISSING_COMMA_ERROR_CONDITION = "was expecting comma to separate Object entries";
    String MISSING_COMMA_ERROR_MESSAGE = "JSON Syntax Error: Missing comma at the end of line: ";
    String SUCCESS_MESSAGE = "Entered Input is Valid";
    String AADHAAR_NUMBER_INVALID = "AADHAAR_NUMBER_INVALID";
    String PATTREN_MISMATCHED = "PATTREN_MISMATCHED";
    String OTP_MISSMATCH = "OTP_MISSMATCH";
    String UUID_MISSMATCH = "UUID_MISSMATCH";
    String MOBILE_NUMBER_MISSMATCH = "MOBILE_NUMBER_MISSMATCH";
    String ABHA_ID = "ABHA_ID";
    String INVALID_NAME_FORMAT = "INVALID_NAME_FORMAT";

    String VALIDATION_EMPTY_AUTHMETHOD = "AuthMethod cannot be null or empty";

    String VALIDATION_ERROR_AUTHMETHOD = "Invalid AuthMethod";
    String VALIDATION_ERROR_TIMESTAMP_FIELD = "Invalid Timestamp";
    String VALIDATION_ERROR_OTP_VALUE_FIELD = "Invalid OTP Value";

    String VALIDATION_ERROR_CONSENT_FIELD = "Consent cannot be null or empty";
    String VALIDATION_ERROR_CONSENT_CODE_FIELD = "Invalid Code or null or empty";
    String VALIDATION_ERROR_CONSENT_VERSION_FIELD = "Invalid Version or null or empty";

    String VALIDATION_ERROR_OTP_FIELD = "timeStamp ,txnId or otpValue is Null or Empty";


}
