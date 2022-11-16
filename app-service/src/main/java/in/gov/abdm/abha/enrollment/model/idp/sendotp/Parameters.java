package in.gov.abdm.abha.enrollment.model.idp.sendotp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * It is a Pojo class Parameters
 */
public class Parameters {
    /**
     * It is Key
     */
    @JsonProperty("key")
    private String key;
    /**
     * It is value
     */
    @JsonProperty("value")
    private String value;
}
