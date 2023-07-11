package in.gov.abdm.abha.enrollment.services.enrol.aadhaar.bio;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.exception.application.BadRequestException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.BioDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

@Slf4j
public class EnrolByBioValidatorService {
    private static final String AADHAAR = "Aadhaar";
    private static final String TIMESTAMP = "Timestamp";
    private static final String DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";
    @Autowired
    RSAUtil rsaUtil;


    public void validateEnrolByBio(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto) {
        BioDto bioData = enrolByAadhaarRequestDto.getAuthData().getBio();
        LinkedHashMap<String, String> errors = new LinkedHashMap<>();
        if (!isValidAadhaar(bioData)) {
            errors.put(AADHAAR, AbhaConstants.AADHAAR_NUMBER_INVALID);
        }

        if (errors.size() != 0) {
            throw new BadRequestException(errors);
        }
    }

    private boolean isValidAadhaar(BioDto bioData) {
        return rsaUtil.isRSAEncrypted(bioData.getAadhaar()) && GeneralUtils.isValidAadhaarNumber(rsaUtil.decrypt(bioData.getAadhaar()));
    }

}
