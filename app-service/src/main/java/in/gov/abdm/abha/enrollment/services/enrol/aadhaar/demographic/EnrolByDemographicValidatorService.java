package in.gov.abdm.abha.enrollment.services.enrol.aadhaar.demographic;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.PropertyConstants;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.MobileType;
import in.gov.abdm.abha.enrollment.exception.application.BadRequestException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.demographic.Demographic;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

@Service
@Slf4j
public class EnrolByDemographicValidatorService {

    private static final String M = "M";
    private static final String F = "F";
    private static final String O = "O";
    private static final String AADHAAR = "Aadhaar";
    private static final String GENDER = "Gender";
    private static final String DAY_OF_BIRTH = "dayOfBirth";
    private static final String MONTH_OF_BIRTH = "monthOfBirth";
    private static final String YEAR_OF_BIRTH = "yearOfBirth";
    private static final String FIRST_NAME = "FirstName";
    private static final String MIDDLE_NAME = "MiddleName";
    private static final String LAST_NAME = "LastName";
    private static final String PIN_CODE = "PinCode";
    private static final String STATE = "State";
    private static final String DISTRICT = "District";
    private static final String CONSENT_FORM_IMAGE = "ConsentFormImage";
    private static final String MOBILE = "mobile";
    private static final String MOBILE_TYPE = "mobileType";
    private static final String HEALTH_WORKER_MOBILE = "healthWorkerMobile";
    public static final int MAX_NAME_SIZE = 255;
    private String alphabeticCharOnlyRegex = "^[A-Za-z' ]+$";
    private String alphabeticCharOnlyRegexWithSpace = "^[A-Za-z ]+$";
    private String onlyDigitRegex = "^[0-9]{6}$";
    private String only2Digit = "^[0-9]{1,2}$";
    private String only4Digit = "^[0-9]{1,4}$";
    private static final String MOBILE_NO_10_DIGIT_REGEX_PATTERN = "[1-9]\\d{9}";



    @Value(PropertyConstants.ENROLLMENT_DOCUMENT_PHOTO_MIN_SIZE_IN_KB)
    private String documentPhotoMinSizeLimit;

    @Value(PropertyConstants.ENROLLMENT_DOCUMENT_PHOTO_MAX_SIZE_IN_KB)
    private String documentPhotoMaxSizeLimit;

    @Autowired
    RSAUtil rsaUtil;

    public void validateEnrolByDemographic(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto) {
        Demographic demographic = enrolByAadhaarRequestDto.getAuthData().getDemographic();
        LinkedHashMap<String, String> errors;
        errors = new LinkedHashMap<>();
        if (!isValidAadhaar(demographic)) {
            errors.put(AADHAAR, AbhaConstants.AADHAAR_NUMBER_INVALID);
        }
        if (!isValidGender(demographic)) {
            errors.put(GENDER, AbhaConstants.VALIDATION_ERROR_GENDER_FIELD);
        }
        validateNameAndDob(demographic, errors);
        if (!isValidPinCode(demographic)) {
            errors.put(PIN_CODE, AbhaConstants.INVALID_PIN_CODE);
        }
        if (!isValidState(demographic)) {
            errors.put(STATE, AbhaConstants.INVALID_STATE);
        }
        if (!isValidDistrict(demographic)) {
            errors.put(DISTRICT, AbhaConstants.INVALID_DISTRICT);
        }
        if (!isValidConsentFormImage(demographic)) {
            errors.put(CONSENT_FORM_IMAGE, AbhaConstants.INVALID_PHOTO_FORMAT);
        } else if (!isValidConsentFormImageFormat(demographic)) {
            errors.put(CONSENT_FORM_IMAGE, AbhaConstants.INVALID_DOCUMENT_PHOTO_SIZE);
        }
        if (!isValidMobileNumber(demographic)) {
            errors.put(MOBILE, AbhaConstants.INVALID_MOBILE_NUMBER);
        }
        if (!isValidMobileType(demographic)) {
            errors.put(MOBILE_TYPE, AbhaConstants.INVALID_MOBILE_TYPE);
        }
        if (!isValidHealthWorkerMobile(demographic)) {
            errors.put(HEALTH_WORKER_MOBILE, AbhaConstants.INVALID_MOBILE_NUMBER);
        }
        if (errors.size() != 0) {
            throw new BadRequestException(errors);
        }
    }

    private void validateNameAndDob(Demographic demographic, LinkedHashMap<String, String> errors) {
        if (!isValidDayOfBirth(demographic)) {
            errors.put(DAY_OF_BIRTH, AbhaConstants.INVALID_DAY_OF_BIRTH);
        }
        if (!isValidMonthOfBirth(demographic)) {
            errors.put(MONTH_OF_BIRTH, AbhaConstants.INVALID_MONTH_OF_BIRTH);
        }
        if (!isValidYearOfBirth(demographic)) {
            errors.put(YEAR_OF_BIRTH, AbhaConstants.INVALID_YEAR_OF_BIRTH);
        }
        if (!isValidFirstName(demographic)) {
            errors.put(FIRST_NAME, AbhaConstants.INVALID_FIRST_NAME);
        }
        if (!isValidMiddleName(demographic)) {
            errors.put(MIDDLE_NAME, AbhaConstants.INVALID_MIDDLE_NAME);
        }
        if (!isValidLastName(demographic)) {
            errors.put(LAST_NAME, AbhaConstants.INVALID_LAST_NAME);
        }
    }

    private boolean isValidMobileType(Demographic demographic) {
        return !demographic.getMobileType().equals(MobileType.WRONG);
    }

    private boolean isValidHealthWorkerMobile(Demographic demographic) {
        return Pattern.compile(MOBILE_NO_10_DIGIT_REGEX_PATTERN).matcher(demographic.getHealthWorkerMobile()).matches();
    }

    private boolean isValidMobileNumber(Demographic demographic) {
        return Pattern.compile(MOBILE_NO_10_DIGIT_REGEX_PATTERN).matcher(demographic.getMobile()).matches();
    }

    private boolean isValidYearOfBirth(Demographic demographic) {
        return demographic.getYearOfBirth().matches(only4Digit) && Integer.parseInt(demographic.getYearOfBirth()) <= LocalDateTime.now().getYear();
    }

    private boolean isValidMonthOfBirth(Demographic demographic) {
        return (StringUtils.isEmpty(demographic.getMonthOfBirth()) || demographic.getMonthOfBirth().matches(only2Digit)) && Integer.parseInt(demographic.getMonthOfBirth()) <= 12;
    }

    private boolean isValidDayOfBirth(Demographic demographic) {
        if (StringUtils.isEmpty(demographic.getDayOfBirth())) {
            return true;
        } else if (isValidMonthOfBirth(demographic) && isValidYearOfBirth(demographic)) {
            YearMonth yearMonth = YearMonth.of(Integer.parseInt(demographic.getYearOfBirth()), Integer.parseInt(demographic.getMonthOfBirth()));
            return yearMonth.isValidDay(Integer.parseInt(demographic.getDayOfBirth()));
        } else {
            return false;
        }
    }

    private boolean isValidAadhaar(Demographic demographic) {
        try {
           return rsaUtil.isRSAEncrypted(demographic.getAadhaarNumber()) && GeneralUtils.isValidAadhaarNumber(rsaUtil.decrypt(demographic.getAadhaarNumber()));
        }catch(Exception ex){
            log.error("Invalid encryption value {}",ex.getMessage());
            return false;
        }
    }

    private boolean isValidConsentFormImage(Demographic demographic) {
        double size = GeneralUtils.fileSize(demographic.getConsentFormImage());
        return !(size < Integer.parseInt(documentPhotoMinSizeLimit)
                || size > Integer.parseInt(documentPhotoMaxSizeLimit));
    }

    private boolean isValidConsentFormImageFormat(Demographic demographic) {
        return GeneralUtils.isImageFileFormat(demographic.getConsentFormImage());
    }

    private boolean isValidDistrict(Demographic demographic) {
        return demographic.getDistrict().matches(alphabeticCharOnlyRegexWithSpace);
    }

    private boolean isValidState(Demographic demographic) {
        return demographic.getState().matches(alphabeticCharOnlyRegexWithSpace);
    }

    private boolean isValidPinCode(Demographic demographic) {
        return demographic.getPinCode().matches(onlyDigitRegex);
    }

    private boolean isValidLastName(Demographic demographic) {
        return StringUtils.isEmpty(demographic.getLastName())
                || (Common.validStringSize(demographic.getLastName(), MAX_NAME_SIZE)
                && demographic.getLastName().matches(alphabeticCharOnlyRegex));
    }

    private boolean isValidMiddleName(Demographic demographic) {
        return StringUtils.isEmpty(demographic.getMiddleName())
                || (Common.validStringSize(demographic.getMiddleName(), MAX_NAME_SIZE)
                && demographic.getMiddleName().matches(alphabeticCharOnlyRegex));
    }

    private boolean isValidFirstName(Demographic demographic) {
        return Common.validStringSize(demographic.getFirstName(), MAX_NAME_SIZE) && demographic.getFirstName().matches(alphabeticCharOnlyRegex);
    }

    private boolean isValidGender(Demographic demographic) {
        return demographic.getGender().equals(M) ||
                demographic.getGender().equals(F) ||
                demographic.getGender().equals(O);
    }
}
