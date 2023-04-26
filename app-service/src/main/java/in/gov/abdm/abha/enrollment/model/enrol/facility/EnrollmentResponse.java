package in.gov.abdm.abha.enrollment.model.enrol.facility;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class EnrollmentResponse {
    private String status;
    private String message;
    private String token;
    private Long expiresIn;
    private Long refreshExpiresIn;
    private String refreshToken;
}
