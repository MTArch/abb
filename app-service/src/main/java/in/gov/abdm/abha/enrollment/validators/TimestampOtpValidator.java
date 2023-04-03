package in.gov.abdm.abha.enrollment.validators;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.gov.abdm.abha.enrollment.validators.annotations.TimestampOtp;
import lombok.extern.slf4j.Slf4j;

/**
 * Validating timestamp 
 * It should be in given format and in given range
 */
@Slf4j
public class TimestampOtpValidator implements ConstraintValidator<TimestampOtp, String> {

	private String DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";
	DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);

	private int minusMinutes = 10;
	private int plusMinutes = 10;

	@Override
	public boolean isValid(String timestamp, ConstraintValidatorContext context) {
		if (timestampNotNullorEmpty(timestamp)) {
			try {
				LocalDateTime currentTime = LocalDateTime.now();
				return LocalDateTime.parse(timestamp, dateTimeFormatter).isBefore(currentTime.plusMinutes(plusMinutes))
						&& LocalDateTime.parse(timestamp, dateTimeFormatter)
								.isAfter(currentTime.minusMinutes(minusMinutes));
			} catch (Exception ex) {
				log.error(ex.getMessage());
				return false;
			}
		}
		return true;
	}

	private boolean timestampNotNullorEmpty(String timestamp) {
		return timestamp != null && !timestamp.isEmpty();
	}
}
