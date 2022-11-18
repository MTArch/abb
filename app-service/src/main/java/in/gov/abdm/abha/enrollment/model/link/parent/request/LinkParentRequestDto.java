package in.gov.abdm.abha.enrollment.model.link.parent.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.ConsentDto;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidChildAbhaRequest;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidParentAbhaRequest;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidParentScope;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidTransactionId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ValidTransactionId
@ValidParentScope
@ValidParentAbhaRequest
@ValidChildAbhaRequest
public class LinkParentRequestDto {

    @JsonProperty("txnId")
    @NotNull(message = AbhaConstants.VALIDATION_ERROR_TRANSACTION_FIELD)
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
    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_CONSENT_CODE_FIELD)
    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_CONSENT_VERSION_FIELD)
    private ConsentDto consentDto;
}
