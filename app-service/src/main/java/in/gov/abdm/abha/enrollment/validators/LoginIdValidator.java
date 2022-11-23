package in.gov.abdm.abha.enrollment.validators;

import java.util.Base64;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import in.gov.abdm.abha.enrollment.enums.LoginHint;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.utilities.VerhoeffAlgorithm;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidLoginId;

/**
 * Validating login Id as Aadhaar number or mobile number based on proposed otp system
 */
public class LoginIdValidator implements ConstraintValidator<ValidLoginId, MobileOrEmailOtpRequestDto> {

    /**
     * Constant for any 10-digit number pattern matching, 
     * Starting from 1-9
     */
    private static final String MOBILE_NO_10_DIGIT_REGEX_PATTERN = "[1-9]\\d{9}";

    /**
     * Constant for any 14-digit number pattern matching,
     * $1-$2-$3-$4 
     * Starting from 91
     */
    private static final String ABHA_NO_REGEX_PATTERN = "^91[\\-]\\d{4}[\\-]\\d{4}[\\-]\\d{4}";
    
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
     * @param context                    context in which the constraint is evaluated
     * @return
     */
    @Override
	public boolean isValid(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto, ConstraintValidatorContext context) {
		if (!StringUtils.isEmpty(mobileOrEmailOtpRequestDto.getLoginId())
				&& mobileOrEmailOtpRequestDto.getLoginId() != null) {

			if (isRSAEncrypted(mobileOrEmailOtpRequestDto.getLoginId())
					&& isValidInput(mobileOrEmailOtpRequestDto.getLoginId())
					&& mobileOrEmailOtpRequestDto.getLoginHint() != null) {

				String loginId = rsaUtil.decrypt(mobileOrEmailOtpRequestDto.getLoginId());
				if (mobileOrEmailOtpRequestDto.getLoginHint().equals(LoginHint.MOBILE)) {
					return isValidMobile(loginId);
				} else if (mobileOrEmailOtpRequestDto.getLoginHint().equals(LoginHint.AADHAAR)) {
					return isValidAadhaar(loginId);
				} else if (mobileOrEmailOtpRequestDto.getLoginHint().equals(LoginHint.ABHA_NUMBER)) {
					return isValidAbha(loginId);
				}
			}
		}
		return false;
	}

    private boolean isValidInput(String loginId) {
        return !Pattern.compile("[0-9]+").matcher(loginId).matches()
                && !Pattern.compile("[a-zA-Z]+").matcher(loginId).matches();
    }


    private boolean isRSAEncrypted(String loginId) {
        try {
            new String(Base64.getDecoder().decode(loginId));
            return true;
        } catch (Exception ex) {
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
        return Pattern.compile(MOBILE_NO_10_DIGIT_REGEX_PATTERN).matcher(mobileNo).matches();
    }

    /**
     * implements aadhaar number validation
     *
     * @param aadhaar
     * @return
     */
    private boolean isValidAadhaar(String aadhaar) {
        return VerhoeffAlgorithm.validateVerhoeff(aadhaar);
    }

    /**
     * @param abha
     * @return
     */
	private boolean isValidAbha(String abha) {
		return Pattern.compile(ABHA_NO_REGEX_PATTERN).matcher(abha).matches();
	}
}
