package in.gov.abdm.abha.enrollment.model.profile.children;

import com.fasterxml.jackson.annotation.JsonInclude;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAChildProfileDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChildrenProfiles {
	private String parentAbhaNumber;
	private String mobileNumber;
	private String address;
	private long childrenCount;
	public List<ABHAChildProfileDto> children;
}
