package in.gov.abdm.abha.enrollment.exception.aadhaar;

import lombok.Data;

/**
 * exception handler for missing header error
 */
@Data
public class MissingHeaderException extends Exception {
    /**
     * constant for header value
     */
    private String header="";

    /**
     * throwing handler error message
     * @param header
     * @param message
     */
    public MissingHeaderException(String header,String message) {
        super(message);
        this.header=header;
    }
}
