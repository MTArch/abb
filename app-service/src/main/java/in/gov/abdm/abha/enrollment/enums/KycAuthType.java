package in.gov.abdm.abha.enrollment.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public enum KycAuthType {
    FINGERSCAN("FINGERSCAN"), IRIS("IRIS"), OTP("OTP");

    public static List<String> getAllSupportedAuth() {
        List<String> names = new ArrayList<>();
        for (KycAuthType auth : values()) {
            names.add(auth.value);
        }
        return names;
    }

    public static List<String> getAllSupportedBioAuth() {
        List<String> names = new ArrayList<>();
        for (KycAuthType auth : values()) {
            if (auth.equals(OTP)) {
                continue;
            }
            names.add(auth.value);
        }
        return names;
    }

    public static boolean isValid(String authType) {
        KycAuthType[] values = KycAuthType.values();
        for (KycAuthType auth : values) {
            if (auth.toString().equals(authType)) {
                return true;
            }
        }
        return false;
    }

    private String value;
}