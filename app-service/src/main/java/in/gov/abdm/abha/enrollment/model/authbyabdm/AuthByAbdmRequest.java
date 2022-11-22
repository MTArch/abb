package in.gov.abdm.abha.enrollment.model.authbyabdm;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidScope;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * It is Pojo class AuthByAbdmRequest
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ValidScope
public class AuthByAbdmRequest {
    /**
     * it is a scope
     */
    @JsonProperty("scope")
    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_SCOPE_FIELD)
    private List<Scopes> scope;
    /**
     * it is a authdata
     */
    @JsonProperty("authData")
    private AuthData authData;
}
