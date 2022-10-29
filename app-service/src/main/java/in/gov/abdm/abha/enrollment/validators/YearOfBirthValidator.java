package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.DemoDto;
import in.gov.abdm.abha.enrollment.validators.annotations.YOB;
import org.springframework.util.StringUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
/**
 * Validates year of birth
 *
 * it should be valid 4-digit number
 */
public class YearOfBirthValidator implements ConstraintValidator<YOB, DemoDto> {

    private static final String YOB_REGEX_PATTERN = "^(\\d{4})$";

    @Override
    public boolean isValid(DemoDto demoDto, ConstraintValidatorContext context) {

        if(!StringUtils.isEmpty(demoDto)
                && demoDto!=null && yobNotNullorEmpty(demoDto)) {
            return Pattern.compile(YOB_REGEX_PATTERN).matcher(demoDto.getYob()).matches();
        }
        return true;
    }

    private boolean yobNotNullorEmpty(DemoDto demoDto) {
        return demoDto.getYob()!=null
                && !demoDto.getYob().isEmpty();
    }
}
