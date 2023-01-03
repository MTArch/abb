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
public class GeneralUtils {
    /**
     * Method to check if given String is palindrome
     *
     * @param str
     * @return true if String is a Palindrome.
     */
    public boolean isPalindrome(String str) {
        return str.equals(new StringBuffer(str).reverse().toString());
    }

    public String stringTrimmer(String str) {
        return StringUtils.isEmpty(str) ? str : str.trim();
    }

    public String generateRandomOTP() {
        return RandomStringUtils.randomNumeric(6);
    }

    public static Long getCurrentTime() {
        return System.currentTimeMillis() + (1000);
    }

    public boolean isOtpExpired(LocalDateTime currentDateTime, long expireTimeMin) {
        boolean isOTPExpire = false;
        if (Objects.nonNull(currentDateTime)) {
            isOTPExpire = expireTimeMin < TimeUnit.MILLISECONDS.toMinutes(getCurrentTime() - Common.dateOf(currentDateTime).getTime());
        }
        return isOTPExpire;
    }

    public String documentChecksum(String documentId){
        return DigestUtils.md5Hex(documentId);
    }
}

