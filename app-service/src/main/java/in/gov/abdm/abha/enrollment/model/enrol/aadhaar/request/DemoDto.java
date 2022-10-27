package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * It is Data Transfer Object for Demo
 */
public class DemoDto {

    /**
     * It is a Date
     */
   // @NotNull(message = FIELD_BLANK_ERROR_MSG)
    @JsonProperty("timeStamp")
    private String timestamp;
    /**
     * It is a Aadhaar Number
     */
   // @Pattern(regexp = "^(\\d{12}|\\d{16})*$", message = PATTREN_MISMATCHED)

    //@AadhaarNumber(message = AADHAAR_NUMBER_INVALID, optional = true, encrypted = true)
    //@NotNull(message = FIELD_BLANK_ERROR_MSG)
    @JsonProperty("aadhaar")
    private String aadhaar;
    /**
     * It is a Persons Name
     */
    //@Name
    //@NotNull(message = FIELD_BLANK_ERROR_MSG)
    @JsonProperty("name")
    private String name;
    /**
     * It is a nameMatchStrategy
     */
    //@NotNull(message = FIELD_BLANK_ERROR_MSG)
    @JsonProperty("nameMatchStrategy")
    private String nameMatchStrategy;
    /**
     * It is gender Male/Female
     */
    //@ApiModelProperty(example = "M",dataType="String",name="gender",allowableValues = "M,F,O,U")
    //@NotNull(message = FIELD_BLANK_ERROR_MSG)
    @JsonProperty("gender")
    private String gender;
    /**
     * It is year of birth
     */
//    @YOB
//    @NotNull(message = FIELD_BLANK_ERROR_MSG)
    @JsonProperty("yob")
    private String yob;
    /**
     * It is Mobile Number
     */
//    @Mobile
//    @NotNull(message = FIELD_BLANK_ERROR_MSG)
    @JsonProperty("mobile")
    private String mobile;
}
