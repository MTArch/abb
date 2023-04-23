package in.gov.abdm.abha.enrollment.utilities.abha_generator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import in.gov.abdm.abha.enrollment.constants.StringConstants;

import static in.gov.abdm.abha.enrollment.constants.PropertyConstants.ENROLLMENT_DOMAIN;

@Service
public class AbhaAddressGenerator {


    @Value(ENROLLMENT_DOMAIN)
    private String abhaAddressExtension;

    public String generateDefaultAbhaAddress(String abhaNumber){
        return new StringBuffer(abhaNumber.replaceAll(StringConstants.DASH, StringConstants.EMPTY))
                .append(StringConstants.AT)+ abhaAddressExtension;
    }
    
    
}
