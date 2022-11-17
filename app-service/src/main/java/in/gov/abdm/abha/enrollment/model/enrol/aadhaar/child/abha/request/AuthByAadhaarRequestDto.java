package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request;

import java.util.ArrayList;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidScope;
import lombok.Data;

@Data
@ValidScope
public class AuthByAadhaarRequestDto {

    @JsonProperty("scope")
    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_SCOPE_FIELD)
    ArrayList<Scopes> scope;

    @JsonProperty("authData")
    AuthData authData;
}
