package in.gov.abdm.abha.enrollment.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnrollmentStatus {
    ACCEPT("ACCEPT"),
    REJECT("REJECT");
    private final String value;
}
