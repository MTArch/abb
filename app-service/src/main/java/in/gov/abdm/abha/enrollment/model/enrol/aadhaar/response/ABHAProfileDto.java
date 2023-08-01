package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.enums.childabha.AbhaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ABHAProfileDto {
	
    @JsonProperty("ABHANumber")
    private String abhaNumber;
    @JsonProperty("abhaStatus")
    private AccountStatus abhaStatus;
    private String firstName;
    private Object middleName;
    private Object lastName;
    private String dob;
    private String gender;
    private String photo;
    private String mobile;
    private String email;
    private List<String> phrAddress;
    private String address;
    private String districtCode;
    private String stateCode;
    private String pinCode;
    private AbhaType abhaType;
}
