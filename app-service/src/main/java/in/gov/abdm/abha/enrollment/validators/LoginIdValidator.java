package in.gov.abdm.abha.enrollment.validators;

import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import in.gov.abdm.abha.profile.constants.StringConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import in.gov.abdm.abha.enrollment.enums.LoginHint;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidLoginId;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.EMAIL_REGEX_PATTERN;

/**
 * Validating login Id as Aadhaar number or mobile number based on proposed otp system
 */
@Slf4j
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
    private static final String ABHA_NO_REGEX_PATTERN = "\\d{2}-\\d{4}-\\d{4}-\\d{4}";

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
     * @param context in which the constraint is evaluated
     */
    @Override
    public boolean isValid(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto, ConstraintValidatorContext context) {
        if (!StringUtils.isEmpty(mobileOrEmailOtpRequestDto.getLoginId().trim()) && mobileOrEmailOtpRequestDto.getLoginId()!=null
                && mobileOrEmailOtpRequestDto.getLoginHint() != null && mobileOrEmailOtpRequestDto.getScope() != null) {
            if(rsaUtil.decrypt(mobileOrEmailOtpRequestDto.getLoginId()).equals(StringConstants.EMPTY))
                return false;

            if (isRSAEncrypted(mobileOrEmailOtpRequestDto.getLoginId())
                    && isValidInput(mobileOrEmailOtpRequestDto.getLoginId())) {

                return isValidEmailOrMobileOrAadhaarOrAbha(mobileOrEmailOtpRequestDto);

            }
            else return true;
        }else if(mobileOrEmailOtpRequestDto.getLoginId()==null ||StringUtils.isEmpty(mobileOrEmailOtpRequestDto.getLoginId().trim())) {
            return false;
        }
        else
            return true;

    }

    /**
     * Validates a given MobileOrEmailOtpRequestDto object by checking whether the loginId
     * is a valid mobile number, email address, Aadhaar number, or ABHA number based on the loginHint and scope values.
     * @param mobileOrEmailOtpRequestDto the MobileOrEmailOtpRequestDto object to validate
     * @return true if the loginId is a valid mobile number, email address, Aadhaar number, or ABHA number
     */
    private boolean isValidEmailOrMobileOrAadhaarOrAbha(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto){
        String loginId = rsaUtil.decrypt(mobileOrEmailOtpRequestDto.getLoginId());

        if (Common.isAllScopesAvailable(mobileOrEmailOtpRequestDto.getScope(), List.of(Scopes.ABHA_ENROL, Scopes.MOBILE_VERIFY))) {
            return isValidMobile(loginId);
        } else if (Common.isAllScopesAvailable(mobileOrEmailOtpRequestDto.getScope(), List.of(Scopes.ABHA_ENROL, Scopes.EMAIL_VERIFY))) {
            return isValidEmail(loginId);
        } else if (mobileOrEmailOtpRequestDto.getLoginHint().equals(LoginHint.AADHAAR)
                && (Common.isAllScopesAvailable(mobileOrEmailOtpRequestDto.getScope(), List.of(Scopes.ABHA_ENROL))
                || Common.isAllScopesAvailable(mobileOrEmailOtpRequestDto.getScope(), List.of(Scopes.CHILD_ABHA_ENROL)))) {
            return isValidAadhaar(loginId);
        } else if (mobileOrEmailOtpRequestDto.getLoginHint().equals(LoginHint.ABHA_NUMBER)) {
            return isValidAbha(loginId);
        }
        else {
            return true;
        }
    }

    /**
     * Validates a given string as a valid email address.
     * @param email the string to validate
     * @return true if the given string is a valid email address, false otherwise
     */
    private boolean isValidEmail(String email) {
        return Pattern.compile(EMAIL_REGEX_PATTERN).matcher(email).matches();
    }

    /**
     * Checks whether a given string is a valid input.
     * A valid input should not consist of only digits or only letters.
     * @param loginId the string to check
     * @return true if the given string is a valid input, false otherwise
     */
    private boolean isValidInput(String loginId) {
        return !Pattern.compile("[0-9]+").matcher(loginId).matches()
                && !Pattern.compile("[a-zA-Z]+").matcher(loginId).matches();
    }

    /**
     * Checks whether a given string is RSA encrypted.
     * @param loginId the string to check
     * @return true if the given string is RSA encrypted, false otherwise
     */
    private boolean isRSAEncrypted(String loginId) {
        try {
            new String(Base64.getDecoder().decode(loginId));
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage(),ex);
            return false;
        }
    }

    /**
     * Validates a given string as a valid 10-digit mobile number.
     * @param mobileNo the string to validate
     * @return true if the given string is a valid 10-digit mobile number, false otherwise
     */
    private boolean isValidMobile(String mobileNo) {
        return Pattern.compile(MOBILE_NO_10_DIGIT_REGEX_PATTERN).matcher(mobileNo).matches();
    }

    /**
     * Validates a given string as a valid Aadhaar number.
     * @param aadhaar the string to validate
     * @return true if the given string is a valid Aadhaar number, false otherwise
     */
    private boolean isValidAadhaar(String aadhaar) {
        return GeneralUtils.isValidAadhaarNumber(aadhaar);
    }

    /**
     * Validates a given string as a valid Abha number.
     * @param abha the string to validate
     * @return true if the given string is a valid Abha number, false otherwise
     */
    private boolean isValidAbha(String abha) {
        return Pattern.compile(ABHA_NO_REGEX_PATTERN).matcher(abha).matches();
    }
}
