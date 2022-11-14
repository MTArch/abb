package in.gov.abdm.abha.enrollment.model.idp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Parameter;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor

/**
 * It is IdpMobileSendOtpRequest pojo class
 */
public class IdpMobileSendOtpRequest {

    private String scope;
    private ArrayList<Parameter> parameters;
}
