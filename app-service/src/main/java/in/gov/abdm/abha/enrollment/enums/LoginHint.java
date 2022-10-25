package in.gov.abdm.abha.enrollment.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * defines Enum values for LoginHint
 */
@Getter
@AllArgsConstructor
public enum LoginHint {

    ABHA_NUMBER("abha-number"),
    MOBILE("mobile"),
    PHR_ADDRESS("phr-address");

    private final String name;
}
