package in.gov.abdm.abha.enrollment.model.authbyabdm;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidScope;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ValidScope
/**
 * It is Pojo class AuthByAbdmRequest
 */
public class AuthByAbdmRequest {
    /**
     * it is a scope
     */
    @JsonProperty("scope")
    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_SCOPE_FIELD)
    public ArrayList<String> scope;
    /**
     * it is a authdata
     */
    @JsonProperty("authData")
    public AuthData authData;
}
