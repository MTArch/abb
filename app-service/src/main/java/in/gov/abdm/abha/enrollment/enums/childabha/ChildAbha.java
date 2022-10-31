package in.gov.abdm.abha.enrollment.enums.childabha;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum ChildAbha {

    CHILD("child"),
    DEPENDENT("dependent"),
    PHYSICALLY_CHALLENGED("physically_challenged");
    private final String value;

    public static boolean isValid(String value) {
        ChildAbha[] values = ChildAbha.values();
        for (ChildAbha childAbha : values) {
            if (childAbha.toString().equals(value)) {
                return true;
            }
        }
        return false;
    }



}
