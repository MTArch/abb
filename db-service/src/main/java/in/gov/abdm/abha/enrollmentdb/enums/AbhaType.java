package in.gov.abdm.abha.enrollmentdb.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum AbhaType {

    CHILD("child"),
    DEPENDENT("dependent"),
    STANDARD("standard"),
    PHYSICALLY_CHALLENGED("physically_challenged");
    private final String value;

    public static boolean isValid(String value) {
        AbhaType[] values = AbhaType.values();
        for (AbhaType childAbha : values) {
            if (childAbha.toString().equals(value)) {
                return true;
            }
        }
        return false;
    }



}
