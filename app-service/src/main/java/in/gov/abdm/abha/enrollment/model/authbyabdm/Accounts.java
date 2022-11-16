package in.gov.abdm.abha.enrollment.model.authbyabdm;

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
 * It is a Accounts class
 */ public class Accounts {
    /**
     * It is
     */
    @JsonProperty("ABHANumber")
    public String aBHANumber;
    /**
     * It is a name
     */
    @JsonProperty("name")
    public String name;
    /**
     * It is a preferredAbhaAddress
     */
    @JsonProperty("preferredAbhaAddress")
    public String preferredAbhaAddress;
    /**
     * It is a yearOfBirth
     */
    @JsonProperty("yearOfBirth")
    public String yearOfBirth;
    /**
     * It is a gender
     */
    @JsonProperty("gender")
    public String gender;
    /**
     * It is a mobile
     */
    @JsonProperty("mobile")
    public String mobile;
    /**
     * It is a email
     */
    @JsonProperty("email")
    public String email;
}
