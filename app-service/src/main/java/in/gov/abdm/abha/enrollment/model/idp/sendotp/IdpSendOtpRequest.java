package in.gov.abdm.abha.enrollment.model.idp.sendotp;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidScope;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * It is an Pojo class IdpSendOtpRequest
 */
@ValidScope
public class IdpSendOtpRequest {
    /**
     * It is a scope
     */
    @NotEmpty(message = AbhaConstants.VALIDATION_EMPTY_SCOPE_FIELD)
    @JsonProperty("scope")
    private String scope;
    /**
     * It is a Parameters
     */
    @JsonProperty("parameters")
    private List<Parameters> parameters;
}