package in.gov.abdm.abha.enrollment.constants;

public interface ABHAEnrollmentConstant {
    String API_VERSION = "/v3/enrollment";
    String BASE_URI = "/api" + API_VERSION;
    String OTP_REQUEST_ENDPOINT = BASE_URI + "/request";
    String ENROL_ENDPOINT = BASE_URI + "/enrol";
    String MOBILE_OR_EMAIL_TOP_ENDPOINT = "/mobileOrEmailOtp";
    String BY_AADHAAR_ENDPOINT = "/byAadhaar";


//    DB URIS
    String DB_BASE_URI = "/api/v3";

    String DB_ADD_TRANSACTION_URI = DB_BASE_URI + "/transaction";
    String DB_GET_TRANSACTION_BY_TXN_ID = DB_BASE_URI + "/transaction/txnId/";

    String DB_ADD_ACCOUNT_URI = DB_BASE_URI + "/account";

//    Aadhaar Service URI

    String AADHAAR_BASE_URI = "/api/v3/aadhaar/";
    String AADHAAR_SEND_OTP_URI = AADHAAR_BASE_URI + "/sendOtp";
    String AADHAAR_VERIFY_OTP_URI = AADHAAR_BASE_URI + "/verifyOtp";
}
