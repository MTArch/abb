package in.gov.abdm.abha.enrollmentdb.model.account;

import java.time.LocalDateTime;

import in.gov.abdm.abha.enrollmentdb.enums.AbhaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Account
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {

    /**
     * abha id
     * 14-digit unique number
     */
    private String healthIdNumber;

    /**
     * address of abha id
     */
    private String address;
    
    /**
     * origin of abha user
     */
    private String origin;

	/**
	 * date of creation of account
	 */
	private LocalDateTime createdDate;

	/**
	 * date of birth of abha user
	 */
	private String dayOfBirth;
	
	/**
     * district code of abha user
     */
    private String districtCode;

    /**
     * district name of abha user
     */
    private String districtName;
    
    /**
     * email id of abha user
     */
    private String email;

    /**
     * facility id of abha user
     */
    private String facilityId;
    
    /**
	 * first name of abha user
	 */
	private String firstName;
	
	/**
	 * gender of abha user
	 */
	private String gender;
	
	/**
	 * health id of abha user
	 */
	private String healthId;
	
	/**
	 * dob of abha user
	 */
	private String kycDob;
	
	/**
     * kyc verified flag for user
     */
    private boolean kycVerified;
	
	/**
     * last name of abha user
     */
    private String lastName;

    /**
     * middle name of abha user
     */
    private String middleName;

    /**
     * mobile of abha user
     */
    private String mobile;

    /**
     * month of birth of abha user
     */
    private String monthOfBirth;
    
    /**
     * user's name
     */
    private String name;
	
    /**
     * okyc verified flag for user
     */
    private boolean okycVerified;
	
	/**
     * password of abha user
     */
    private String password;

    /**
     * pincode of abha user
     */
    private String pincode;

    /**
     * state code of abha user
     */
    private String stateCode;

    /**
     * state name of abha user
     */
    private String stateName;
    
    /**
     * status of abha user
     */
	private String status;

    /**
     * sub district code of abha user
     */
    private String subDistrictCode;

    /**
     * sub district name of abha user
     */
    private String subDistrictName;

    /**
     * town code of abha user
     */
    private String townCode;

    /**
     * town name of abha user
     */
    private String townName;
	
    /**
     * audit details
     */
    private LocalDateTime updateDate;

    /**
     * village code of abha user
     */
    private String villageCode;

    /**
     * village name of abha user
     */
    private String villageName;

    /**
     * ward code of abha user
     */
    private String wardCode;

    /**
     * ward name of abha user
     */
    private String wardName;
    
    /**
     * hip id of abha user
     */
    private String hipId;

    /**
     * xmluid of abha user
     */
    private String xmlUID;

    /**
     * year of birth of abha user
     */
    private String yearOfBirth;
    
    /**
     * user's date of consent for abha creation
     */
    private LocalDateTime consentDate;
    
    /**
     * compressed profile photo of abha user
     */
    private boolean profilePhotoCompressed;

    /**
     * email verification date
     */
    private LocalDateTime emailVerificationDate;
    
    /**
     * email verified flag for abha user
     */
    private String emailVerified;
    
    /**
	 * document code
	 */
	private String documentCode;

    /**
     * verification status of account
     */
	private String verificationStatus;

    /**
     * verification type of account
     */
	private String verificationType;
	
	/**
     * audit details
     */
    private String lstUpdatedBy;

    /**
     * consent version of account
     */
	private String consentVersion;
	
	 /**
     * is cm migrated of account
     */
	private String cmMigrated;
	
	 /**
     * is phr migrated of account
     */
	private String phrMigrated;
	
	/**
     * health worker mobile
     */
    private String healthWorkerMobile;

    /**
     * health worker name
     */
    private String healthWorkerName;

	/**
	 * mobile type
	 */
	private String mobileType;
	
	/**
     * isNewTemplate of boolean type that stores the state of an entity object.
     */
	private boolean isNewAccount;

    /**
     * type of account
     */
    private AbhaType type;
    
}
