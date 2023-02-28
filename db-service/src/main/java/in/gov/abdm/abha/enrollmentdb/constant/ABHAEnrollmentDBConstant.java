package in.gov.abdm.abha.enrollmentdb.constant;

public interface ABHAEnrollmentDBConstant {
    String API_VERSION = "/api/v3/enrollmentdb";
    String ACCOUNT_ENDPOINT = API_VERSION + "/account";
    String ACCOUNT_ACTION_ENDPOINT = API_VERSION + "/accountaction";
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
    String GET_HID_PHR_ADDRESS_BY_PHR_ADDRESS = "/byPhrAddress/{phrAddress}";

    String DELETE_HID_PHR_ADDRESS_BY_ID = "/{hidPhrAddressId}";

    String GET_HID_PHR_ADDRESS_BY_HEALTH_ID_NUMBER = "/byHealthIdNumber/{healthIdNumber}";

    String UPDATE_HID_PHR_ADDRESS_BY_ID = "/{hidPhrAddressId}";

    String UPDATE_PREFERRED_FLAG_BY_HEALTH_ID_NUMBER = "/update/{healthIdNumber}";

    String GET_ACCOUNT_BY_XML_UID = "/getByXml/{xmluid}";

    String GET_ACCOUNT_BY_DOCUMENT_CODE = "/documentCode/{documentCode}";
    String GET_LINKED_ACCOUNT_COUNT_BY_MOBILE_NUMBER = "/linkedAccountCount/{mobileNumber}";

    String ACCOUNT_AUTH_METHODS_ENDPOINT = API_VERSION + "/accountauthmethods";
    public static final String KAFKA_SERVER = "${spring.kafka.bootstrap-servers}";
    public static final String REQUEST_DETAILS = " HEALTH ID : ";
    public static final String TIMESTAMP = " TIMESTAMP : ";
    public static final String UNDERSCORE_NEW = "_NEW";
    public static final String PATIENT_SYNC_ACKNOWLEDGEMENT_UPDATE_QUERY = "UPDATE sync_acknowledgement SET synced_with_patient = :isSyncedWithPatient, updated_date = :updatedDate WHERE request_id = :requestId and health_id_number = :healthIdNumber";
    public static final String PHR_SYNC_ACKNOWLEDGEMENT_UPDATE_QUERY = "UPDATE sync_acknowledgement SET synced_with_phr = :isSyncedWithPhr, updated_date = :updatedDate WHERE request_id = :requestId and health_id_number = :healthIdNumber";
    public static final String MSG_ABHA_CONSUME_ACKNOWLEDGEMENT_FROM_HIECM = "Received sync acknowledgement from HIECM with the request ID: ";
    public static final String MSG_ABHA_CONSUME_ACKNOWLEDGEMENT_SUCCESS_FROM_HIECM = "Updated the status of synchronization at HIECM end for the patient with PHR/ABHA address: ";
    public static final String MSG_ABHA_PUBLISH_PATIENT_TO_HIECM = "Published event to be consumed by HIECM system to add a new patient.";
    public static final String MSG_ABHA_CONSUME_ACKNOWLEDGEMENT_FROM_PHR = "Received sync acknowledgement from PHR with the request ID: ";
    public static final String MSG_ABHA_CONSUME_ACKNOWLEDGEMENT_SUCCESS_FROM_PHR = "Updated the status of synchronization at ABHA end for the user with PHR/ABHA address: ";
    public static final String MSG_ABHA_PUBLISH_USER_TO_PHR = "User about to be published from ABHA";
    public static final String MSG_ABHA_PUBLISH_USER_SUCCESS_TO_PHR = "User published to PHR successfully from ABHA";
    public static final String MSG_SYNC_ACKNOWLEDGMENT_ADDED_ABHA = "Acknowledgment object added at ABHA system with request id: ";
}
