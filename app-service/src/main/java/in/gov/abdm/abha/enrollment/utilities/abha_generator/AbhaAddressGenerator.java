package in.gov.abdm.abha.enrollment.utilities.abha_generator;

import in.gov.abdm.abha.enrollment.constants.StringConstants;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Value;

@UtilityClass
public class AbhaAddressGenerator {


    @Value("${enrollment.domain}")
    private String ABHA_ADDRESS_EXTENSION;

    public String generateDefaultAbhaAddress(String abhaNumber){
        return new StringBuffer(abhaNumber.replaceAll(StringConstants.DASH, StringConstants.EMPTY))
                .append(StringConstants.AT)+ ABHA_ADDRESS_EXTENSION;
    }
}
