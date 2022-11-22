package in.gov.abdm.abha.enrollment.constants;

public interface AbhaConstants {
    String VALIDATION_EMPTY_SCOPE_FIELD = "Scope cannot be null or empty";
    String VALIDATION_EMPTY_LOGIN_ID_FIELD = "LoginId cannot be null or Empty";
    String VALIDATION_EMPTY_OTP_SYSTEM_FIELD = "Otp System cannot be null or Empty";
    String VALIDATION_NULL_LOGIN_HINT_FIELD = "Login Hint cannot not be null";
    String VALIDATION_EMPTY_KEY_FIELD = "Key cannot be null or empty";
    String VALIDATION_EMPTY_VALUE_FIELD = "Value cannot be null or empty";

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

    String AADHAAR_NUMBER_INVALID = "Invalid Aadhaar Number";
    String PATTERN_MISMATCHED = "Pattern Mismatched for Year of Birth";
    String MOBILE_NUMBER_MISSMATCH = "Invalid Mobile Number";
    String ABHA_ID = "ABHA_ID";
    String INVALID_NAME_FORMAT = "Invalid Name format";
    String VALIDATION_ERROR_GENDER_FIELD = "Invalid Gender";

    String VALIDATION_EMPTY_AUTHMETHOD = "AuthMethod cannot be null or empty";

    String VALIDATION_ERROR_AUTHMETHOD = "Invalid AuthMethod";
    String VALIDATION_ERROR_TIMESTAMP_FIELD = "Invalid Timestamp";
    String VALIDATION_ERROR_OTP_VALUE_FIELD = "Invalid OTP Value";

    String VALIDATION_ERROR_CONSENT_FIELD = "Consent cannot be null or empty";
    String VALIDATION_ERROR_CONSENT_CODE_FIELD = "Invalid Consent Code";
    String VALIDATION_ERROR_CONSENT_VERSION_FIELD = "Invalid Consent Version";
    String VALIDATION_ERROR_OTP_OBJECT = "otp cannot be null or empty OR timeStamp ,txnId or otpValue cannot be null or empty";

    String VALIDATION_ERROR_DEMO_OBJECT = "demo cannot be null or empty OR timeStamp ,aadhaar,name,nameMatchStrategy,gender,yob or mobile cannot be null or empty";

    String VALIDATION_ERROR_BIO_OBJECT = "bio cannot be null or empty OR timeStamp ,aadhaar or rPidData cannot be null or empty";
    String ACCOUNT_NOT_FOUND_EXCEPTION_MESSAGE = "No account found with aadhaar number.Please,create account by clicking create account link";

    String VALIDATION_ERROR_PARENT_ABHA_REQUEST_DEMO_OBJECT = "ParentAbhaRequest cannot be null or empty OR ABHANumber ,yearOfBirth,name,gender,email,relationship,document,or mobile cannot be null or empty";

    String VALIDATION_ERROR_CHILD_ABHA_REQUEST_DEMO_OBJECT = "ChildAbhaRequest cannot be null or empty OR ABHANumber cannot be null or empty";

    String VALIDATION_NULL_ABHA_NUMBER = " Abha Number can not be null";

    String VALIDATION_ERROR_RELATIONSHIP_FIELD = "Invalid Relationship";

    String VALIDATION_ERROR_DOCUMENT_FIELD = "Invalid Document";


    String MESSAGE_TEMPLATES = "MESSAGE_TEMPLATES";

    String DETAILS_NOT_FOUND_EXCEPTION_MESSAGE = "DETAILS NOT PRESENT";

    String INVALID_LINK_REQUEST_EXCEPTION_MESSAGE = "INVALID REQUEST FOR PARENT LINKING";
}
