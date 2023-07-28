package in.gov.abdm.abha.enrollment.services.enrol.aadhaar.bio;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.exception.application.BadRequestException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.BioDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

@Slf4j
public class EnrolByBioValidatorService {
    private static final String AADHAAR = "Aadhaar";
    private static final String MOBILE = "Mobile";
    private static final String TIMESTAMP = "Timestamp";
    private static final String DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";
    private static final String MOBILE_NUMBER_REGEX_PATTERN = "(\\+91)?[1-9][0-9]{9}";
    @Autowired
    RSAUtil rsaUtil;


    public void validateEnrolByBio(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, String fToken) {
        BioDto bioData = enrolByAadhaarRequestDto.getAuthData().getBio();
        LinkedHashMap<String, String> errors = new LinkedHashMap<>();
        if (!isValidAadhaar(bioData)) {
            errors.put(AADHAAR, AbhaConstants.AADHAAR_NUMBER_INVALID);
        }
        if(!isMobileRequired(fToken,enrolByAadhaarRequestDto)){
            errors.put(MOBILE, AbhaConstants.MOBILE_NUMBER_MISSMATCH);
        }

        if (errors.size() != 0) {
            throw new BadRequestException(errors);
        }
    }

    private boolean isMobileRequired(String fToken, EnrolByAadhaarRequestDto enrolByAadhaarRequestDto){
        String mobile = enrolByAadhaarRequestDto.getAuthData().getBio().getMobile();
        if(null != fToken){
            if(mobile==null){
                return true;
            }else{
                return Pattern.compile(MOBILE_NUMBER_REGEX_PATTERN).matcher(mobile).matches();
            }
        }else{
            return null != mobile && Pattern.compile(MOBILE_NUMBER_REGEX_PATTERN).matcher(mobile).matches();
        }
    }

    private boolean isValidAadhaar(BioDto bioData) {
        return rsaUtil.isRSAEncrypted(bioData.getAadhaar()) && GeneralUtils.isValidAadhaarNumber(rsaUtil.decrypt(bioData.getAadhaar()));
    }

}
