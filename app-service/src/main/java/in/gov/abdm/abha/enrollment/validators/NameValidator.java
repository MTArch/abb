package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.DemoDto;
import in.gov.abdm.abha.enrollment.validators.annotations.Name;
import org.springframework.util.StringUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
/**
 * Validating name should be a valid name
 *
 */
public class NameValidator implements ConstraintValidator<Name, DemoDto> {
    private static final String NAME_REGEX_PATTERN = "^[A-Za-z\\s\\.\\']{1,}";

    @Override
    public boolean isValid(DemoDto demoDto, ConstraintValidatorContext context) {
        if(!StringUtils.isEmpty(demoDto)
                && demoDto!=null && nameNotNullorEmpty(demoDto)) {
            return Pattern.compile(NAME_REGEX_PATTERN).matcher(demoDto.getName()).matches();
        }
        return true;
    }

    private boolean nameNotNullorEmpty(DemoDto demoDto) {
        return demoDto.getName()!=null
                && !demoDto.getName().isEmpty();
    }
}
