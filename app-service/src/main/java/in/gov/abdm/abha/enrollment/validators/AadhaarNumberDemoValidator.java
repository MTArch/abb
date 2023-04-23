package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.DemoDto;
import in.gov.abdm.abha.enrollment.utilities.VerhoeffAlgorithm;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import in.gov.abdm.abha.enrollment.validators.annotations.AadhaarNumberDemo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Base64;
import java.util.regex.Pattern;
/**
 * Validating aadhaar number should be valid and encrypted for abha creation using aadhaar
 */
@Slf4j
public class AadhaarNumberDemoValidator implements ConstraintValidator<AadhaarNumberDemo, DemoDto> {

    @Autowired
    RSAUtil rsaUtil;

    @Override
    public boolean isValid(DemoDto demoDto, ConstraintValidatorContext context) {
        if(demoDto !=null && aadhaarNotNullorEmpty(demoDto.getAadhaar())) {
            if (demoDto.getAadhaar() != null && isValidInput(demoDto.getAadhaar()) && isRSAEncrypted(demoDto.getAadhaar())) {
                String decryptedAadhaar = rsaUtil.decrypt(demoDto.getAadhaar());
                if (Pattern.compile("\\d{12}").matcher(decryptedAadhaar).matches())
                    return VerhoeffAlgorithm.validateVerhoeff(decryptedAadhaar);
            }
            return false;
        }
        return true;
    }

    private boolean aadhaarNotNullorEmpty(String aadhaar) {
        return aadhaar != null
                && !aadhaar.isEmpty();
    }

    private boolean isValidInput(String aadhaar) {
        return !Pattern.compile("[0-9]+").matcher(aadhaar).matches()
                && !Pattern.compile("[a-zA-Z]+").matcher(aadhaar).matches();
    }

    /**
     * to validate input is encrypted or not
     * @param aadhaar
     * @return
     */
    private boolean isRSAEncrypted(String aadhaar) {
        try {
            new String(Base64.getDecoder().decode(aadhaar));
            return true;
        }
        catch (Exception ex)
        {
            log.error(ex.getMessage());
            return false;
        }
    }
}
