package in.gov.abdm.abha.enrollment.validators;
import java.util.Base64;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import in.gov.abdm.abha.enrollment.validators.annotations.OtpValue;

/**
 * Validating otp value should be valid 6-digit number and must be encrypted
 */
@Slf4j
public class OtpValueValidator implements ConstraintValidator<OtpValue, String> {

    @Autowired
    RSAUtil rsaUtil;

    /**
     * Constant for mobile number pattern matching
     */
    private static final String OTP_REGEX_PATTERN = "[0-9]{6}";

    @Override
	public boolean isValid(String otp, ConstraintValidatorContext context) {
		if (otp != null && !otp.isEmpty() && isValidInput(otp) && isRSAEncrypted(otp)) {
			return Pattern.compile(OTP_REGEX_PATTERN).matcher(rsaUtil.decrypt(otp)).matches();
		}
		return false;
	}


    private boolean isValidInput(String otp) {
        return !Pattern.compile("[0-9]+").matcher(otp).matches()
                && !Pattern.compile("[a-zA-Z]+").matcher(otp).matches();
    }

    /**
     * check if otp number is encrypted or not
     * @param value
     * @return
     */
    private boolean isRSAEncrypted(String value) {
        try {
            new String(Base64.getDecoder().decode(value));
            return true;
        }
        catch (Exception ex)
        {
            log.error(ex.getMessage());
            return false;
        }
    }

}
