package in.gov.abdm.abha.enrollmentdb.constant;

public class ABHAEnrollmentDBConstant {

	private ABHAEnrollmentDBConstant() {
	}

	public static final String API_VERSION = "/api/v3/enrollmentdb";
	public static final String ACCOUNT_ENDPOINT = API_VERSION + "/account";
	public static final String ACCOUNT_ACTION_ENDPOINT = API_VERSION + "/accountaction";
	public static final String INTEGRATED_PROGRAM_ENDPOINT = API_VERSION + "/integratedProgram";

	public static final String HID_BENEFIT_ENDPOINT = API_VERSION + "/hidBenefit";
	public static final String TRANSACTION_ENDPOINT = API_VERSION + "/transaction";
	public static final String GET_ACCOUNT_BY_HEALTH_ID_NUMBER = "/{healthIdNumber}";
	public static final String ACCOUNT_REATTEMPT_ENDPOINT = "/reAttempt";
	public static final String UPDATE_ACCOUNT_BY_HEALTH_ID_NUMBER = "/{healthIdNumber}";
	public static final String GET_TRANSACTION_BY_TXN_ID = "/txnId/{txnId}";
	public static final String DELETE_TRANSACTION_BY_TXN_ID = "/txnId/{txnId}";
	public static final String DELETE_ACCOUNT_AUTH_METHOD_BY_HEALTH_ID = "/delete/{healthIdNumber}";
	public static final String HID_CHECK = "/check";
	public static final String ID = "/{id}";
	public static final String HID_PHR_ADDRESS_ID = "/{hidPhrAddressId}";
	public static final String UPDATE_TRANSACTION_BY_ID = ID;
	public static final String ENROLLMENT_LOG_PREFIX = "ENROLLMENT_DB: ";
	public static final String FIELD_BLANK_ERROR_MSG = "Please enter a Valid value for the specified field. "
			+ "Valid Format Reference:"
			+ " 1. The specified field is mandatory (not null), 2. The specified field shouldn't be Blank, 3. The specified field shouldn't be Empty.";

	public static final String DEPENDENT_ACCOUNT_RELATIONSHIP_ENDPOINT = API_VERSION + "/dependentaccountrelationship";

	public static final String UPDATE_DEPENDENT_ACCOUNT_RELATIONSHIP_BY_ID = ID;

	public static final String DELETE_DEPENDENT_ACCOUNT_RELATIONSHIP_BY_ID = ID;

	public static final String GET_DEPENDENT_ACCOUNT_RELATIONSHIP_BY_ID = ID;

	public static final String HID_PHR_ADDRESS_ENDPOINT = API_VERSION + "/hidphraddress";

	public static final String GET_HID_PHR_ADDRESS_BY_ID = HID_PHR_ADDRESS_ID;
	public static final String GET_HID_PHR_ADDRESS_BY_PHR_ADDRESS = "/byPhrAddress/{phrAddress}";

	public static final String DELETE_HID_PHR_ADDRESS_BY_ID = HID_PHR_ADDRESS_ID;

	public static final String GET_HID_PHR_ADDRESS_BY_HEALTH_ID_NUMBER = "/byHealthIdNumber/{healthIdNumber}";

	public static final String UPDATE_HID_PHR_ADDRESS_BY_ID = HID_PHR_ADDRESS_ID;

	public static final String GET_ACCOUNT_BY_XML_UID = "/getByXml/{xmluid}";

	public static final String GET_INTEGRATED_PROGRAM_BY_BENEFIT_NAME = "/{benefitName}";

	public static final String GET_ACCOUNT_BY_DOCUMENT_CODE = "/documentCode/{documentCode}";
	public static final String GET_ACCOUNTS_BY_DOCUMENT_CODE = "/child/documentCode/{documentCode}";
	public static final String GET_ACCOUNTS_BY_DOCUMENT_CODE_ENROL = "/enrol/child/documentCode/{documentCode}";
	public static final String GET_LINKED_ACCOUNT_COUNT_BY_MOBILE_NUMBER = "/linkedAccountCount/{mobileNumber}";
	public static final String GET_LINKED_ACCOUNT_COUNT_BY_EMAIL = "/linkedAccountCount/email/{email}";
	public static final String DB_GET_DUPLICATE_ACCOUNT = "/check/de-duplicate";
	public static final String ACCOUNT_AUTH_METHODS_ENDPOINT = API_VERSION + "/accountauthmethods";
	public static final String KAFKA_SERVER = "${spring.kafka.bootstrap-servers}";
	public static final String UNDERSCORE_NEW = "_NEW";
	public static final String PATIENT_SYNC_ACKNOWLEDGEMENT_UPDATE_QUERY = "UPDATE sync_acknowledgement SET synced_with_patient = :isSyncedWithPatient, updated_date = :updatedDate WHERE request_id = :requestId and health_id_number = :healthIdNumber";
	public static final String PHR_SYNC_ACKNOWLEDGEMENT_UPDATE_QUERY = "UPDATE sync_acknowledgement SET synced_with_phr = :isSyncedWithPhr, updated_date = :updatedDate WHERE request_id = :requestId and health_id_number = :healthIdNumber";
	public static final String MSG_ABHA_CONSUME_ACKNOWLEDGEMENT_FROM_HIECM = "Received sync acknowledgement from HIECM with the request ID: ";
	public static final String MSG_ABHA_PUBLISH_PATIENT_TO_HIECM = "Published event to be consumed by HIECM system to add a new patient.";

	public static final String MSG_ABHA_PUBLISH_TO_DASHBOARD = "Published event to be consumed by bashboard db.";

	public static final String MSG_ABHA_CONSUME_ACKNOWLEDGEMENT_FROM_PHR = "Received sync acknowledgement from PHR with the request ID: ";
	public static final String MSG_ABHA_CONSUME_ACKNOWLEDGEMENT_SUCCESS_FROM_PHR = "Updated the status of synchronization at ABHA end for the user with PHR/ABHA address: ";
	public static final String MSG_ABHA_PUBLISH_USER_TO_PHR = "User about to be published from ABHA : {} with request id: {}";
	public static final String MSG_ABHA_PUBLISH_USER_SUCCESS_TO_PHR = "User published to PHR successfully from ABHA";
	public static final String MSG_SYNC_ACKNOWLEDGMENT_ADDED_ABHA = "Acknowledgment object added at ABHA system with request id: ";
	public static final String ABHA_SYNC = "ABHA_SYNC";

	public static final String VERIFIED = "VERIFIED";
	public static final String PENDING = "PENDING";
	public static final String SYSTEM = "SYSTEM";
	public static final String KAFKA_ERROR_LOG_MSG = "Error while publishing kafka event: {}";

	public static final String ENROLLMENT_DB_LOG_MSG = "ABHA_ENROLLMENT_DB: About to Process the request to ";

	public static final String ENROLLMENT_DB_ACCOUNT_ACTION = " for account action .";
	public static final String ENROLLMENT_DB_AUTH_METHODS = " for auth methods .";
	public static final String ENROLLMENT_DB_ACCOUNTS = " for accounts .";
	public static final String ENROLLMENT_DB_DEPENDENT_ACCOUNT_RELATIONSHIP = " for dependent account relationship .";
	public static final String ENROLLMENT_DB_HID_BENEFIT = " for hid benefit .";
	public static final String ENROLLMENT_DB_HID_PHR_ADDRESS = " for hid phr address .";
	public static final String ENROLLMENT_DB_INTEGRATED_PROGRAM = " for integrated program .";
	public static final String ENROLLMENT_DB_PROCEDURE_CALL = " for procedure call .";
	public static final String PROVISIONAL = "PROVISIONAL";
	public static final String AADHAAR = "AADHAAR";
	public static final String DL = "DL";
	public static final String GET_ACCOUNT_BY_HEALTH_ID_NUMBER_AND_ABHA_ADDRESS = "/{healthIdNumber}/{abhaAddress}";
	public static final String GET_ACCOUNT_BY_ABHA_ADDRESS = "/{abhaAddress}";
	public static final String GET_ACCOUNT_WITHOUT_STATUS_BY_ABHA_ADDRESS = "/getUserByAbhaAddressWithoutStatus/{abhaAddress}";
	public static final String GET_ACCOUNT_BY_ABHA_ADDRESS_LIST = "/getUserByAbhaAddressList";
	public static final String IDP_ENDPOINT = API_VERSION + "/idp";

}
