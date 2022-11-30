package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request;

import java.util.ArrayList;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidScope;
import lombok.Data;

@Data
public class AuthRequestDto {

    @JsonProperty("scope")
    @ValidScope
    ArrayList<Scopes> scope;

    @JsonProperty("authData")
    @Valid
    AuthData authData;
}
