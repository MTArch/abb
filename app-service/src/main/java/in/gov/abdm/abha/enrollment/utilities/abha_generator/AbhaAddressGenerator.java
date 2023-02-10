package in.gov.abdm.abha.enrollment.utilities.abha_generator;

import in.gov.abdm.abha.enrollment.constants.StringConstants;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AbhaAddressGenerator {


    public String generateDefaultAbhaAddress(String abhaNumber, String domain){
        return new StringBuffer(abhaNumber.replaceAll(StringConstants.DASH, StringConstants.EMPTY))
                .append(StringConstants.AT)+ domain;
    }
}
