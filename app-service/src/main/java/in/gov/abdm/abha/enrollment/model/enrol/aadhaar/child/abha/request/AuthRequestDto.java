package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request;

import java.util.ArrayList;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidScope;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class AuthRequestDto {

    @JsonProperty("scope")
    @ValidScope
    ArrayList<Scopes> scope;

    @JsonProperty("authData")
    @NotNull(message = AbhaConstants.VALIDATION_ERROR_AUTH_DATA_FIELD)
    @Valid
    AuthData authData;
}
