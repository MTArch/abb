package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.OtpDto;
import in.gov.abdm.abha.enrollment.validators.annotations.TimestampOtp;
import org.springframework.util.StringUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Validating timestamp should not be greater than current date
 */
public class TimestampOtpValidator implements ConstraintValidator<TimestampOtp, OtpDto> {

    private String DATE_TIME_FORMATTER = "dd-MM-yyyy HH:mm:ss";
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);

    @Override
    public boolean isValid(OtpDto otpDto, ConstraintValidatorContext context) {
        if(!StringUtils.isEmpty(otpDto)
                && otpDto!=null && timestampNotNullorEmpty(otpDto.getTimeStamp())) {
            try {
                return LocalDateTime.parse(otpDto.getTimeStamp(), dateTimeFormatter).isBefore(LocalDateTime.now());
            } catch (Exception ex) {
                return false;
            }
        }
        return true;
    }

//    @Override
//    public boolean isValid(AuthData authData, ConstraintValidatorContext context) {
//        if(typeOtp)
//        {
//            if(!StringUtils.isEmpty(authData.getOtp())
//                    && authData.getOtp()!=null && timestampNotNullorEmpty(authData.getOtp().getTimeStamp())) {
//                try {
//                    return LocalDateTime.parse(authData.getOtp().getTimeStamp(), dateTimeFormatter).isBefore(LocalDateTime.now());
//                } catch (Exception ex) {
//                    return false;
//                }
//            }
//        }
//        if(typeDemo)
//        {
//            if(!StringUtils.isEmpty(authData.getDemo())
//                    && authData.getDemo()!=null && timestampNotNullorEmpty(authData.getDemo().getTimestamp())) {
//                try {
//                    return LocalDateTime.parse(authData.getDemo().getTimestamp(), dateTimeFormatter).isBefore(LocalDateTime.now());
//                } catch (Exception ex) {
//                    return false;
//                }
//            }
//        }
//        if(typeBio)
//        {
//            if(!StringUtils.isEmpty(authData.getBio())
//                    && authData.getBio()!=null && timestampNotNullorEmpty(authData.getBio().getTimestamp())) {
//                try {
//                    return LocalDateTime.parse(authData.getBio().getTimestamp(), dateTimeFormatter).isBefore(LocalDateTime.now());
//                } catch (Exception ex) {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }



    private boolean timestampNotNullorEmpty(String timestamp) {
        return timestamp!=null
                && !timestamp.isEmpty();
    }
}
