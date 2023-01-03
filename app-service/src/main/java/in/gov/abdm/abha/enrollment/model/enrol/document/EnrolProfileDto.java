package in.gov.abdm.abha.enrollment.model.enrol.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnrolProfileDto {
    private String enrolmentNumber;
    private String enrolmentState;
    private String firstName;
    private String middleName;
    private String lastName;
    private String dob;
    private String gender;
    private String mobile;
    private String email;
    private String emailVerified;
    private String address;
    private String districtCode;
    private String stateCode;
    @JsonProperty("ABHAType")
    private String abhaType;
    private String pinCode;
}
