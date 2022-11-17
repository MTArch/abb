package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import lombok.Data;

import java.util.ArrayList;

@Data
public class AuthByAadhaarRequestDto {

    @JsonProperty("scope")
    ArrayList<Scopes> scope;

    @JsonProperty("authData")
    AuthData authData;
}
