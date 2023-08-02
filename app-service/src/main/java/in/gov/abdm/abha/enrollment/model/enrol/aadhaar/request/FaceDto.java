package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.validators.annotations.AadhaarNumberFace;
import in.gov.abdm.abha.enrollment.validators.annotations.Mobile;
import lombok.Data;

/**
 * It is Data Transfer Object for Bio
 */

@AadhaarNumberFace
@Data
public class FaceDto {

    /**
     * It is Aadhaar Number
     */
    @JsonProperty("aadhaar")
    private String aadhaar;

    /**
     * It is a rdPidData
     */
    @JsonProperty("rdPidData")
    private String rdPidData;

    @JsonProperty("mobile")
    @Mobile
    private String mobile;
}
