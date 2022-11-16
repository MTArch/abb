package in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
/**
 * It is Kyc class
 */
public class Kyc {
    /**
     * It is name
     */
    @JsonProperty("name")
    public String name;
    /**
     * It is a abhaNumber
     */
    @JsonProperty("abhaNumber")
    public String abhaNumber;
    /**
     * It is a abhaAddress
     */
    @JsonProperty("abhaAddress")
    public String abhaAddress;
    /**
     * It is a Year of Birth
     */
    @JsonProperty("yearOfBirth")
    public String yearOfBirth;
    /**
     * It is a gender
     */
    @JsonProperty("gender")
    public String gender;
    /**
     * It is a Mobile Number
     */
    @JsonProperty("mobile")
    public String mobile;
    /**
     * It is a Email id
     */
    @JsonProperty("email")
    public String email;
}
