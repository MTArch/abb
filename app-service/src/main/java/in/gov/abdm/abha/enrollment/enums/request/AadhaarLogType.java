package in.gov.abdm.abha.enrollment.enums.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum AadhaarLogType {
    /**
     * KYC-OTP Generation
     */
    KYC_GEN_OTP("KYC_GEN_OTP"),
    /**
     * KYC-OTP Verify
     */
    KYC_OTP("KYC_OTP"),
    /**
     * KYC-FingerNew
     */
    KYC_F("KYC_F"),
    /**
     * KYC-Photo(Face)
     */
    KYC_P("KYC_P"),
    /**
     * KYC-Iris
     */
    KYC_I("KYC_I"),
    /**
     * KYC_Auth-demographic
     */
    KYC_D_AUTH("KYC_D_AUTH"),
    /**
     * Auth-OTP Generation
     */
    AUTH_GEN_OTP("AUTH_GEN_OTP"),
    /**
     * Auth-OTP Verify
     */
    AUTH_OTP("AUTH_OTP"),
    /**
     * Auth-FingerNew
     */
    AUTH_F("AUTH_F"),
    /**
     * AUTH-Photo(Face)
     */
    AUTH_P("AUTH_P"),
    /**
     * Auth_Iris
     */
    AUTH_I("AUTH_I"),
    /**
     * Auth-demographic
     */
    AUTH_D("AUTH_D");

    private String value;

    public static boolean isValid(String value) {
        AadhaarLogType[] values = AadhaarLogType.values();
        for (AadhaarLogType authType : values) {
            if (authType.toString().equals(value)) {
                return true;
            }
        }
        return false;
    }


}
