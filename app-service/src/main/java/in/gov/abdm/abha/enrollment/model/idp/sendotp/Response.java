package in.gov.abdm.abha.enrollment.model.idp.sendotp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * It is a Response class
 */
public class Response {
    /**
     * It is a Request Id
     */
    @JsonProperty("requestId")
    private String requestId;
}
