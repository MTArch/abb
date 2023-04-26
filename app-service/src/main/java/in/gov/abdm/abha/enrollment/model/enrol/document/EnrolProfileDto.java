package in.gov.abdm.abha.enrollment.model.enrol.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class EnrolProfileDto {
    private String enrolmentNumber;
    private String abhaNumber;
    private String enrolmentState;
    private String firstName;
    private String middleName;
    private String lastName;
    private String dob;
    private String gender;
    private String mobile;
    private String email;
    private String address;
    private String districtCode;
    private String district;
    private String stateCode;
    private String state;
    private String abhaType;
    private String pinCode;
    private String abhaStatus;
    private List<String> phrAddress;
}
