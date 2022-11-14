package in.gov.abdm.abha.enrollment.model.idp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Parameter;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdpSendOtpRequest {
    public String scope;
    public ArrayList<Parameter> parameters;
}