package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.DemoDto;
import in.gov.abdm.abha.enrollment.validators.annotations.TimestampDemo;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 * Validating timestamp should not be greater than current date
 */
public class TimestampDemoValidator implements ConstraintValidator<TimestampDemo, DemoDto> {
    private String DATE_TIME_FORMATTER = "dd-MM-yyyy HH:mm:ss";
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);

    @Override
    public boolean isValid(DemoDto demoDto, ConstraintValidatorContext context) {
        if (!StringUtils.isEmpty(demoDto)
                && demoDto != null && timestampNotNullorEmpty(demoDto.getTimestamp())) {
            try {
                return LocalDateTime.parse(demoDto.getTimestamp(), dateTimeFormatter).isBefore(LocalDateTime.now());
            } catch (Exception ex) {
                return false;
            }
        }
        return true;
    }

    private boolean timestampNotNullorEmpty(String timestamp) {
        return timestamp != null
                && !timestamp.isEmpty();
    }
}
