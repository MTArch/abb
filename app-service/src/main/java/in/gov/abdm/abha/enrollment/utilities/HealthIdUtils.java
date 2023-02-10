package in.gov.abdm.abha.enrollment.utilities;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * It is General Utils class
 */
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

