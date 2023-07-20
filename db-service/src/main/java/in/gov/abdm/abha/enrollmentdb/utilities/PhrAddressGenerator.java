package in.gov.abdm.abha.enrollmentdb.utilities;

import in.gov.abdm.abha.enrollmentdb.constant.StringConstants;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PhrAddressGenerator {

    public static final String PHR_ADDRESS_EXTENSION = "sbx";

    public String generateDefaultPhrAddress(String phrId) {

        return new StringBuffer(phrId.replaceAll(StringConstants.DASH, StringConstants.EMPTY)).append(StringConstants.AT) + PHR_ADDRESS_EXTENSION;
    }
}
