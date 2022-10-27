package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ABHAProfileDto {
    @JsonProperty("ABHANumber")
    private String abhaNumber;
    @JsonProperty("abhaStatus")
    private AccountStatus abhaStatus;
    @JsonProperty("ABHAStatusReasonCode")
    private String abhaStatusReasonCode;
    private String poi;
    private String firstName;
    private Object middleName;
    private Object lastName;
    private String dob;
    private String gender;
    private byte[] photo;
    private String mobile;
    private String email;
    private ArrayList<String> phrAddress;
    private String addressLine1;
    private String districtCode;
    private String stateCode;
    private String pinCode;
    private String qrCode;
    private String pdfData;
    private ArrayList<BenefitInfoDto> benefitInfo;

}
