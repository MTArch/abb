package in.gov.abdm.abha.enrollment.validators;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.gov.abdm.abha.enrollment.validators.annotations.TimestampOtp;

/**
 * Validating timestamp should not be greater than current date
 */
public class TimestampOtpValidator implements ConstraintValidator<TimestampOtp, String> {

    private String DATE_TIME_FORMATTER = "dd-MM-yyyy HH:mm:ss";
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);

    @Override
    public boolean isValid(String timestamp, ConstraintValidatorContext context) {
        if(timestampNotNullorEmpty(timestamp)) {
            try {
                return LocalDateTime.parse(timestamp, dateTimeFormatter).isBefore(LocalDateTime.now());
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
