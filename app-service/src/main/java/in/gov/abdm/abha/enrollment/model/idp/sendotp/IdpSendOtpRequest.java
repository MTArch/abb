package in.gov.abdm.abha.enrollment.model.idp.sendotp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * It is an Pojo class IdpSendOtpRequest
 */
public class IdpSendOtpRequest {
    /**
     * It is a scope
     */
    @JsonProperty("scope")
    private String scope;
    /**
     * It is a Parameters
     */
    @JsonProperty("parameters")
    private Map<String, String> parameters = new HashMap();
}