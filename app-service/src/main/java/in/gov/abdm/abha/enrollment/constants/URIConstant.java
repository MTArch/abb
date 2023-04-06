package in.gov.abdm.abha.enrollment.constants;

public class URIConstant {    

    private URIConstant() {}

    public static final String API_VERSION = "/v3";   
    public static final String BASE_URI = "/api" + API_VERSION + "/enrollment";

    //enrol endpoints
    public static final String ENROL_ENDPOINT = BASE_URI + "/enrol";
    public static final String BY_ENROL_AADHAAR_ENDPOINT = "/byAadhaar";
    public static final String ENROL_BY_DOCUMENT_ENDPOINT = "/byDocument";
    public static final String ENROL_SUGGEST_ABHA_ENDPOINT = "/suggestion";

    public static final String ENROL_ABHA_ADDRESS_ENDPOINT = "/abha-address";

    //request endpoints
    public static final String OTP_REQUEST_ENDPOINT = BASE_URI + "/request";
    public static final String MOBILE_OR_EMAIL_OTP_ENDPOINT = "/otp";

    //auth endpoints
    public static final String AUTH_ENDPOINT = BASE_URI + "/auth";
    public static final String AUTH_BY_ABDM_ENDPOINT = "/byAbdm";
    public static final String AUTH_BY_AADHAAR_ENDPOINT = "/byAadhaar";

    //profile endpoints
    public static final String PROFILE_ENDPOINT = BASE_URI + "/profile";
    public static final String LINK_PARENT_ENDPOINT = "/link/parent";

    //    DB URIS
    public static final String DB_BASE_URI = "/api/v3/enrollmentdb";

    public static final String DB_ADD_TRANSACTION_URI = DB_BASE_URI + "/transaction";
    public static final String DB_UPDATE_TRANSACTION_URI = DB_BASE_URI + "/transaction/{id}";
    public static final String DB_DELETE_TRANSACTION_URI = DB_BASE_URI + "/transaction/txnId/";
    public static final String FDB_DELETE_TRANSACTION_URI = DB_BASE_URI + "/transaction/txnId/{txnId}";
    public static final String DB_GET_TRANSACTION_BY_TXN_ID = DB_BASE_URI + "/transaction/txnId/";
    public static final String FDB_GET_TRANSACTION_BY_TXN_ID = DB_BASE_URI + "/transaction/txnId/{txnId}";

    public static final String DB_GET_ACCOUNT_BY_XML_UID = DB_BASE_URI + "/account/getByXml/";
    public static final String FDB_GET_ACCOUNT_BY_XML_UID = DB_BASE_URI + "/account/getByXml/{xmlUid}";

    public static final String DB_ADD_ACCOUNT_URI = DB_BASE_URI + "/account";
    public static final String DB_ADD_ACCOUNT_ACTION_URI = DB_BASE_URI + "/accountaction";

    public static final String DB_ADD_DEPENDENT_ACCOUNT_URI = DB_BASE_URI + "/dependentaccountrelationship";

    public static final String DB_ADD_ACCOUNT_AUTH_METHODS_ENDPOINT = DB_BASE_URI + "/accountauthmethods";
    public static final String DB_DELETE_ACCOUNT_AUTH_METHOD_BY_HEALTH_ID = DB_BASE_URI+"/accountauthmethods/delete/{healthIdNumber}";


    public static final String DB_GET_ACCOUNT_BY_HEALTH_ID_NUMBER = DB_BASE_URI + "/account/";
    public static final String DB_GET_ACCOUNT_ACTION_BY_HEALTH_ID_NUMBER = DB_BASE_URI + "/accountaction/{id}";
    public static final String FDB_GET_ACCOUNT_BY_HEALTH_ID_NUMBER = DB_BASE_URI + "/account/{healthIdNumber}";
    public static final String FDB_GET_ACCOUNTS_BY_HEALTH_ID_NUMBER = DB_BASE_URI + "/account/{healthIdNumber}";

    public static final String DB_GET_ACCOUNT_BY_DOCUMENT_CODE = DB_BASE_URI + "/account/documentCode/";
    public static final String FDB_GET_ACCOUNT_BY_DOCUMENT_CODE = DB_BASE_URI + "/account/documentCode/{documentCode}";
    public static final String GET_LINKED_ACCOUNT_COUNT_BY_MOBILE_NUMBER = DB_BASE_URI + "/account/linkedAccountCount/{mobileNumber}";
    public static final String GET_LINKED_ACCOUNT_COUNT_BY_EMAIL = DB_BASE_URI + "/account/linkedAccountCount/email/{email}";

    public static final String DB_UPDATE_ACCOUNT_URI = DB_BASE_URI + "/account/{id}";
    public static final String FDB_GET_DUPLICATE_ACCOUNT = DB_BASE_URI + "/account/check/de-duplicate";

    public static final String DB_ADD_HID_PHR_ADDRESS_URI = DB_BASE_URI + "/hidphraddress";
x
    public static final String DB_GET_ACCOUNTS_BY_HEALTH_ID_NUMBER_LIST = DB_BASE_URI + "/account/";

    public static final String DB_GET_HID_PHR_ADDRESS_BY_PHR_ADDRESS_LIST = DB_BASE_URI + "/hidphraddress/check/";

    public static final String DB_GET_HID_PHR_ADDRESS_BY_PHR_ADDRESS = DB_BASE_URI + "/hidphraddress/byPhrAddress/{phrAddress}";

    public static final String DB_GET_HID_PHR_ADDRESS_BY_HEALTH_ID_NUMBER = DB_BASE_URI + "/hidphraddress/byHealthIdNumber/";
    public static final String FDB_GET_HID_PHR_ADDRESS_BY_HEALTH_ID_NUMBER = DB_BASE_URI + "/hidphraddress/byHealthIdNumber/{healthIdNumber}";

    public static final String DB_UPDATE_HID_PHR_ADDRESS_BY_HID_PHR_ADDRESS_ID = DB_BASE_URI + "/hidphraddress/{hidPhrAddressId}";
//    Aadhaar Service URI

    public static final String AADHAAR_BASE_URI = "/api/v3/aadhaar";
    public static final String AADHAAR_SEND_OTP_URI = AADHAAR_BASE_URI + "/sendOtp";
    public static final String AADHAAR_VERIFY_OTP_URI = AADHAAR_BASE_URI + "/verifyOtp";
    public static final String AADHAAR_VERIFY_DEMOGRAPHIC = AADHAAR_BASE_URI + "/verifyDemographic";


    // IDP Service URI
    public static final String IDP_BASE_URI = "/internal/v3/identity";
    public static final String IDP_SEND_OTP_URI = IDP_BASE_URI + "/authentication";
    public static final String IDP_VERIFY_OTP_URI = IDP_BASE_URI + "/verify";

    //    Notification Service
    public static final String NOTIFICATION_BASE_URI = "/internal/v3/notification";
    public static final String NOTIFICATION_SEND_OTP_URI = NOTIFICATION_BASE_URI + "/message";

    //    Notification DB Service
    public static final String NOTIFICATION_DB_BASE_URI = "/internal/v3/notification";
    public static final String NOTIFICATION_DB_GET_ALL_TEMPLATES_URI = NOTIFICATION_DB_BASE_URI + "/template/name/ABHA";

    //LGD service
    public static final String LGD_BASE_URI = "/internal/v3/abdm/lgd";
    public static final String FLGD_BASE_URI = "/internal/v3/abdm/lgd/search";
    public static final String FLGD_STATE_SEARCH_URI = "/internal/v3/abdm/lgd/state/search";

    // Driving Licence
    public static final String DOCUMENT_BASE_URI = "/api/v3/document";
    public static final String DOCUMENT_DB_BASE_URI = "/api/v3/documentdb";
    public static final String DOCUMENT_VERIFY = DOCUMENT_BASE_URI + "/verify";
    public static final String IDENTITY_DOCUMENT_ADD = DOCUMENT_DB_BASE_URI + "/identityDocument";
    public static final String IDENTITY_DOCUMENT_GET = DOCUMENT_DB_BASE_URI + "/identityDocument/{healthId}";

    public static final String FACILITY_ENDPOINT = BASE_URI + "/facility";
    public static final String FACILITY_OTP_ENDPOINT = "/request/otp";
    public static final String VERIFY_FACILITY_OTP_ENDPOINT = "/auth/byAbdm";
    public static final String VERIFY_ENROLLMENT_ENDPOINT = "/byEnrollmentNumber";

    public static final String FACILITY_PROFILE_DETAILS_BY_ENROLLMENT_NUMBER_ENDPOINT = "/{enrollmentNumber}";
    //Feign Client
    public static final String REQUEST_ID = "REQUEST_ID";
    public static final String TIMESTAMP = "TIMESTAMP";
    public static final String REQUESTER_ID = "REQUESTER_ID";
    public static final String AADHAAR_VERIFY_FACE = AADHAAR_BASE_URI + "/verifyFace";
    public static final String ENROL_ABHA_RD_PID = "/pid";

    public static final String AADHAAR_VERIFY_BIO = AADHAAR_BASE_URI + "/verifyBio";
}
