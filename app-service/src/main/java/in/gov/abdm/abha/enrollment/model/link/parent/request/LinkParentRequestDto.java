package in.gov.abdm.abha.enrollment.model.link.parent.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.ConsentDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LinkParentRequestDto {

    @JsonProperty("txnId")
    private String txnId;

    @JsonProperty("scope")
    private List<Scopes> scope;

//    @JsonProperty("ParentAbha")
//    private ParentAbhaRequestDto parentAbhaRequestDto;
    @JsonProperty("ParentAbha")
    private List<ParentAbhaRequestDto> parentAbhaRequestDtoList;

    @JsonProperty("ChildAbha")
    private ChildAbhaRequestDto childAbhaRequestDto;

    @JsonProperty("consent")
    private ConsentDto consentDto;
}
