package in.gov.abdm.abha.enrollment.model.enrol.facility;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties
public class EnrollmentResponse {
    private String status;
    private String message;
    private String token;
}
