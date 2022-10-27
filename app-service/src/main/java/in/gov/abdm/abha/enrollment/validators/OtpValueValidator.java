package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.OtpDto;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import in.gov.abdm.abha.enrollment.validators.annotations.OtpValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Validating otp value should be valid 6-digit number and must be encrypted
 */
public class OtpValueValidator implements ConstraintValidator<OtpValue, OtpDto> {

    @Autowired
    RSAUtil rsaUtil;

    /**
     * Constant for mobile number pattern matching
     */
    private static final String OTP_REGEX_PATTERN = "[0-9]{6}";

    @Override
    public boolean isValid(OtpDto otpDto, ConstraintValidatorContext context) {
        if(!StringUtils.isEmpty(otpDto)
                && otpDto!=null && otpValueNotNullorEmpty(otpDto)) {
            if (isValidInput(otpDto.getOtpValue()) && isRSAEncrypted(otpDto.getOtpValue())) {
                String otp = rsaUtil.decrypt(otpDto.getOtpValue());
                return Pattern.compile(OTP_REGEX_PATTERN).matcher(otp).matches();
            } else
                return false;
        }
        return true;
    }

    private boolean otpValueNotNullorEmpty(OtpDto otpDto) {
        return otpDto.getOtpValue()!=null
                && !otpDto.getOtpValue().isEmpty();
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
            return false;
        }
    }

}
