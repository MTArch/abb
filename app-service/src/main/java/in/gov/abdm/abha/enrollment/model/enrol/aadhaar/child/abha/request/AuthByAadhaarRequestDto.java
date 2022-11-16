package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.enums.request.ScopeEnum;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidScope;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;

@Data
@ValidScope
public class AuthByAadhaarRequestDto {

    @JsonProperty("scope")
    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_SCOPE_FIELD)
    ArrayList<ScopeEnum> scope;

    @JsonProperty("authData")
    AuthData authData;
}
