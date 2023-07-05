package in.gov.abdm.abha.enrollment.services.enrol.aadhaar.iris;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.exception.application.BadRequestException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.BioDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.IrisDto;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

@Slf4j
public class EnrolByIrisValidatorService {
    private static final String AADHAAR = "Aadhaar";
    private static final String TIMESTAMP = "Timestamp";
    private static final String DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";
    @Autowired
    RSAUtil rsaUtil;


    public void validateEnrolByIris(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto) {
        IrisDto irisDto = enrolByAadhaarRequestDto.getAuthData().getIris();
        LinkedHashMap<String, String> errors = new LinkedHashMap<>();
        if (!isValidAadhaar(irisDto)) {
            errors.put(AADHAAR, AbhaConstants.AADHAAR_NUMBER_INVALID);
        }
        if (!isValidTimeStamp(irisDto)) {
            errors.put(TIMESTAMP, AbhaConstants.VALIDATION_ERROR_TIMESTAMP_FIELD);
        }
        if (errors.size() != 0) {
            throw new BadRequestException(errors);
        }
    }

    private boolean isValidAadhaar(IrisDto irisDto) {
        return rsaUtil.isRSAEncrypted(irisDto.getAadhaar()) && GeneralUtils.isValidAadhaarNumber(rsaUtil.decrypt(irisDto.getAadhaar()));
    }
    private boolean isValidTimeStamp(IrisDto irisDto) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
        if(!ObjectUtils.isEmpty(irisDto)
                 && timestampNotNullOrEmpty(irisDto.getTimestamp())) {
            try {
                return LocalDateTime.parse(irisDto.getTimestamp(), dateTimeFormatter).isBefore(LocalDateTime.now());
            } catch (Exception ex) {
                log.error("Error while parsing timestamp",ex);
                return false;
            }
        }
        return true;
    }

    private boolean timestampNotNullOrEmpty(String timestamp) {
        return timestamp!=null
                && !timestamp.isEmpty();
    }
}
