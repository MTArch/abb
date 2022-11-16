package in.gov.abdm.abha.enrollment.model.authbyabdm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
/**
 * It is Pojo class AuthByAbdmRequest
 */
public class AuthByAbdmRequest {
    /**
     * it is a scope
     */
    @JsonProperty("scope")
    public ArrayList<String> scope;
    /**
     * it is a authdata
     */
    @JsonProperty("authData")
    public AuthData authData;
}
