package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;

@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HidBenefitRequestPayload {
	private String healthIdNumber;
	private String benefitId;
	private String healthId;
	private String mobile;
	private String firstName;
	private String middleName;
	private String lastName;
	private String name;
	private String yearOfBirth;
	private String dayOfBirth;
	private String monthOfBirth;
	private String gender;
	private String email;
	private String profilePhoto;
	private String stateCode;
	private String districtCode;
	private String subDistrictCode;
	private String villageCode;
	private String townCode;
	private String wardCode;
	private String pincode;
	private String address;
	private String kycPhoto;
	private String stateName;
	private String districtName;
	private String subdistrictName;
	private String villageName;
	private String townName;
	private String wardName;
	private boolean isNew;
	private Map<String, String> tags;
	private boolean kycVerified;
	private String token;
	private ResponseTokensDto jwtResponse;
	private String status;
}