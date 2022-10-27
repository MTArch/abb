package in.gov.abdm.abha.enrollment.utilities;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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

    public static String stringTrimmer(String str) {
        return StringUtils.isEmpty(str) ? str : str.trim();
    }
}
