package in.gov.abdm.abha.enrollment.constants;

public class AbhaConstants {
    private AbhaConstants() {
    }

    public static final String VALIDATION_EMPTY_KEY_FIELD = "Key cannot be null or empty";
    public static final String VALIDATION_EMPTY_VALUE_FIELD = "Value cannot be null or empty";
    public static final String VALIDATION_ERROR_TRANSACTION_FIELD = "Invalid Transaction Id";
    public static final String VALIDATION_ERROR_SCOPE_FIELD = "Invalid Scope";
    public static final String VALIDATION_ERROR_LOGIN_HINT_FIELD = "Invalid Login Hint";
    public static final String VALIDATION_ERROR_LOGIN_ID_FIELD = "Invalid LoginId";
    public static final String VALIDATION_ERROR_OTP_SYSTEM_FIELD = "Invalid Otp System";


    public static final String FIELD_BLANK_ERROR_MSG = "Please enter a Valid value for the specified field. " +
            "Valid Format Reference: 1. The specified field is mandatory (not null), 2. The specified field shouldn't be Blank, 3. The specified field shouldn't be Empty.";



    public static final String AADHAAR_NUMBER_INVALID = "Invalid Aadhaar Number";
    public static final String YEAR_OF_BIRTH_INVALID = "Invalid Year of birth";
    public static final String MOBILE_NUMBER_MISSMATCH = "Invalid Mobile Number";
    public static final String ABHA_ID = "ABHA_ID";
    public static final String INVALID_NAME_FORMAT = "Invalid Name format";
    public static final String VALIDATION_ERROR_GENDER_FIELD = "Invalid Gender";

    public static final String VALIDATION_EMPTY_AUTH_METHOD = "Invalid Auth Method";

    public static final String VALIDATION_ERROR_AUTH_DATA_FIELD = "Auth data cannot be null or empty";
    public static final String VALIDATION_ERROR_TXN_ID = "Txn ID cannot be null or empty";
    public static final String VALIDATION_ERROR_TIMESTAMP_FIELD = "Invalid Timestamp";
    public static final String VALIDATION_ERROR_OTP_VALUE_FIELD = "Invalid OTP Value";

    public static final String VALIDATION_ERROR_CONSENT_FIELD = "Consent cannot be null or empty";
    public static final String VALIDATION_ERROR_CONSENT_CODE_FIELD = "Invalid Consent Code";
    public static final String VALIDATION_ERROR_CONSENT_VERSION_FIELD = "Invalid Consent Version";
    public static final String VALIDATION_ERROR_OTP_OBJECT = "otp cannot be null or empty OR timeStamp ,txnId or otpValue cannot be null or empty";

    public static final String VALIDATION_ERROR_DEMO_OBJECT = "demo cannot be null or empty OR timeStamp ,aadhaar,name,nameMatchStrategy,gender,yob or mobile cannot be null or empty";

    public static final String VALIDATION_ERROR_BIO_OBJECT = "bio cannot be null or empty OR timeStamp ,aadhaar or rPidData cannot be null or empty";
    public static final String TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE = "Invalid transaction, either the transaction is expired or not found";

    public static final String ENROLLMENT_NOT_FOUND_EXCEPTION_MESSAGE = "The provided Enrolment number does not exist.";
    public static final String VALIDATION_ERROR_PARENT_ABHA_REQUEST_DEMO_OBJECT = "ParentAbhaRequest cannot be null or empty OR ABHANumber ,yearOfBirth,name,gender,email,relationship,document,or mobile cannot be null or empty";

    public static final String VALIDATION_ERROR_CHILD_ABHA_REQUEST_DEMO_OBJECT = "ChildAbhaRequest cannot be null or empty OR ABHANumber cannot be null or empty";

    public static final String VALIDATION_ERROR_RELATIONSHIP_FIELD = "Invalid Relationship";

    public static final String VALIDATION_ERROR_DOCUMENT_FIELD = "Invalid Document";
    public static final String INVALID_REASON = "Maximum length allowed is 255";
    public static final String INVALID_VERIFICATION_STATUS = "Allowed values are 'ACCEPT' or 'REJECT'";
    public static final String VERIFICATION_STATUS_REGEX = "ACCEPT|REJECT";


    public static final String MESSAGE_TEMPLATES = "MESSAGE_TEMPLATES";
    public static final String INVALID_OTP = "Invalid OTP, Please try again.";
    public static final String INVALID_PARENT_ABHA_NUMBER = "Invalid Parent ABHA Number";
    public static final String INVALID_CHILD_ABHA_NUMBER = "Invalid Child ABHA Number";
    public static final String INVALID_EMAIL_ID = "Invalid Email Id";
    public static final String INVALID_AADHAAR_OTP = "Invalid Aadhaar OTP";
    public static final String AADHAAR_OTP_EXPIRED = "Aadhaar OTP expired";
    public static final String NO_ACCOUNT_FOUND_WITH_AADHAAR_NUMBER = "No account found with AADHAAR Number. Please, create a new account.";

    public static final String NO_ACCOUNT_FOUND = "No account found. Please, create a new account.";
    public static final String DRIVING_LICENCE = "DRIVING_LICENCE";
    public static final String PROVISIONAL = "PROVISIONAL";
    public static final String INVALID_DOCUMENT_TYPE = "Invalid Document Type";
    public static final String INVALID_DOCUMENT_ID = "Invalid Document Id";
    public static final String INVALID_FIRST_NAME = "Invalid First Name";
    public static final String FIRST_NAME_EXCEED = "First Name exceeded 255 characters";

    public static final String INVALID_MIDDLE_NAME = "Invalid Middle Name";

    public static final String MIDDLE_NAME_EXCEED = "Middle Name exceeded 255 characters";

    public static final String INVALID_LAST_NAME = "Invalid Last Name";

    public static final String LAST_NAME_EXCEED = "Last Name exceeded 255 characters";

    public static final String INVALID_DOB = "Invalid DOB";

    public static final String INVALID_FRONT_SIDE_PHOTO = "Invalid Front side photo";

    public static final String INVALID_BACK_SIDE_PHOTO = "Invalid Back side photo";

    public static final String INVALID_ADDRESS = "Invalid Address";

    public static final String ADDRESS_EXCEED = "Address exceeded 255 characters";

    public static final String INVALID_STATE = "Invalid State";

    public static final String INVALID_DISTRICT = "Invalid District";

    public static final String INVALID_DOCUMENT_PHOTO_SIZE = "The size of the document uploaded exceeds the permissible limits. Please upload a document of size less than 150KB";

    public static final String SAME_PHOTO_EXCEPTION = "Front and backside photos cannot be the same. Please upload different photos for both sides.";

    public static final String INVALID_PHOTO_FORMAT = "Invalid file extension. Please upload a file with extensions as jpg/png";

    public static final String INVALID_PIN_CODE = "Invalid PinCode";

    public static final String AADHAAR = "AADHAAR";
    public static final String OFFLINE_AADHAAR = "OFFLINE_AADHAAR";

    public static final String VERIFIED = "VERIFIED";

    public static final String DOCUMENT_TYPE = "documentType";

    public static final String VALIDATION_ERROR_ABHA_ADDRESS_FIELD = "Invalid Abha Address";

    public static final String VALIDATION_ERROR_PREFERRED_FLAG = "Invalid Preferred Flag";

    public static final String ABHA_ENROL_LOG_PREFIX = "ABHA_ENROL_LOG_PREFIX: ";

    public static final String SENT = "sent";

    public static final String TRANSACTION_ID = "TRANSACTION_ID";
    public static final String AADHAAR_SERVICE_CLIENT = "aadhaar-service-client";

    public static final String ABHA_DB_ACCOUNT_AUTH_METHODS_CLIENT = "abha-db-account-auth-methods-client";

    public static final String ABHA_DB_ACCOUNT_CLIENT = "abha-db-account-client";
    public static final String ABHA_DB_TRANSACTION_CLIENT = "abha-db-transaction-client";
    public static final String ABHA_DB_ACCOUNT_ACTION_CLIENT = "abha-db-account-action-client";

    public static final String ABHA_DB_DEPENDENT_ACCOUNT_RELATIONSHIP_CLIENT = "abha-db-dependent-account-relationship-client";
    public static final String DOCUMENT_DB_IDENTITY_DOCUMENT_CLIENT = "document-db-identity-document-client";
    public static final String DOCUMENT_APP_CLIENT = "document-app-client";
    public static final String ABHA_DB_HID_PHR_ADDRESS_CLIENT = "abha-db-hid-phr-address-client";
    public static final String IDP_APP_CLIENT = "idp-app-client";
    public static final String LGD_APP_CLIENT = "lgd-app-client";
    public static final String NOTIFICATION_APP_SERVICE = "notification-app-service";

    public static final String NOTIFICATION_DB_SERVICE = "notification-db-service";

    public static final String TOKEN_TYPE_TRANSACTION = "Transaction";
    public static final String TOKEN_TYPE_REFRESH = "Refresh";
    public static final String CLIENT_ID_VALUE = "abha-profile-app-api";
    public static final String SYSTEM_VALUE = "ABHA-N";

    //Demographic validations
    public static final String INVALID_YEAR_OF_BIRTH = "Invalid Year of Birth";
    public static final String INVALID_DAY_OF_BIRTH = "Invalid Day of Birth";
    public static final String INVALID_MONTH_OF_BIRTH = "Invalid Month of Birth";
    public static final String INVALID_MOBILE_NUMBER = "Invalid Mobile Number";
    public static final String INVALID_MOBILE_TYPE = "Invalid Mobile Type";
    public static final String INVALID_HEALTH_WORKER_NAME = "Invalid Health Health Worker Name";
    public static final String INVALID_HEALTH_WORKER_MOBILE_NUMBER = "Invalid Health Worker Mobile Number";
    public static final String INVALID_CONSENT_FORM_IMAGE = "Invalid Consent Form Image";
    public static final String THIS_ACCOUNT_ALREADY_EXIST = "This account already exist";
    public static final String ACCOUNT_CREATED_SUCCESSFULLY = "Account created successfully";

    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String UTC_TIMEZONE_ID = "UTC";

    public static final String AUTHORIZATION = "Authorization";
    public static final String AADHAAR_TECHNICAL_ERROR_MSG = "Technical error that are internal to authentication server.";
    public static final String EMAIL_HIDE_REGEX = "(^[^@]{3}|(?!^)\\G)[^@]";

    public static final String EMAIL_MASK_CHAR = "$1*";

    public static final String EMAIL_REGEX_PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

}
