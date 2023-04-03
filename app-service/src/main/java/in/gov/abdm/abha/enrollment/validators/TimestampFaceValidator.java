package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.FaceDto;
import in.gov.abdm.abha.enrollment.validators.annotations.TimestampBio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Validating timestamp should not be greater than current date
 */
@Slf4j
public class TimestampFaceValidator implements ConstraintValidator<TimestampBio, FaceDto> {
    private String DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);

    @Override
    public boolean isValid(FaceDto faceDto, ConstraintValidatorContext context) {
        if(!StringUtils.isEmpty(faceDto)
                && faceDto !=null && timestampNotNullorEmpty(faceDto.getTimestamp())) {
            try {
                return LocalDateTime.parse(faceDto.getTimestamp(), dateTimeFormatter).isBefore(LocalDateTime.now());
            } catch (Exception ex) {
                log.error(ex.getMessage());
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
