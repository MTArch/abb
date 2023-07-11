package in.gov.abdm.abha.enrollment.services.enrol.aadhaar.iris;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.exception.application.BadRequestException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.IrisDto;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;

@Slf4j
public class EnrolByIrisValidatorService {
    private static final String AADHAAR = "Aadhaar";

    @Autowired
    RSAUtil rsaUtil;


    public void validateEnrolByIris(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto) {
        IrisDto irisDto = enrolByAadhaarRequestDto.getAuthData().getIris();
        LinkedHashMap<String, String> errors = new LinkedHashMap<>();
        if (!isValidAadhaar(irisDto)) {
            errors.put(AADHAAR, AbhaConstants.AADHAAR_NUMBER_INVALID);
        }
        if (errors.size() != 0) {
            throw new BadRequestException(errors);
        }
    }

    private boolean isValidAadhaar(IrisDto irisDto) {
        return rsaUtil.isRSAEncrypted(irisDto.getAadhaar()) && GeneralUtils.isValidAadhaarNumber(rsaUtil.decrypt(irisDto.getAadhaar()));
    }

}
