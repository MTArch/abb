package in.gov.abdm.abha.enrollment.utilities.abha_generator;

import in.gov.abdm.abha.enrollment.constants.StringConstants;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AbhaAddressGenerator {

    public static final String ABHA_ADDRESS_EXTENSION = "abdm";

    public String generateDefaultAbhaAddress(String abhaNumber){
        return new StringBuffer(abhaNumber.replaceAll(StringConstants.DASH, StringConstants.EMPTY))
                .append(StringConstants.AT)+ ABHA_ADDRESS_EXTENSION;
    }
}
