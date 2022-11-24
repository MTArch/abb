package in.gov.abdm.abha.enrollment.model.link.parent.request;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.enums.link.parent.Relationship;
import in.gov.abdm.abha.enrollment.validators.annotations.AbhaNumber;
import in.gov.abdm.abha.enrollment.validators.annotations.Gender;
import in.gov.abdm.abha.enrollment.validators.annotations.Mobile;
import in.gov.abdm.abha.enrollment.validators.annotations.Name;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidDocument;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidRelationship;
import in.gov.abdm.abha.enrollment.validators.annotations.YOB;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ValidRelationship
public class ParentAbhaRequestDto {


    @JsonProperty("ABHANumber")
    @NotNull(message = AbhaConstants.VALIDATION_NULL_ABHA_NUMBER)
    @AbhaNumber
    private String ABHANumber;

    @JsonProperty("name")
    @NotNull(message = AbhaConstants.INVALID_NAME_FORMAT)
    @Name
    private String name;

    @JsonProperty("yearOfBirth")
    @NotNull(message = AbhaConstants.PATTERN_MISMATCHED)
    @YOB
    private String yearOfBirth;

    @JsonProperty("gender")
    @NotNull(message = AbhaConstants.VALIDATION_ERROR_GENDER_FIELD)
    @Gender
    private String gender;

    @JsonProperty("mobile")
    @NotNull(message = AbhaConstants.MOBILE_NUMBER_MISSMATCH)
    @Mobile
    private String mobile;

    @JsonProperty("email")
    private String email;

    
    @JsonProperty("relationship")
    @NotNull(message = AbhaConstants.VALIDATION_ERROR_RELATIONSHIP_FIELD)
    private Relationship relationship;

    @JsonProperty("document")
    @NotNull(message = AbhaConstants.VALIDATION_ERROR_DOCUMENT_FIELD)
    @ValidDocument
    private String document;

}
