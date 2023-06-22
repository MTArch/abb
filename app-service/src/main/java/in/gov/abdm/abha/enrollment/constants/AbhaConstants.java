package in.gov.abdm.abha.enrollment.constants;

public class AbhaConstants {

    private AbhaConstants() {}
    public static final String ABDM_REDIS_SERVER = "${abdm.redis.server}";
    public static final String ABDM_REDIS_PORT = "${abdm.redis.port}";
    public static final String ABDM_REDIS_PASSWORD = "${abdm.redis.password}";
    public static final String ABDM_REDIS_DATABASE = "${abdm.redis.database}";
    public static final String VALIDATION_EMPTY_KEY_FIELD = "Key cannot be null or empty";
    public static final String VALIDATION_EMPTY_VALUE_FIELD = "Value cannot be null or empty";
    public static final String VALIDATION_ERROR_TRANSACTION_FIELD = "Invalid Transaction Id";
    public static final String VALIDATION_ERROR_SCOPE_FIELD = "Invalid Scope";
    public static final String VALIDATION_ERROR_LOGIN_HINT_FIELD = "Invalid Login Hint";
    public static final String VALIDATION_ERROR_LOGIN_ID_FIELD = "Invalid LoginId";
    public static final String VALIDATION_ERROR_OTP_SYSTEM_FIELD = "Invalid Otp System";
    public static final String MOBILE_ALREADY_LINKED_TO_MAX_ACCOUNTS = "The mobile number provided by you is already linked to {0} ABHA Numbers. Please provide a different Mobile Number.";
    public static final String EMAIL_ALREADY_LINKED_TO_MAX_ACCOUNTS = "The email address provided by you is already linked to {0} ABHA Numbers. Please provide a different email address.";


    public static final String FIELD_BLANK_ERROR_MSG = "Please enter a Valid value for the specified field. " +
            "Valid Format Reference: 1. The specified field is mandatory (not null), 2. The specified field shouldn't be Blank, 3. The specified field shouldn't be Empty.";



    public static final String AADHAAR_NUMBER_INVALID = "Invalid Aadhaar Number";
    public static final String YEAR_OF_BIRTH_INVALID = "Invalid Year of birth";
    public static final String MOBILE_NUMBER_MISSMATCH = "Invalid Mobile Number";

    public static final String INVALID_NAME_FORMAT = "Invalid Name format";
    public static final String VALIDATION_ERROR_GENDER_FIELD = "Invalid Gender";

    public static final String VALIDATION_EMPTY_AUTH_METHOD = "Invalid Auth Method";

    public static final String VALIDATION_ERROR_AUTH_DATA_FIELD = "Auth data cannot be null or empty";
    public static final String VALIDATION_ERROR_TXN_ID = "Invalid Transaction Id";
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
    public static final String VALIDATION_ERROR_DL_NUMBER = "Please enter a valid Driving Licence number.";
    public static final String INVALID_REASON = "Maximum limit is 255 characters";
    public static final String INVALID_VERIFICATION_STATUS = "Allowed values are 'ACCEPT' or 'REJECT'";
    public static final String VERIFICATION_STATUS_REGEX = "ACCEPT|REJECT";


    public static final String MESSAGE_TEMPLATES = "MESSAGE_TEMPLATES";
    public static final String INTEGRATED_PROGRAMS = "INTEGRATED_PROGRAMS";
    public static final String INVALID_OTP = "Invalid OTP, Please try again.";
    public static final String INVALID_PARENT_ABHA_NUMBER = "Invalid Parent ABHA Number";
    public static final String INVALID_CHILD_ABHA_NUMBER = "Invalid Child ABHA Number";
    public static final String INVALID_EMAIL_ID = "Invalid Email Id";
    public static final String INVALID_AADHAAR_OTP = "Please enter a valid OTP ";
    public static final String AADHAAR_OTP_EXPIRED = "Aadhaar OTP expired";
    public static final String NO_ACCOUNT_FOUND_WITH_AADHAAR_NUMBER = "No account found with AADHAAR Number. Please, create a new account.";

    public static final String NO_ACCOUNT_FOUND = "No account found. Please, create a new account.";
    public static final String
            DRIVING_LICENCE = "DRIVING_LICENCE";
    public static final String PROVISIONAL = "PROVISIONAL";
    public static final String INVALID_DOCUMENT_TYPE = "Invalid Document Type";
    public static final String INVALID_DOCUMENT_ID = "Invalid Document Id";
    public static final String INVALID_FIRST_NAME = "Invalid First Name";
    public static final String FIRST_NAME_EXCEED = "First name should not exceed limit of 255 characters";

    public static final String INVALID_MIDDLE_NAME = "Invalid Middle Name";

    public static final String MIDDLE_NAME_EXCEED = "Middle name should not exceed limit of 255 characters";

    public static final String INVALID_LAST_NAME = "Invalid Last Name";

    public static final String LAST_NAME_EXCEED = "Last name should not exceed limit of 255 characters";

    public static final String INVALID_DOB = "Invalid DOB";

    public static final String INVALID_FRONT_SIDE_PHOTO = "Invalid Front side photo";

    public static final String INVALID_BACK_SIDE_PHOTO = "Invalid Back side photo";

    public static final String INVALID_ADDRESS = "Invalid Address";

    public static final String ADDRESS_EXCEED = "Address should not exceed limit of 255 characters";

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

    public static final String VALIDATION_ERROR_ABHA_ADDRESS_CANNOT_14_DIGIT_NO = "The ABHA address cannot consist of a 14-digit number exclusively.";

    public static final String VALIDATION_ERROR_PREFERRED_FLAG = "Invalid Preferred Flag";

    public static final String ABHA_ENROL_LOG_PREFIX = "ABHA_ENROL_LOG_PREFIX: ";

    public static final String SENT = "sent";

    public static final String TRANSACTION_ID = "TRANSACTION_ID";
    public static final String AADHAAR_SERVICE_CLIENT = "aadhaar-service-client";

    public static final String ABHA_DB_ACCOUNT_AUTH_METHODS_CLIENT = "abha-db-account-auth-methods-client";

    public static final String ABHA_DB_ACCOUNT_CLIENT = "abha-db-account-client";
    public static final String ABHA_DB_TRANSACTION_CLIENT = "abha-db-transaction-client";
    public static final String ABHA_DB_ACCOUNT_ACTION_CLIENT = "abha-db-account-action-client";
    public static final String ABHA_DB_INTEGRATED_PROGRAM_CLIENT = "abha-db-integrated-program-client";

    public static final String ABHA_DB_HID_BENEFIT_CLIENT = "abha-db-hid-benefit-client";

    public static final String ABHA_DB_DEPENDENT_ACCOUNT_RELATIONSHIP_CLIENT = "abha-db-dependent-account-relationship-client";
    public static final String DOCUMENT_DB_IDENTITY_DOCUMENT_CLIENT = "document-db-identity-document-client";
    public static final String DOCUMENT_APP_CLIENT = "document-app-client";
    public static final String ABHA_DB_HID_PHR_ADDRESS_CLIENT = "abha-db-hid-phr-address-client";
    public static final String IDP_APP_CLIENT = "idp-app-client";
    public static final String LGD_APP_CLIENT = "lgd-app-client";
    public static final String NOTIFICATION_APP_SERVICE = "notification-app-service";

    public static final String NOTIFICATION_DB_SERVICE = "notification-db-service";
    public static final String PHR_APP_CLIENT = "phr-app-client";

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
    public static final String THIS_ACCOUNT_ALREADY_EXIST_AND_DEACTIVATED = "This account is deactivated. Please continue to reactivate.";
    public static final String ACCOUNT_CREATED_SUCCESSFULLY = "Account created successfully";

    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String UTC_TIMEZONE_ID = "UTC";

    public static final String AUTHORIZATION = "Authorization";
    public static final String AADHAAR_TECHNICAL_ERROR_MSG = "Technical error that are internal to authentication server.";
    public static final String EMAIL_HIDE_REGEX = ".(?=.{4})";

    public static final String EMAIL_MASK_CHAR = "$1*";

    public static final String EMAIL_REGEX_PATTERN = "^[a-zA-Z0-9_-]+(?:\\.[a-zA-Z0-9_-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    public static final String PID_INVALID = "Invalid PID";
    public static final String INTEGRATED_PROGRAM_ROLE = "HidIntegratedProgram";
    public static final String BENEFIT_NAME = "Benefit-Name";
    public static final String F_TOKEN = "F-token";
    public static final String SUB = "sub";
    public static final String ROLES = "roles";

    public static final String OFFLINE_HID = "OFFLINE_HID";
    public static final String SCOPES="Scopes";
    public static final String AUTH_METHOD="AuthMethod";
    public static final String REASONS="reasons";

    public static final long REGISTRATION_OTP_TEMPLATE_ID = 1007164181681962323L;
    public static final long ABHA_CREATED_TEMPLATE_ID = 1007168421931217527L;
    public static final long ENROLL_CREATED_TEMPLATE_ID = 1007168421921410710L;

    public static final String DEFAULT_CLIENT_ID = "healthid-api";
    public static final String CREATION = "CREATION";
    public static final String ABHA_NO_REGEX_PATTERN = "\\d{2}-\\d{4}-\\d{4}-\\d{4}";

    public static final String ABHA_NUMBER ="abhaNumber";
    public static final String VALIDATION_ERROR_ABHA_NUMBER_FIELD = "Invalid Abha Number";

    public static final String TYPE ="type";
    public static final String VALIDATION_ERROR_TYPE_FIELD = "Invalid Type";

    public static final String NOTIFICATION_TYPE="notificationType";
    public static final String VALIDATION_ERROR_NOTIFICATION_TYPE_FIELD = "Invalid Notification Type";
    public static final String EMAIL_ACCOUNT_CREATION_SUBJECT = "account creation";



}
