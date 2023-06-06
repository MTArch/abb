package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.DemoDto;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import in.gov.abdm.abha.enrollment.validators.annotations.Mobile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Base64;
import java.util.regex.Pattern;
/**
 * Validating mobile number should be encrypted and valid
 *
 */
@Slf4j
public class MobileValidator implements ConstraintValidator<Mobile, DemoDto> {

    @Autowired
    RSAUtil rsaUtil;

    private static final String MOBILE_NUMBER_REGEX_PATTERN = "(\\+91)?[1-9][0-9]{9}";

    @Override
    public boolean isValid(DemoDto demoDto, ConstraintValidatorContext cvc) {
        if(demoDto!=null && mobileNotNullorEmpty(demoDto)) {
            if (isValidInput(demoDto.getMobile()) && isRSAEncrypted(demoDto.getMobile())) {
                String decryptedMobile = rsaUtil.decrypt(demoDto.getMobile());
                return Pattern.compile(MOBILE_NUMBER_REGEX_PATTERN).matcher(decryptedMobile).matches();
            }
            return false;
        }
        return true;
    }

    private boolean mobileNotNullorEmpty(DemoDto demoDto) {
        return demoDto.getMobile()!=null
                && !demoDto.getMobile().isEmpty();
    }

    private boolean isValidInput(String mobile) {
        return !Pattern.compile("[0-9]+").matcher(mobile).matches()
                && !Pattern.compile("[a-zA-Z]+").matcher(mobile).matches();
    }


    /**
     * to validate input is encrypted or not
     * @param mobile
     * @return
     */
    private boolean isRSAEncrypted(String mobile) {
        try {
            new String(Base64.getDecoder().decode(mobile));
            return true;
        }
        catch (Exception ex)
        {
            log.error(ex.getMessage(),ex);
            return false;
        }
    }
}
