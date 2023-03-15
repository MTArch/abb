package in.gov.abdm.abha.enrollment.model.enrol.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.model.enrol.facility.EnrollmentResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class EnrolByDocumentResponseDto {
    @JsonProperty("EnrolProfile")
    EnrolProfileDto enrolProfileDto;
    EnrollmentResponse enrollmentResponse;
}
