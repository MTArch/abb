package in.gov.abdm.abha.enrollment.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountAuthMethods {
    AADHAAR_OTP("AADHAAR_OTP"), MOBILE_OTP("MOBILE_OTP"),
    PASSWORD("PASSWORD"), DEMOGRAPHICS("DEMOGRAPHICS"),
    AADHAAR_BIO("AADHAAR_BIO"), EMAIL_OTP("EMAIL_OTP");
    private final String value;
}
