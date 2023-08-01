package in.gov.abdm.abha.enrollment.utilities;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;


/**
 * It is General Utils class
 */
@SuppressWarnings("java:S6353")
@UtilityClass
@Slf4j
public class HealthIdUtils {

    public static String actualHealthIdNumber(String healthIdNumber) {

        String parttern = "[0-9]{14}";
        if (!healthIdNumber.contains("-") && healthIdNumber.matches(parttern)) {
            StringBuilder temphealthIdNumber = new StringBuilder();
            String tempZeroToTwo = healthIdNumber.substring(0, 2);
            String tempTwoToSix = healthIdNumber.substring(2, 6);
            String tempSixToTen = healthIdNumber.substring(6, 10);
            String tempTenToFourteenth = healthIdNumber.substring(10, 14);
            healthIdNumber = temphealthIdNumber.append(tempZeroToTwo).append("-").append(tempTwoToSix).append("-")
                    .append(tempSixToTen).append("-").append(tempTenToFourteenth).toString();

        }

        return healthIdNumber;
    }

}

