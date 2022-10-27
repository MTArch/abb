package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidLoginId;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Validating login Id as Aadhaar number or mobile number based on proposed otp system
 */
public class LoginIdValidator implements ConstraintValidator<ValidLoginId, MobileOrEmailOtpRequestDto> {

    /**
     * Constant for mobile number pattern matching
     */
    private static final String MOBILE_NO_REGEX_PATTERN = "(0/91)?[7-9]\\d{9}";

    /**
     * Constant for 10-digit mobile number pattern matching
     */
    private static final String MOBILE_NO_10_DIGIT_REGEX_PATTERN = "(0/91)?[7-9]\\d{9}";

    /**
     * Injected Utility class to utilise RSA encryption and decryption for aadhaar no.
     */
    @Autowired
    RSAUtil rsaUtil;

    /**
     * Implements the validation for Login Id
     * If otp system is aadhaar , login id will be aadhaar number and perform regex validation on decrypted aadhaar no.
     * If otp system is abdm , login id will be mobile number and perform mobile regex validations
     *
     * @param mobileOrEmailOtpRequestDto object to validate
     * @param context context in which the constraint is evaluated
     *
     * @return
     */
    @Override
    public boolean isValid(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto, ConstraintValidatorContext context) {
//
//        String value = mobileOrEmailOtpRequestDto.getLoginId();
//        if(mobileOrEmailOtpRequestDto.getOtpSystem()!=null && !mobileOrEmailOtpRequestDto.getOtpSystem().isEmpty())
//        {
//            if(isRSAEncrypted(value))
//            {
//                String loginId = rsaUtil.decrypt(value);
//                if (mobileOrEmailOtpRequestDto.getOtpSystem().equals("abdm")) {
//                    if(isDigit(loginId) && loginId.length() == 10)
//                        return isValidMobile(loginId);
//                    else
//                        return false;
//                } else if (mobileOrEmailOtpRequestDto.getOtpSystem().equals("aadhaar")) {
//                    if(isDigit(loginId) && loginId.length() == 12)
//                        return isValidAadhaar(loginId);
//                    else
//                        return false;
//                }
//                else
//                {
//                    if(isDigit(loginId) && loginId.length() == 10)
//                        return isValidMobile(loginId);
//                    else if(isDigit(loginId) && loginId.length() == 12)
//                        return isValidAadhaar(loginId);
//                    else
//                        return false;
//                }
//            }
//            else
//                return false;
//        }
//        else if(StringUtils.isEmpty(mobileOrEmailOtpRequestDto.getOtpSystem()))
//        {
//            if(isRSAEncrypted(value))
//            {
//                String loginId = rsaUtil.decrypt(value);
//                if (isDigit(loginId) && loginId.length() == 10)
//                    return isValidMobile(loginId);
//                else if (isDigit(loginId) && loginId.length() == 12)
//                    return isValidAadhaar(loginId);
//                else
//                    return false;
//            }
//        }
//        return false;
        return true;
    }

    private boolean isDigit(String loginId) {
        return loginId.chars().allMatch(Character::isDigit);
    }

    private boolean isRSAEncrypted(String loginId) {
        try {
            new String(Base64.getDecoder().decode(loginId));
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    /**
     * implements Mobile Number Validation
     *
     * @param mobileNo
     * @return
     */
    private boolean isValidMobile(String mobileNo) {
        return Pattern.compile(MOBILE_NO_REGEX_PATTERN).matcher(mobileNo).matches()
                && Pattern.compile(MOBILE_NO_10_DIGIT_REGEX_PATTERN).matcher(mobileNo).matches();
    }

    /**
     * implements aadhaar number validation
     *
     * @param aadhaar
     * @return
     */
    private boolean isValidAadhaar(String aadhaar){
        String decryptedAaadhar = rsaUtil.decrypt(aadhaar);
        return validateVerhoeff(decryptedAaadhar);
    }

    static int[][] d = new int[][] {
            { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 },
            { 1, 2, 3, 4, 0, 6, 7, 8, 9, 5 },
            { 2, 3, 4, 0, 1, 7, 8, 9, 5, 6 },
            { 3, 4, 0, 1, 2, 8, 9, 5, 6, 7 },
            { 4, 0, 1, 2, 3, 9, 5, 6, 7, 8 },
            { 5, 9, 8, 7, 6, 0, 4, 3, 2, 1 },
            { 6, 5, 9, 8, 7, 1, 0, 4, 3, 2 },
            { 7, 6, 5, 9, 8, 2, 1, 0, 4, 3 },
            { 8, 7, 6, 5, 9, 3, 2, 1, 0, 4 },
            { 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 } };

    static int[][] p = new int[][] {
            { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 },
            { 1, 5, 7, 6, 2, 8, 3, 0, 9, 4 },
            { 5, 8, 0, 3, 7, 9, 6, 1, 4, 2 },
            { 8, 9, 1, 6, 0, 4, 3, 5, 2, 7 },
            { 9, 4, 5, 3, 1, 2, 6, 8, 7, 0 },
            { 4, 2, 8, 6, 5, 7, 3, 9, 0, 1 },
            { 2, 7, 9, 3, 8, 0, 6, 4, 1, 5 },
            { 7, 0, 4, 6, 9, 1, 3, 2, 5, 8 } };

    public static boolean validateVerhoeff(String aadhaar) {
        int c = 0;
        int[] myArray = StringToReversedIntArray(aadhaar);
        for (int i = 0; i < myArray.length; i++) {
            c = d[c][p[(i % 8)][myArray[i]]];
        }
        return (c == 0);
    }

    private static int[] StringToReversedIntArray(String aadhaar) {
        int[] myArray = new int[aadhaar.length()];
        for (int i = 0; i < aadhaar.length(); i++) {
            myArray[i] = Integer.parseInt(aadhaar.substring(i, i + 1));
        }
        myArray = Reverse(myArray);
        return myArray;
    }

    private static int[] Reverse(int[] myArray) {
        int[] reversed = new int[myArray.length];
        for (int i = 0; i < myArray.length; i++) {
            reversed[i] = myArray[myArray.length - (i + 1)];
        }
        return reversed;
    }
}
