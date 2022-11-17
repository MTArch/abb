package in.gov.abdm.abha.enrollment.model.aadhaar.otp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * dto for user ekyc details
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AadhaarUserKycDto {

    private String userEkycId;

    private String healthIdNumber;

    private String healthId;

    private String photo;

    private String gender;

    private String name;

    private String email;

    private String phone;

    private String pincode;

    private String birthdate;

    private String careOf;

    private String house;

    private String street;

    private String landmark;

    private String locality;

    private String villageTownCity;

    private String subDist;

    private String district;

    private String state;

    private String postOffice;

    private String signature;

    private String aadhaar;

    private String uidiaTxn;

    private String errorCode;

    private String reason;

    private String status;

    private String responseCode;

    private String actionCode;

//    private DOAuthOTP auth;

    private String migrated;

    private String txnId;

    private String address;
}