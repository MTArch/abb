package in.gov.abdm.abha.enrollment.exception.aadhaar.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * pojo to respond error codes and message to endpoints
 * errors related to UIDAI
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorDetails {

	private String message;
	private String code;
	private ErrorAttribute attribute;

}
