package in.gov.abdm.abha.enrollmentdb.model.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountReattemptDto {
	private String healthIdNumber;
	private String requestType;
	private String createdBy;
	private final String version="v3";

}
