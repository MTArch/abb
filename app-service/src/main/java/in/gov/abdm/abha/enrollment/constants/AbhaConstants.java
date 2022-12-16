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
    String YEAR_OF_BIRTH_INVALID = "Invalid Year of birth";
    String MOBILE_NUMBER_MISSMATCH = "Invalid Mobile Number";
    String ABHA_ID = "ABHA_ID";
    String INVALID_NAME_FORMAT = "Invalid Name format";
    String VALIDATION_ERROR_GENDER_FIELD = "Invalid Gender";

    String VALIDATION_EMPTY_AUTHMETHOD = "AuthMethod cannot be null or empty";

    String VALIDATION_ERROR_AUTH_DATA_FIELD = "Auth data cannot be null or empty";
    String VALIDATION_ERROR_TIMESTAMP_FIELD = "Invalid Timestamp";
    String VALIDATION_ERROR_OTP_VALUE_FIELD = "Invalid OTP Value";

    String VALIDATION_ERROR_CONSENT_FIELD = "Consent cannot be null or empty";
    String VALIDATION_ERROR_CONSENT_CODE_FIELD = "Invalid Consent Code";
    String VALIDATION_ERROR_CONSENT_VERSION_FIELD = "Invalid Consent Version";
    String VALIDATION_ERROR_OTP_OBJECT = "otp cannot be null or empty OR timeStamp ,txnId or otpValue cannot be null or empty";

    String VALIDATION_ERROR_DEMO_OBJECT = "demo cannot be null or empty OR timeStamp ,aadhaar,name,nameMatchStrategy,gender,yob or mobile cannot be null or empty";

    String VALIDATION_ERROR_BIO_OBJECT = "bio cannot be null or empty OR timeStamp ,aadhaar or rPidData cannot be null or empty";
    String ACCOUNT_NOT_FOUND_EXCEPTION_MESSAGE = "No account found with AADHAAR Number.Please,create an account by clicking Click here to Create ABHA Number link.";
    String TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE = "Invalid transaction, either the transaction is expired or not found";
    String VALIDATION_ERROR_PARENT_ABHA_REQUEST_DEMO_OBJECT = "ParentAbhaRequest cannot be null or empty OR ABHANumber ,yearOfBirth,name,gender,email,relationship,document,or mobile cannot be null or empty";

    String VALIDATION_ERROR_CHILD_ABHA_REQUEST_DEMO_OBJECT = "ChildAbhaRequest cannot be null or empty OR ABHANumber cannot be null or empty";

    String INVALID_ABHA_NUMBER = "Invalid ABHA Number";

    String VALIDATION_ERROR_RELATIONSHIP_FIELD = "Invalid Relationship";

    String VALIDATION_ERROR_DOCUMENT_FIELD = "Invalid Document";

    String MESSAGE_TEMPLATES = "MESSAGE_TEMPLATES";

    String INVALID_LINK_REQUEST_EXCEPTION_MESSAGE = "INVALID REQUEST FOR PARENT LINKING";

    String INVALID_OTP = "Invalid OTP, Please try again.";

    String EXPIRED_OTP = "OTP expired, Please try again.";

    String INVALID_PARENT_ABHA_NUMBER = "Invalid Parent ABHA Number";
    
    String INVALID_CHILD_ABHA_NUMBER = "Invalid Child ABHA Number";

    String INVALID_EMAIL_ID = "Invalid Email Id";

    String INVALID_AADHAAR_OTP = "Invalid Aadhaar OTP";

    String AADHAAR_OTP_EXPIRED = "Aadhaar OTP expired";

    String NO_ACCOUNT_FOUND_WITH_AADHAAR_NUMBER = "No account found with AADHAAR Number. Please, create a new account.";
    
    String NO_ACCOUNT_FOUND = "No account found. Please, create a new account.";
    
    String INVALID_REQUEST = "Bad request, check request before retrying";
}
