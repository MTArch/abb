package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.FaceDto;
import in.gov.abdm.abha.enrollment.utilities.VerhoeffAlgorithm;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import in.gov.abdm.abha.enrollment.validators.annotations.AadhaarNumberFace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Validating aadhaar number should be valid and encrypted for abha creation using aadhaar
 */
public class AadhaarNumberFaceValidator implements ConstraintValidator<AadhaarNumberFace, FaceDto> {

    @Autowired
    RSAUtil rsaUtil;

    @Override
    public boolean isValid(FaceDto faceDto, ConstraintValidatorContext context) {
        if (!StringUtils.isEmpty(faceDto)
                && faceDto != null && aadhaarNotNullorEmpty(faceDto.getAadhaar())) {
            if (faceDto.getAadhaar() != null && isValidInput(faceDto.getAadhaar()) && isRSAEncrypted(faceDto.getAadhaar())) {
                String decryptedAadhaar = rsaUtil.decrypt(faceDto.getAadhaar());
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
     *
     * @param aadhaar
     * @return
     */
    private boolean isRSAEncrypted(String aadhaar) {
        try {
            new String(Base64.getDecoder().decode(aadhaar));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
