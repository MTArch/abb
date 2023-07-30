package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("java:S116")
public class AccountResponseDto {

    @JsonProperty("ABHANumber")
    private String ABHANumber;

    @JsonProperty("EnrolmentNumber")
    private String EnrolmentNumber;

    @JsonProperty("name")
    private String name;

    @JsonProperty("preferredAbhaAddress")
    private String preferredAbhaAddress;

    @JsonProperty("yearOfBirth")
    private String yearOfBirth;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("mobile")
    private String mobile;

    @JsonProperty("email")
    private String email;
    
    @JsonProperty("kycPhoto")
    private String kycPhoto;
}
