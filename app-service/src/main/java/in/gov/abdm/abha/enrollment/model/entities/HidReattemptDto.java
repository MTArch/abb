package in.gov.abdm.abha.enrollment.model.entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HidReattemptDto {
	private String healthIdNumber;
	private String requestType;
	private String createdBy;
	private String updatedBy;

}
