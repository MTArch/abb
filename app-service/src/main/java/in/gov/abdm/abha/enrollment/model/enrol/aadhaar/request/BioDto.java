package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * It is Data Transfer Object for Bio
 */
public class BioDto {
    /**
     *  It is for date
     */

    //@NotBlank(message = FIELD_BLANK_ERROR_MSG)
    @JsonProperty("timeStamp")
    private Date timestamp;
    /**
     * It is Aadhaar Number
     */
    //@NotBlank(message = FIELD_BLANK_ERROR_MSG)
    @JsonProperty("aadhaar")
    private String aadhaar;
    /**
     * It is a rdPidData
     */
    //@NotBlank(message = FIELD_BLANK_ERROR_MSG)
    @JsonProperty("rdPidData")
    private String rdPidData;
}
