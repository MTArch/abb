package in.gov.abdm.abha.enrollment.utilities.abha_generator;

import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * It is ABHA Number generator class
 */
@Slf4j
@UtilityClass
public class AbhaNumberGenerator {
    /**
     * Length of ABHA Number is 14 digits unique number
     */
    private static final int ABHA_NUMBER_LENGTH = 14;
    private static final String NUMBER_IS_NULL_OR_EMPTY = "Number is null or empty";

    /**
     * ABHA Number pattern is XX-XXXX-XXXX-XXXX
     */
    private static final String ABHA_NUMBER_PATTERN = "(\\d{2})(\\d{4})(\\d{4})(\\d{4})";
    /**
     * ABHA Number pattern have 4 groups i.e, XX-XXXX-XXXX-XXXX
     */
    private static final String ABHA_NUMBER_PATTERN_GROUPS = "$1-$2-$3-$4";
    /**
     * ABHA Number must be start with 91
     */
    private static final String HID_PREFIX = "91";

    /**
     * ABHA Number Must not be Palindrome and must be unique.
     * Hence taking bin as Random between 1-8 only and keep generating a number.
     * until it's not palindrome and This number is not already in use.
     */
    public String generateAbhaNumber() {
        long randomInteger = RandomUtil.getRandomInteger(8, 1);
        String generatedNumber;
        String generatedNumberWithHyphon;
        do {
            generatedNumber = generate(randomInteger);
            generatedNumberWithHyphon = formatAbhaNumber(generatedNumber);
        } while (GeneralUtils.isPalindrome(generatedNumber) || !passesLuhnCheck(generatedNumber));
        return generatedNumberWithHyphon;
    }

    /**
     * ABHA Number follows the pattern is {2}{4}{4}{4}.
     * ABHA Number pattern group is XX-XXXX-XXXX-XXXX
     *
     * @param abhaNumber
     */
    private String formatAbhaNumber(String abhaNumber) {
        return abhaNumber.replaceAll(ABHA_NUMBER_PATTERN, ABHA_NUMBER_PATTERN_GROUPS);
    }

    /**
     * Generate Health Id Number using Luhn Algorithm
     * Every ABHA Number is started with 91 after 12 digits are unique for every user
     *
     * @param randomNumber
     * @return
     */
    private String generate(final Long randomNumber) {
        final StringBuffer num = new StringBuffer(HID_PREFIX + randomNumber.toString());
        final int howManyMore = ABHA_NUMBER_LENGTH - num.toString().length() - 1;
        for (int i = 0; i < howManyMore; i++) {
            num.append(Integer.valueOf(RandomUtil.getRandomNumber(9)));
        }
        num.append(calculateCheckDigit(num.toString()));
        return num.toString();
    }

    /**
     * Calculate checksum digit for given number using luhn algorithm.
     *
     * @param str
     * @return checksum digit.
     */
    private int calculateCheckDigit(final String str) {
        final int sum = calculateLuhnSum(str, false);
        final int checkDigit = calculateCheckDigit(sum);
        return checkDigit;
    }

    /**
     * Calculate checksum digit for given number using luhn algorithm.
     *
     * @param luhnSum
     * @return checksum digit.
     */
    private int calculateCheckDigit(final int luhnSum) {
        final int checkDigit = (luhnSum * 9) % 10;
        return checkDigit;
    }

    /**
     * Calculate Luhn algorithm based checksum number.
     *
     * @param str
     * @param hasCheckDigit
     * @return calculated checksum.
     */
    private int calculateLuhnSum(final String str, final boolean hasCheckDigit) {
        final int luhnNums[] = new int[str.length()];
        final int start = str.length() - (hasCheckDigit ? 2 : 1);
        int sum = 0;

        boolean doubleMe = true;

        for (int i = start; i >= 0; i--) {
            final int num = Integer.parseInt(str.substring(i, i + 1));

            if (doubleMe) {
                int x2 = num * 2;
                luhnNums[i] = x2 > 9 ? x2 - 9 : x2;
            } else {
                luhnNums[i] = num;
            }

            sum += luhnNums[i];
            doubleMe = !doubleMe;
        }

        return sum;
    }

    /**
     * Check if given string has a number which passes Luhn's algorithm checksum
     *
     * @param num
     * @return true if its a valid Number with correct checksum.
     */
    private boolean passesLuhnCheck(final String num) {
        if (num == null || num.isEmpty()) {
            throw new IllegalArgumentException(NUMBER_IS_NULL_OR_EMPTY);
        }
        final int sum = calculateLuhnSum(num, true);
        final int checkDigit = calculateCheckDigit(sum);

        return (sum + checkDigit) % 10 == 0 && Integer.parseInt(num.substring(num.length() - 1)) == checkDigit;
    }
}
