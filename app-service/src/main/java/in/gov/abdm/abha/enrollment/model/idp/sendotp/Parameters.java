package in.gov.abdm.abha.enrollment.model.idp.sendotp;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * It is a Pojo class Parameters
 */
public class Parameters {
    /**
     * It is Key("abhaNumber")
     */
    @NotEmpty(message = AbhaConstants.VALIDATION_EMPTY_KEY_FIELD)
    @JsonProperty("key")
    private String key;
    /**
     * It is value (AbhaNumber Value)
     */
    @NotEmpty(message = AbhaConstants.VALIDATION_EMPTY_VALUE_FIELD)
    @JsonProperty("value")
    private String value;
}
