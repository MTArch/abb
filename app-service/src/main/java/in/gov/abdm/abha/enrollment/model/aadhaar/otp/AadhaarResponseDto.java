package in.gov.abdm.abha.enrollment.model.aadhaar.otp;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.constants.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * response dto from aadhaar global service of send otp request
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AadhaarResponseDto {

    /**
     * reason for failure
     */
    private String reason;
    /**
     * status of request of otp using aadhaar service
     */
    private String status;
    /**
     * response error code
     */
    private String responseCode;
    /**
     * error code response
     */
    private String errorCode;
    /**
     * device type
     */
    private String deviceType;
    /**
     * response from uidai
     */
    private String response;
    /**
     * request xml shared with uidai to send otp
     */
    private String requestXml;
    /**
     * error code
     */
    private String code;
    /**
     * DO auth otp object
     */
    @JsonProperty("DOAuthOTP")
    private AadhaarAuthOtpDto aadhaarAuthOtpDto;
    /**
     * user kyc details object
     */
    @JsonProperty("UserKycData")
    private AadhaarUserKycDto aadhaarUserKycDto;

    /**
     * checking is operation is successfull for not
     *
     * @return
     */
    public boolean isSuccessful() {
        return status.equalsIgnoreCase(StringConstants.SUCCESS) || status.equalsIgnoreCase(StringConstants.Y);
    }

    /**
     * internal error code finder
     *
     * @return
     */
    public String getErrorCodeInternal() {
        return errorCode != null ? errorCode.replace(StringConstants.DASH, StringConstants.EMPTY) : StringConstants.EMPTY;
    }
}
