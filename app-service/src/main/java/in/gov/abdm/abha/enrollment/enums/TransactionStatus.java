package in.gov.abdm.abha.enrollment.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * transaction entity status
 * when user try to create new account
 * then while creating account we need to generate transaction details
 */
@AllArgsConstructor
@Getter
public enum TransactionStatus {
    ACTIVE("ACTIVE"), EXPIRED("EXPIRED"), COMPLETED("COMPLETED"), EMAIL_UPDATE("EMAIL_UPDATE"), EMAIL_GENERATE("EMAIL_GENERATE"), EMAIL_REGENERATE("EMAIL_REGENERATE");

    private final String name;

    /**
     * to check status is valid enum or not
     * if it is available in enum list return true else false
     * @param status
     * @return
     */
    public static boolean isValid(String status) {
        TransactionStatus[] values = TransactionStatus.values();
        for (TransactionStatus auth : values) {
            if (auth.toString().equals(status)) {
                return true;
            }
        }
        return false;
    }
}
