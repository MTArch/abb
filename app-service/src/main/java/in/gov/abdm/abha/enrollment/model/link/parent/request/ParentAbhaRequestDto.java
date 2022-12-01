package in.gov.abdm.abha.enrollment.model.link.parent.request;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.enums.link.parent.Relationship;
import in.gov.abdm.abha.enrollment.validators.annotations.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParentAbhaRequestDto {


    @JsonProperty("ABHANumber")
    @NotNull(message = AbhaConstants.INVALID_PARENT_ABHA_NUMBER)
    @AbhaNumber
    private String ABHANumber;

    @JsonProperty("name")
    @Name
    private String name;

    @JsonProperty("yearOfBirth")
    @NotNull(message = AbhaConstants.YEAR_OF_BIRTH_INVALID)
    @YOB
    private String yearOfBirth;

    @JsonProperty("gender")
    @Gender
    private String gender;

    @JsonProperty("mobile")
    @Mobile
    private String mobile;

    @JsonProperty("email")
    @Email
    private String email;

    @JsonProperty("relationship")
    @NotNull(message = AbhaConstants.VALIDATION_ERROR_RELATIONSHIP_FIELD)
    @ValidRelationship
    private Relationship relationship;

    @JsonProperty("document")
    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_DOCUMENT_FIELD)
    @ValidDocument
    private String document;

}
