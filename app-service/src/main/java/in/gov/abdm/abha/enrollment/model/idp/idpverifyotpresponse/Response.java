package in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
/**
 * It is response class
 */
public class Response {
    /**
     * It is a requestId
     */
    @JsonProperty("requestId")
    public String requestId;
}
