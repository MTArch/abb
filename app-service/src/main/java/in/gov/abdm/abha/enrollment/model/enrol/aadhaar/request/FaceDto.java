package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.validators.annotations.AadhaarNumberFace;
import in.gov.abdm.abha.enrollment.validators.annotations.TimestampBio;
import lombok.Data;

/**
 * It is Data Transfer Object for Bio
 */
@TimestampBio
@AadhaarNumberFace
@Data
public class FaceDto {
    /**
     *  It is for date
     */
    @JsonProperty("timeStamp")
    private String timestamp;

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
}
