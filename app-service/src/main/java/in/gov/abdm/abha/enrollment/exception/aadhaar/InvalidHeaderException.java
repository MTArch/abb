package in.gov.abdm.abha.enrollment.exception.aadhaar;

import lombok.Data;

/**
 * handler to invalid header for UIDAI
 */
@Data
public class InvalidHeaderException extends Exception {
    private String header="";
    public InvalidHeaderException(String header,String message) {
        super(message);
        this.header=header;
    }
}
