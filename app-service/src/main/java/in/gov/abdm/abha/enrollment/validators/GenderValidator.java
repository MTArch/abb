package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.DemoDto;
import in.gov.abdm.abha.enrollment.validators.annotations.Gender;
import org.springframework.util.StringUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
/**
 * Validating gender should match one of the values from enum
 *
 */
public class GenderValidator implements ConstraintValidator<Gender, DemoDto> {
    private Boolean isRequired;

    @Override
    public void initialize(Gender gender) {
        this.isRequired = gender.required();
    }
    @Override
    public boolean isValid(DemoDto demoDto, ConstraintValidatorContext context) {

        if(!StringUtils.isEmpty(demoDto)
                && demoDto!=null && genderNotNullorEmpty(demoDto))
        {
            return in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.Gender.isValidByCode(demoDto.getGender());
        }
        return true;
    }

    private boolean genderNotNullorEmpty(DemoDto demoDto) {
        return demoDto.getGender()!=null
                && !demoDto.getGender().isEmpty();
    }
}
