package in.gov.abdm.abha.enrollmentdb.constant;

public interface ABHAEnrollmentDBConstant {
    String API_VERSION ="/api/v3";
    String ACCOUNT_ENDPOINT = API_VERSION+"/account";
    String TRANSACTION_ENDPOINT = API_VERSION+"/transaction";
    String GET_ACCOUNT_BY_HEALTH_ID_NUMBER = "/{healthIdNumber}";
    String UPDATE_ACCOUNT_BY_HEALTH_ID_NUMBER = "/{healthIdNumber}";
    String GET_TRANSACTION_BY_TXN_ID = "/txnId/{txnId}";
    String UPDATE_TRANSACTION_BY_TXN_ID = "/{id}";
    String ENROLLMENT_LOG_PREFIX = "ENROLLMENT_DB: ";
}
