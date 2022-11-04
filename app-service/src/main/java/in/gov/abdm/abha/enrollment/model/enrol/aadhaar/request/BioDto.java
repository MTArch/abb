package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.validators.annotations.AadhaarNumberBio;
import in.gov.abdm.abha.enrollment.validators.annotations.TimestampBio;
import lombok.Data;

/**
 * It is Data Transfer Object for Bio
 */
@TimestampBio
@AadhaarNumberBio
@Data
public class BioDto {
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
