package in.gov.abdm.abha.enrollmentdb.constant;

public interface ABHAEnrollmentDBConstant {
    String API_VERSION = "/api/v3/enrollmentdb";
    String ACCOUNT_ENDPOINT = API_VERSION + "/account";
    String TRANSACTION_ENDPOINT = API_VERSION + "/transaction";
    String GET_ACCOUNT_BY_HEALTH_ID_NUMBER = "/{healthIdNumber}";
    String UPDATE_ACCOUNT_BY_HEALTH_ID_NUMBER = "/{healthIdNumber}";
    String GET_TRANSACTION_BY_TXN_ID = "/txnId/{txnId}";
    String DELETE_TRANSACTION_BY_TXN_ID = "/txnId/{txnId}";
    String UPDATE_TRANSACTION_BY_ID = "/{id}";
    String ENROLLMENT_LOG_PREFIX = "ENROLLMENT_DB: ";
    String FIELD_BLANK_ERROR_MSG = "Please enter a Valid value for the specified field. " + "Valid Format Reference:" +
            " 1. The specified field is mandatory (not null), 2. The specified field shouldn't be Blank, 3. The specified field shouldn't be Empty.";

    String DEPENDENT_ACCOUNT_RELATIONSHIP_ENDPOINT = API_VERSION + "/dependentaccountrelationship";

    String UPDATE_DEPENDENT_ACCOUNT_RELATIONSHIP_BY_ID = "/{id}";

    String DELETE_DEPENDENT_ACCOUNT_RELATIONSHIP_BY_ID = "/{id}";

    String GET_DEPENDENT_ACCOUNT_RELATIONSHIP_BY_ID = "/{id}";

    String HID_PHR_ADDRESS_ENDPOINT = API_VERSION + "/hidphraddress";

    String GET_HID_PHR_ADDRESS_BY_ID = "/{hidPhrAddressId}";

    String DELETE_HID_PHR_ADDRESS_BY_ID = "/{hidPhrAddressId}";

    String UPDATE_HID_PHR_ADDRESS_BY_ID = "/{hidPhrAddressId}";
    
    String GET_ACCOUNT_BY_XML_UID = "/getByXml/{xmluid}";

    String GET_ACCOUNT_BY_DOCUMENT_CODE = "/documentCode/{documentCode}";

    String ACCOUNT_AUTH_METHODS_ENDPOINT = API_VERSION + "/accountauthmethods";
}
