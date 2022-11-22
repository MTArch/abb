package in.gov.abdm.abha.enrollment.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer object for Transaction Entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {

	/**
	 * sequence number
	 */
	private long id;

	/**
	 * aadhaar number of abha user
	 */
	private String aadharNo;

	/**
	 * retry count with aadhaar
	 */
	private int aadharRetryCount;

	/**
	 * aadhaar transaction id
	 */
	private String aadharTxn;

	/**
	 * account type
	 */
	private String accType;

	/**
	 * address of abha user
	 */
	private String address;

	/**
	 * care of for abha user
	 */
	private String co;

	/**
	 * audit details
	 */
	private LocalDateTime createdDate;

	/**
	 * day of birth of abha user
	 */
	private String dayOfBirth;

	/**
	 * district name of abha user
	 */
	private String districtName;

	/**
	 * email of abha user
	 */
	private String email;

	/**
	 * gender of abha user
	 */
	private String gender;

	/**
	 * house no. of abha user
	 */
	private String house;

	/**
	 * kyc date of birth
	 */
	private String kycdob;

//    /**
//     * kyc photo of abha user
//     */
//    private byte[] kycPhoto;

	/**
	 * kyc reason
	 */
	private String kycReason;

	/**
	 * kyc status of abha user
	 */
	private String kycStatus;

	/**
	 * kyc verified flag for abha user
	 */
	private boolean kycVerified;

	/**
	 * landmark of abha user
	 */
	private String lm;

	/**
	 * locality of abha user
	 */
	private String loc;

	/**
	 * mobile number of abha user
	 */
	private String mobile;

	/**
	 * mobile verified flag for abha user
	 */
	private boolean mobileVerified;

	/**
	 * month of birth of abha user
	 */
	private String monthOfBirth;

	/**
	 * name of abha user
	 */
	private String name;

	/**
	 * mobile otp received by abha user for authentication
	 */
	private String otp;

	/**
	 * otp retry count for authentication
	 */
	private int otpRetryCount;

	/**
	 * pincode of abha user
	 */
	private String pincode;

	/**
	 * post office of abha user
	 */
	private String po;

	/**
	 * state name of abha user
	 */
	private String stateName;

	/**
	 * state name of abha user
	 */
	private String status;

	/**
	 * subdistrict name of abha user
	 */
	private String subDistrictName;

	/**
	 * town name of abha user
	 */
	private String townName;

	/**
	 * log in transaction details
	 */
	protected UUID txnId;

	/**
	 * type
	 */
	private String type;

	/**
	 * village name of abha user
	 */
	private String villageName;

	/**
	 * ward name of abha user
	 */
	private String wardName;

	/**
	 * xmluid of abha user
	 */
	private String xmluid;

	/**
	 * year of birth of abha user
	 */
	private String yearOfBirth;

	/**
	 * response code
	 */
	private String responseCode;

	/**
	 * code challenge
	 */
	private String codeChallenge;

	/**
	 * code challenge method
	 */
	private String codeChallengeMethod;

	/**
	 * OIDC action type
	 */
	private String oidcActionType;

	/**
	 * OIDC client id
	 */
	private String oidcClientId;

	/**
	 * OIDC redirectUrl
	 */
	private String oidcRedirectUrl;

	/**
	 * response type
	 */
	private String responseType;

	/**
	 * scope
	 */
	private String scope;

	/**
	 * state of abha id
	 */
	private String state;

	/**
	 * kyc type
	 */
	private String kycType;

	/**
	 * client ip
	 */
	private String clientIp;

	/**
	 * phr address of abha id
	 */
	private String phrAddress;

	/**
	 * email verified flag for abha user
	 */
	private boolean emailVerified;

	/**
	 * document code
	 */
	private String documentCode;

	/**
	 * document code
	 */
	private String loginModeType;

	/**
	 * transaction response - captures list of healthIdnumbers
	 */
	private String txnResponse;

}
