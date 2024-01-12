package in.gov.abdm.abha.enrollment.enums.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum AadhaarLogType {
    KYC_GEN_OTP("KYC_GEN_OTP"), // KYC-OTP Generation
    KYC_OTP("KYC_OTP"),// KYC-OTP Verify
    KYC_F("KYC_F"),// KYC-FingerNew
    KYC_P("KYC_P"),// KYC-Photo(Face)
    KYC_I("KYC_I"), // KYC-Iris
    KYC_D_AUTH("KYC_D_AUTH"), // KYC_Auth-demographic

    AUTH_GEN_OTP("AUTH_GEN_OTP"),// Auth-OTP Generation
    AUTH_OTP("AUTH_OTP"),// Auth-OTP Verify
    AUTH_F("AUTH_F"),// Auth-FingerNew
    AUTH_P("AUTH_P"),// AUTH-Photo(Face)
    AUTH_I("AUTH_I"),// Auth_Iris
    D_AUTH("D_AUTH");// Auth-demographic

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
