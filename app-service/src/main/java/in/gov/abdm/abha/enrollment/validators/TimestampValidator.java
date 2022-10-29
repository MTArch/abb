package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.validators.annotations.Timestamp;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Validating timestamp should not be greater than current date
 */
public class TimestampValidator implements ConstraintValidator<Timestamp, String> {

    private String DATE_TIME_FORMATTER = "dd-MM-yyyy HH:mm:ss";
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);

    @Override
    public boolean isValid(String strDate, ConstraintValidatorContext context) {

          try
          {
              LocalDateTime.parse(strDate, dateTimeFormatter).isBefore(LocalDateTime.now());
              return true;
          }
        catch (Exception ex)
        {
            return false;
        }
    }
}
