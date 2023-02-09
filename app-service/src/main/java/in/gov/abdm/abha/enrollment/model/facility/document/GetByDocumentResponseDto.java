package in.gov.abdm.abha.enrollment.model.facility.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetByDocumentResponseDto {
    @JsonProperty("EnrolProfile")
    EnrolProfileDetailsDto enrolProfileDto;
}
