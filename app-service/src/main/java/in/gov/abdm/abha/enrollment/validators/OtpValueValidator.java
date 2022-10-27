package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import in.gov.abdm.abha.enrollment.validators.annotations.OtpValue;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Base64;
import java.util.regex.Pattern;


public class OtpValueValidator implements ConstraintValidator<OtpValue, String> {

    @Autowired
    RSAUtil rsaUtil;

    /**
     * Constant for mobile number pattern matching
     */
    private static final String OTP_REGEX_PATTERN = "[0-9]{6}";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if(isRSAEncrypted(value))
        {
            String otp = rsaUtil.decrypt(value);
            return Pattern.compile(OTP_REGEX_PATTERN).matcher(otp).matches();
        }
        else
            return false;
    }

    private boolean isRSAEncrypted(String value) {
        try {
            new String(Base64.getDecoder().decode(value));
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }
}
