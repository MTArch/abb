package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.enums.request.OtpSystem;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.utilities.VerhoeffAlgorithm;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidLoginId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

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

        if(mobileOrEmailOtpRequestDto.getLoginId()!=null
                && !StringUtils.isEmpty(mobileOrEmailOtpRequestDto.getLoginId()))
        {
            if(isRSAEncrypted(mobileOrEmailOtpRequestDto.getLoginId()) && isValidInput(mobileOrEmailOtpRequestDto.getLoginId()))
            {
                if(rsaUtil.decrypt(mobileOrEmailOtpRequestDto.getLoginId()).length()==10)
                {
                    return mobileOrEmailOtpRequestDto.getOtpSystem()!=null
                            && !mobileOrEmailOtpRequestDto.getOtpSystem().isEmpty()
                            && !mobileOrEmailOtpRequestDto.getOtpSystem().equals("")
                            && mobileOrEmailOtpRequestDto.getOtpSystem().equals(OtpSystem.ABDM.getValue())
                            && isValidMobile(rsaUtil.decrypt(mobileOrEmailOtpRequestDto.getLoginId()));
                }
                else if(rsaUtil.decrypt(mobileOrEmailOtpRequestDto.getLoginId()).length()==12) {
                    return mobileOrEmailOtpRequestDto.getOtpSystem()!=null
                            && !mobileOrEmailOtpRequestDto.getOtpSystem().isEmpty()
                            && !mobileOrEmailOtpRequestDto.getOtpSystem().equals("")
                            && mobileOrEmailOtpRequestDto.getOtpSystem().equals(OtpSystem.AADHAAR.getValue())
                            && isValidAadhaar(rsaUtil.decrypt(mobileOrEmailOtpRequestDto.getLoginId()));
                }
            }
        }
        return false;
    }
    private boolean isValidInput(String loginId) {
        return !Pattern.compile("[0-9]+").matcher(loginId).matches()
                && !Pattern.compile("[a-zA-Z]+").matcher(loginId).matches();
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
        return VerhoeffAlgorithm.validateVerhoeff(aadhaar);
    }
}
