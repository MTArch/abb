package in.gov.abdm.abha.enrollment.constants;

public interface ABHAEnrollmentConstant {
    String API_VERSION = "/v3";
    String BASE_URI = "/api" + API_VERSION;
    String OTP_REQUEST_ENDPOINT = BASE_URI + "/enrollment/request";
    String ENROL_ENDPOINT = BASE_URI;
    String MOBILE_OR_EMAIL_TOP_ENDPOINT = "/mobileOrEmailOtp";
    String BY_AADHAAR_ENDPOINT = "/byAadhaar";
    String AUTH_BYABDM_BASE_URI = "abha/v3";

    String AUTH_BYABDM = "/auth/byAbdm";
    String BY_ENROL_AADHAAR_ENDPOINT = "/enrollment/enrol/byAadhaar";
    String BY_AUTH_AADHAAR_ENDPOINT = "/auth/byAadhaar";

    String LINK_PARENT_REQUEST_ENDPOINT = BASE_URI + "/profile";

    String PROFILE_LINK_PARENT_ENDPOINT = "/link/parent";

//    DB URIS
    String DB_BASE_URI = "/api/v3";

    String DB_ADD_TRANSACTION_URI = DB_BASE_URI + "/transaction";
    String DB_GET_TRANSACTION_BY_TXN_ID = DB_BASE_URI + "/transaction/txnId/";

    String DB_GET_ACCOUNT_BY_XML_UID = DB_BASE_URI + "/account/getByXml/";

    String DB_ADD_ACCOUNT_URI = DB_BASE_URI + "/account";

    String DB_ADD_DEPENDENT_ACCOUNT_URI = DB_BASE_URI + "/dependentaccountrelationship";

//    Aadhaar Service URI

    String AADHAAR_BASE_URI = "/api/v3/aadhaar/";
    String AADHAAR_SEND_OTP_URI = AADHAAR_BASE_URI + "/sendOtp";
    String AADHAAR_VERIFY_OTP_URI = AADHAAR_BASE_URI + "/verifyOtp";


    // IDP Service URI
    String IDP_BASE_URI = "/api/v3/identity";

    String IDP_VERIFY_OTP_URI  = IDP_BASE_URI+"/verify";
    String IDP_SEND_OTP_URI = IDP_BASE_URI + "/authentication";
}
