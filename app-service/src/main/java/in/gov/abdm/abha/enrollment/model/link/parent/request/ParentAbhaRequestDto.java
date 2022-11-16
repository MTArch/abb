package in.gov.abdm.abha.enrollment.model.link.parent.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.annotations.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Name
@YOB
@Gender
@Mobile
@ValidRelationship
@ValidDocument
@AbhaId
public class ParentAbhaRequestDto {


    @JsonProperty("ABHANumber")
    @NotNull(message = AbhaConstants.ABHA_ID)
    private String ABHANumber;

    @JsonProperty("name")
    @NotNull(message = AbhaConstants.INVALID_NAME_FORMAT)
    private String name;

    @JsonProperty("yearOfBirth")
    @NotNull(message = AbhaConstants.PATTERN_MISMATCHED)
    private String yearOfBirth;

    @JsonProperty("gender")
    @NotNull(message = AbhaConstants.VALIDATION_ERROR_GENDER_FIELD)
    private String gender;

    @JsonProperty("mobile")
    @NotNull(message = AbhaConstants.MOBILE_NUMBER_MISSMATCH)
    private String mobile;

    @JsonProperty("email")
    private String email;

    @JsonProperty("relationship")
    @NotNull
    private String relationship;

    @JsonProperty("document")
    @NotNull
    private String document;

}
