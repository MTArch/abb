package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.validators.annotations.*;
import lombok.Data;

@TimestampDemo
@AadhaarNumberDemo
@Mobile
@Data
public class DemoDto {

    /**
     * It is a Date
     */
    @JsonProperty("timeStamp")
    private String timestamp;

    /**
     * It is a Aadhaar Number
     */
    @JsonProperty("aadhaar")
    private String aadhaar;

    /**
     * It is a Persons Name
     */
    @JsonProperty("name")
    @Name
    private String name;

    /**
     * It is a nameMatchStrategy
     */
    @JsonProperty("nameMatchStrategy")
    private String nameMatchStrategy;

    /**
     * It is gender Male/Female
     */
    @JsonProperty("gender")
    @Gender
    private String gender;

    /**
     * It is year of birth
     */
    @JsonProperty("yob")
    @YOB
    private String yob;

    /**
     * It is Mobile Number
     */
    @JsonProperty("mobile")
    private String mobile;
}
