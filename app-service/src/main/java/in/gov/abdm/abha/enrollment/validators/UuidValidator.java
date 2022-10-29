package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.OtpDto;
import in.gov.abdm.abha.enrollment.validators.annotations.Uuid;
import org.springframework.util.StringUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Validates uuid/txn id
 *
 * it should match regex pattern
 */
public class UuidValidator implements ConstraintValidator<Uuid, OtpDto> {

    String UUID_REGEX_PATTERN = "[0-9abcdef-]{36}";

    @Override
    public boolean isValid(OtpDto otpDto, ConstraintValidatorContext context) {
        if(!StringUtils.isEmpty(otpDto)
                && otpDto!=null && uidNotNullorEmpty(otpDto)) {
            return Pattern.compile(UUID_REGEX_PATTERN).matcher(otpDto.getTxnId()).matches();
        }
        return true;
    }

    private boolean uidNotNullorEmpty(OtpDto otpDto) {
        return otpDto.getTxnId()!=null
                && !otpDto.getTxnId().isEmpty();
    }
}
