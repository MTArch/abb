package in.gov.abdm.abha.enrollment.constants;

public interface URIConstant {
    String GLOBAL_SERVICE_BASE_URI = "http://global2dev.abdm.gov.internal";//"http://localhost:9292"; //
    String ABHA_DB_BASE_URI = "http://abha2dev.abdm.gov.internal"; //"http://localhost:9188"; //

    String API_VERSION = "/v3";
    String BASE_URI = "/api" + API_VERSION + "/enrollment";

    //enrol endpoints
    String ENROL_ENDPOINT = BASE_URI + "/enrol";
    String BY_ENROL_AADHAAR_ENDPOINT = "/byAadhaar";
    String ENROL_BY_DOCUMENT_ENDPOINT = "/byDocument";
    String ENROL_SUGGEST_ABHA_ENDPOINT = "/suggestion/{txnId}";

    String ENROL_ABHA_ADDRESS_ENDPOINT = "/abha-address";

    //request endpoints
    String OTP_REQUEST_ENDPOINT = BASE_URI + "/request";
    String MOBILE_OR_EMAIL_OTP_ENDPOINT = "/otp";

    //auth endpoints
    String AUTH_ENDPOINT = BASE_URI + "/auth";
    String AUTH_BY_ABDM_ENDPOINT = "/byAbdm";
    String AUTH_BY_AADHAAR_ENDPOINT = "/byAadhaar";

    //profile endpoints
    String PROFILE_ENDPOINT = BASE_URI + "/profile";
    String LINK_PARENT_ENDPOINT = "/link/parent";

    //    DB URIS
    String DB_BASE_URI = "/api/v3/enrollmentdb";

    String DB_ADD_TRANSACTION_URI = DB_BASE_URI + "/transaction";
    String DB_UPDATE_TRANSACTION_URI = DB_BASE_URI + "/transaction/{id}";
    String DB_DELETE_TRANSACTION_URI = DB_BASE_URI + "/transaction/txnId/";
    String DB_GET_TRANSACTION_BY_TXN_ID = DB_BASE_URI + "/transaction/txnId/";
    String FDB_GET_TRANSACTION_BY_TXN_ID = DB_BASE_URI + "/transaction/txnId/{txnId}";

    String DB_GET_ACCOUNT_BY_XML_UID = DB_BASE_URI + "/account/getByXml/";
    String FDB_GET_ACCOUNT_BY_XML_UID = DB_BASE_URI + "/account/getByXml/{xmlUid}";

    String DB_ADD_ACCOUNT_URI = DB_BASE_URI + "/account";

    String DB_ADD_DEPENDENT_ACCOUNT_URI = DB_BASE_URI + "/dependentaccountrelationship";

    String DB_ADD_ACCOUNT_AUTH_METHODS_ENDPOINT = DB_BASE_URI + "/accountauthmethods";

    String DB_GET_ACCOUNT_BY_HEALTH_ID_NUMBER = DB_BASE_URI + "/account/";
    String FDB_GET_ACCOUNT_BY_HEALTH_ID_NUMBER = DB_BASE_URI + "/account/{healthIdNumber}";
    String FDB_GET_ACCOUNTS_BY_HEALTH_ID_NUMBER = DB_BASE_URI + "/account/{healthIdNumber}";

    String DB_GET_ACCOUNT_BY_DOCUMENT_CODE = DB_BASE_URI + "/account/documentCode/";
    String FDB_GET_ACCOUNT_BY_DOCUMENT_CODE = DB_BASE_URI + "/account/documentCode/{documentCode}";

    String DB_UPDATE_ACCOUNT_URI = DB_BASE_URI + "/account/{id}";

    String DB_ADD_HID_PHR_ADDRESS_URI = DB_BASE_URI + "/hidphraddress";

    String DB_GET_ACCOUNTS_BY_HEALTH_ID_NUMBER_LIST = DB_BASE_URI + "/account/";

    String DB_GET_HID_PHR_ADDRESS_BY_PHR_ADDRESS_LIST = DB_BASE_URI + "/hidphraddress/check/";

    String DB_GET_HID_PHR_ADDRESS_BY_PHR_ADDRESS = DB_BASE_URI + "/hidphraddress/byPhrAddress/";

    String DB_GET_HID_PHR_ADDRESS_BY_HEALTH_ID_NUMBER = DB_BASE_URI + "/hidphraddress/byHealthIdNumber/";
    String FDB_GET_HID_PHR_ADDRESS_BY_HEALTH_ID_NUMBER = DB_BASE_URI + "/hidphraddress/byHealthIdNumber/{healthIdNumber}";

    String DB_UPDATE_HID_PHR_ADDRESS_BY_HID_PHR_ADDRESS_ID = DB_BASE_URI + "/hidphraddress/{hidPhrAddressId}";
//    Aadhaar Service URI

    String AADHAAR_BASE_URI = "/api/v3/aadhaar";
    String AADHAAR_SEND_OTP_URI = AADHAAR_BASE_URI + "/sendOtp";
    String AADHAAR_VERIFY_OTP_URI = AADHAAR_BASE_URI + "/verifyOtp";
    String AADHAAR_VERIFY_DEMOGRAPHIC = AADHAAR_BASE_URI + "/verifyDemographic";


    // IDP Service URI
    String IDP_BASE_URI = "/internal/v3/identity";
    String IDP_SEND_OTP_URI = IDP_BASE_URI + "/authentication";
    String IDP_VERIFY_OTP_URI = IDP_BASE_URI + "/verify";

    //    Notification Service
    String NOTIFICATION_BASE_URI = "/internal/v3/notification";
    String NOTIFICATION_SEND_OTP_URI = NOTIFICATION_BASE_URI + "/message";

    //    Notification DB Service
    String NOTIFICATION_DB_BASE_URI = "/internal/v3/notification";
    String NOTIFICATION_DB_GET_ALL_TEMPLATES_URI = NOTIFICATION_DB_BASE_URI + "/template/name/ABHA";

    //LGD service
    String LGD_BASE_URI = "/internal/v3/abdm/lgd";
    String FLGD_BASE_URI = "/internal/v3/abdm/lgd/{search}";

    // Driving Licence
    String DOCUMENT_BASE_URI = "/api/v3/document";
    String DOCUMENT_DB_BASE_URI = "/api/v3/documentdb";
    String DOCUMENT_VERIFY = DOCUMENT_BASE_URI + "/verify";
    String IDENTITY_DOCUMENT_ADD = DOCUMENT_DB_BASE_URI + "/identityDocument";

    //Feign Client
    String REQUEST_ID = "REQUEST_ID";
    String TIMESTAMP = "TIMESTAMP";
    String REQUESTER_ID="REQUESTER_ID";
}
