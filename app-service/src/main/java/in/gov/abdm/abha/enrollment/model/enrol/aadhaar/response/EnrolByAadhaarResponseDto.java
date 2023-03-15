package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnrolByAadhaarResponseDto {

    @JsonProperty("txnId")
    private String txnId;
    @JsonProperty("tokens")
    private ResponseTokensDto responseTokensDto;
    @JsonProperty("ABHAProfile")
    private ABHAProfileDto abhaProfileDto;
    private String message;
    @JsonProperty("isNew")
    private boolean isNew;
}
