package in.gov.abdm.abha.enrollment.client;

import java.util.regex.Pattern;

import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.RandomUtil;

public class Test {
	
	 /**
     * Constant for mobile number pattern matching
     * 
     */
    private static final String MOBILE_NO_REGEX_PATTERN = "^91-\\d{4}-\\d{4}-\\d{4}";

    /**
     * Constant for any 10-digit number pattern matching
     */
    private static final String MOBILE_NO_10_DIGIT_REGEX_PATTERN = "[1-9]\\d{9}";
    
    private static final String ABHA_NO_17_DIGIT_REGEX_PATTERN = "^[A-Za-z](([A-Za-z0-9]{3,31})|(([A-Za-z0-9]*\\\\.[A-Za-z0-9]+)))$";
    private static final String ABHA_NO_REGEX_PATTERN = "^[A-Za-z](([A-Za-z0-9]{3,31})|(([A-Za-z0-9]*\\.[A-Za-z0-9]+)))$";
    

	public static void main(String[] args) {

//		System.out.println(new Test().generateAbhaNumber());
//		System.out.println(isValidMobile("6813010161"));
//		System.out.println(isValidMobile("7813010161"));
//		System.out.println(isValidMobile("8813010161"));
//		System.out.println(isValidMobile("9813010161"));
//		System.out.println(isValidMobile("0781301016"));
//		System.out.println(isValidMobile("1781301016"));
		
		System.out.println(Pattern.compile(MOBILE_NO_REGEX_PATTERN).matcher("91-3100-1304-3750").matches());
		System.out.println(Pattern.compile(MOBILE_NO_REGEX_PATTERN).matcher("91--3100-1304-3750").matches());
		System.out.println(Pattern.compile(MOBILE_NO_REGEX_PATTERN).matcher("90-3100-1304-3750").matches());
		System.out.println(Pattern.compile(MOBILE_NO_REGEX_PATTERN).matcher("99-3100-1304-3750").matches());
		System.out.println(Pattern.compile(MOBILE_NO_REGEX_PATTERN).matcher("91-33100-1304-3750").matches());
		System.out.println(Pattern.compile(MOBILE_NO_REGEX_PATTERN).matcher("91-3100-13054-3750").matches());
		System.out.println(Pattern.compile(MOBILE_NO_REGEX_PATTERN).matcher("91-3100-1305-37507").matches());
	}
	
	 private static boolean isValidMobile(String mobileNo) {
	        return Pattern.compile(MOBILE_NO_10_DIGIT_REGEX_PATTERN).matcher(mobileNo).matches();
	    }

	/**
	 * Length of ABHA Number is 14 digits unique number
	 */
	private static final int ABHA_NUMBER_LENGTH = 14;
	private static final String NUMBER_IS_NULL_OR_EMPTY = "Number is null or empty";
	/**
	 * ABHA Number Hyphen structure is xx-xxxx-xxxx-xxxx
	 */
	private static String HYPHEN = "-";
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
	 * ABHA Number Must not be Palindrome and must be unique. Hence taking bin as
	 * Random between 1-8 only and keep generating a number. until it's not
	 * palindrome and This number is not already in use.
	 *
	 * @return
	 */
	public String generateAbhaNumber() {
		long randomInteger = RandomUtil.getRandomInteger(8, 1);
		String generatedNumber;
		String generatedNumberWithHyphon;
		do {
			generatedNumber = generate(randomInteger);
			generatedNumberWithHyphon = formatAbhaNumber(generatedNumber);
		} while (GeneralUtils.isPalindrome(generatedNumber) || !passesLuhnCheck(generatedNumber)
				|| UserExistWithAbhaNumber(generatedNumberWithHyphon));
		return generatedNumberWithHyphon;
	}

	/**
	 * ABHA Number follows the pattern is {2}{4}{4}{4}. ABHA Number pattern group is
	 * XX-XXXX-XXXX-XXXX
	 *
	 * @param AbhaNumber
	 * @return
	 */
	private String formatAbhaNumber(String AbhaNumber) {
		return AbhaNumber.replaceAll(ABHA_NUMBER_PATTERN, ABHA_NUMBER_PATTERN_GROUPS);
	}

	/**
	 * If the user is already exist with ABHA Number or not
	 *
	 * @param abhaNumber
	 * @return
	 */
	private boolean UserExistWithAbhaNumber(String abhaNumber) {
		// TODO check abha number available in db
		return false;
	}

	/**
	 * Generate Health Id Number using Luhn Algorithm Every ABHA Number is started
	 * with 91 after 12 digits are unique for every user
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
