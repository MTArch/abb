package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.BioDto;
import in.gov.abdm.abha.enrollment.validators.annotations.TimestampBio;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Validating timestamp should not be greater than current date
 */
public class TimestampBioValidator implements ConstraintValidator<TimestampBio, BioDto> {
    private String DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);

    @Override
    public boolean isValid(BioDto bioDto, ConstraintValidatorContext context) {
        if(!StringUtils.isEmpty(bioDto)
                && bioDto!=null && timestampNotNullorEmpty(bioDto.getTimestamp())) {
            try {
                return LocalDateTime.parse(bioDto.getTimestamp(), dateTimeFormatter).isBefore(LocalDateTime.now());
            } catch (Exception ex) {
                return false;
            }
        }
        return true;
    }

    private boolean timestampNotNullorEmpty(String timestamp) {
        return timestamp!=null
                && !timestamp.isEmpty();
    }
}
