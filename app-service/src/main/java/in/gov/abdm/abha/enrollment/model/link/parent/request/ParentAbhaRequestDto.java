package in.gov.abdm.abha.enrollment.model.link.parent.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParentAbhaRequestDto {

    @JsonProperty("ABHANumber")
    private String ABHANumber;

    @JsonProperty("name")
    private String name;

    @JsonProperty("yearOfBirth")
    private String yearOfBirth;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("mobile")
    private String mobile;

    @JsonProperty("email")
    private String email;

    @JsonProperty("relationship")
    private String relationship;

    @JsonProperty("document")
    private String document;

}
