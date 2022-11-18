package in.gov.abdm.abha.enrollment.model.link.parent.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinkParentResponseDto {

    @JsonProperty("txnId")
    private String txnId;

    @JsonProperty("ABHAProfile")
    private ABHAProfileDto abhaProfileDto;
}
