package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.annotations.Mobile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * It is Data Transfer Object for Bio
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    @NotEmpty(message = AbhaConstants.AADHAAR_NUMBER_INVALID)
    @JsonProperty("aadhaar")
    private String aadhaar;

    /**
     * It is a rdPidData
     */
    @JsonProperty("fingerPrintAuthPid")
    private String fingerPrintAuthPid;

    @JsonProperty("mobile")
    @NotEmpty(message = AbhaConstants.MOBILE_NUMBER_MISSMATCH)
    @Mobile
    private String mobile;
}
