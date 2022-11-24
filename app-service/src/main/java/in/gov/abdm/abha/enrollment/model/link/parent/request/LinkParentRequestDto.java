package in.gov.abdm.abha.enrollment.model.link.parent.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.ConsentDto;
import in.gov.abdm.abha.enrollment.validators.annotations.Uuid;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidParentScope;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ValidParentScope
public class LinkParentRequestDto {

    @JsonProperty("txnId")
    @NotNull(message = AbhaConstants.VALIDATION_ERROR_TRANSACTION_FIELD)
    @Uuid
    private String txnId;

    @JsonProperty("scope")
    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_SCOPE_FIELD)
    private List<Scopes> scope;

    @JsonProperty("ParentAbha")
    @Valid
    private List<ParentAbhaRequestDto> parentAbhaRequestDtoList;

    @JsonProperty("ChildAbha")
    @Valid
    private ChildAbhaRequestDto childAbhaRequestDto;

    @JsonProperty("consent")
    @Valid
    private ConsentDto consentDto;
}
