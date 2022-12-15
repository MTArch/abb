package in.gov.abdm.abha.enrollmentdb.model.account;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.FIELD_BLANK_ERROR_MSG;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import in.gov.abdm.abha.enrollmentdb.enums.AbhaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Account Entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Accounts implements Persistable<String>{

    /**
     * abha id
     * 14-digit unique number
     */
	@Id
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
	@CreatedDate
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
	private String kycdob;
	
//	/**
//	 * kyc photo of abha user
//	 */
//	@Basic(fetch = LAZY)
//	@Lob
//	@Type(type = "org.hibernate.type.BinaryType")
//	private byte[] kycPhoto;

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

//    /**
//     * profile photo of abha user
//     */
//    @Basic(fetch = LAZY)
//	@Lob
//	@Column(name = "profile_photo")
//    private byte[] profilePhoto;

    /**
     * state code of abha user
     */
    private String stateCode;

    /**
     * state name of abha user
     */
    private String stateName;
    
    // TODDO : ACTIVE("ACTIVE"), BLOCKED("BLOCKED"), LOCKED("LOCKED"), 
    // CONSENTPENDING("CONSENTPENDING"), DEACTIVATED("DEACTIVATED"), DELETED("DELETED"), DELINKED("DELINKED");
	private String status;

    /**
     * sub district code of abha user
     */
    private String subDistrictCode;

    /**
     * sub district name of abha user
     */
    private String subdistrictName;

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
    @LastModifiedDate
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
    private String xmluid;

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
     * type of account
     */

    @NotNull(message = FIELD_BLANK_ERROR_MSG)
    private AbhaType type;
    
    /**
     * isNewTemplate of boolean type that stores the state of an entity object.
     */
	@Transient
	private boolean isNewAccount;

	/**
	 * When R2DBC repository executes the save method and isNew() method checks
	 * whether the entity new or not, if true it fires persist call. isNew() method
	 * to return true based your model has a field annotated with ID and if that ID
	 * field is null then the record can classified as new.
	 */
	@Override
	@Transient
	public boolean isNew() {
		return this.isNewAccount || healthIdNumber == null;
	}

	/**
	 * setAsNew() method sets an entity as a new record.
	 */
	public Accounts setAsNew() {
		isNewAccount = true;
		return this;
	}

	@Override
	public String getId() {
		return this.getHealthIdNumber();
	}

}
