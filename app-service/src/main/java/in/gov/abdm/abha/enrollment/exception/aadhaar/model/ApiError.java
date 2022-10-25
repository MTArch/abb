package in.gov.abdm.abha.enrollment.exception.aadhaar.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * pojo to respond error codes and message to endpoints
 * errors related to UIDAI
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiError {

	private String code;
	private String message;
	private List<ErrorDetails> details;
}
