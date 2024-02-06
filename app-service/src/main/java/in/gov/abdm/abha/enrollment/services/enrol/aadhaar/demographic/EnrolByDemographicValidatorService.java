package in.gov.abdm.abha.enrollment.services.enrol.aadhaar.demographic;

import com.rabbitmq.client.Return;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.PropertyConstants;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.MobileType;
import in.gov.abdm.abha.enrollment.exception.application.BadRequestException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.demographic.Demographic;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.demographic.DemographicAuth;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Objects;
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

    private static final String NAME = "Name";
    private static final String MIDDLE_NAME = "MiddleName";
    private static final String LAST_NAME = "LastName";
    private static final String PIN_CODE = "PinCode";
    private static final String STATE = "State";
    private static final String DISTRICT = "District";
    private static final String CONSENT_FORM_IMAGE = "ConsentFormImage";
    private static final String MOBILE = "mobile";

    private static final String DATE_OF_BIRTH = "dateOfBirth";
    private static final String MOBILE_TYPE = "mobileType";
    private static final String HEALTH_WORKER_MOBILE = "healthWorkerMobile";
    private static final String ADDRESS = "address";
    public static final int MAX_NAME_SIZE = 255;
    private String alphabeticCharOnlyRegex = "^[A-Za-z' ]+$";
    private String alphabeticCharAndNumberRegexWithSpace = "^[A-Za-z0-9 ]+$";
    private String onlyDigitRegex = "^[0-9]{6}$";
    private String only2Digit = "^[0-9]{1,2}$";
    private String only4Digit = "^[0-9]{1,4}$";
    private static final String MOBILE_NO_10_DIGIT_REGEX_PATTERN = "[1-9]\\d{9}";

    private static final String DATE_FORMATTER = "dd-MM-yyyy";
    @Value(PropertyConstants.ENROLLMENT_DOCUMENT_PHOTO_MIN_SIZE_IN_KB)
    private String documentPhotoMinSizeLimit;

    @Value(PropertyConstants.ENROLLMENT_DOCUMENT_PHOTO_MAX_SIZE_IN_KB)
    private String documentPhotoMaxSizeLimit;

    @Autowired
    RSAUtil rsaUtil;

    public void validateEnrolByDemographic(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, RequestHeaders requestHeaders) {
        Demographic demographic = enrolByAadhaarRequestDto.getAuthData().getDemographic();
        LinkedHashMap<String, String> errors;
        errors = new LinkedHashMap<>();
        if (!isValidAadhaar(demographic.getAadhaarNumber())) {
            errors.put(AADHAAR, AbhaConstants.AADHAAR_NUMBER_INVALID);
        }
        if (!isValidGender(demographic.getGender())) {
            errors.put(GENDER, AbhaConstants.VALIDATION_ERROR_GENDER_FIELD);
        }
        validateNameAndDob(demographic, errors);
        if (!isValidPinCode(demographic.getPinCode())) {
            errors.put(PIN_CODE, AbhaConstants.INVALID_PIN_CODE);
        }
        if (!isValidState(demographic.getState())) {
            errors.put(STATE, AbhaConstants.INVALID_STATE);
        }
        if (!isValidDistrict(demographic.getDistrict())) {
            errors.put(DISTRICT, AbhaConstants.INVALID_DISTRICT);
        }
        if (!demographic.getConsentFormImage().isBlank() && !isValidConsentFormImage(demographic.getConsentFormImage())) {
            errors.put(CONSENT_FORM_IMAGE, AbhaConstants.INVALID_DOCUMENT_PHOTO_SIZE);
        } else if (!demographic.getConsentFormImage().isBlank() && !isValidConsentFormImageFormat(demographic.getConsentFormImage())) {
            errors.put(CONSENT_FORM_IMAGE, AbhaConstants.INVALID_FILE_FORMAT);
        }
        if (!isValidMobileNumber(requestHeaders, demographic.getMobile())) {
            errors.put(MOBILE, AbhaConstants.INVALID_MOBILE_NUMBER);
        }
        if (!isValidMobileType(demographic)) {
            errors.put(MOBILE_TYPE, AbhaConstants.INVALID_MOBILE_TYPE);
        }
        if (!isValidHealthWorkerMobile(demographic.getHealthWorkerMobile())) {
            errors.put(HEALTH_WORKER_MOBILE, AbhaConstants.INVALID_MOBILE_NUMBER);
        }
        if (!isValidHealthWorkerName(demographic)) {
            errors.put(AbhaConstants.HEALTH_WORKER_NAME, AbhaConstants.INVALID_HEALTH_WORKER_NAME);
        }
        if (Objects.nonNull(demographic.getAddress()) && !isValidAddress(demographic.getAddress())) {
            errors.put(ADDRESS, AbhaConstants.INVALID_ADDRESS);
        }
        if (errors.size() != 0) {
            throw new BadRequestException(errors);
        }
    }


    public void validateEnrolByDemographic(DemographicAuth demographic, RequestHeaders requestHeaders) {

        LinkedHashMap<String, String> errors;
        errors = new LinkedHashMap<>();
        if (!isValidAadhaar(demographic.getAadhaarNumber())) {
            errors.put(AADHAAR, AbhaConstants.AADHAAR_NUMBER_INVALID);
        }
        if (!isValidGender(demographic.getGender())) {
            errors.put(GENDER, AbhaConstants.VALIDATION_ERROR_GENDER_FIELD);
        }

        if (!isValidFirstName(demographic.getName())) {
            errors.put(NAME, AbhaConstants.INVALID_NAME);
        }

        if (!isNullOrEmpty(demographic.getPinCode()) && !isValidPinCode(demographic.getPinCode())) {
            errors.put(PIN_CODE, AbhaConstants.INVALID_PIN_CODE);
        }
        if (isNullOrEmpty(demographic.getStateCode()) || !isValidState(demographic.getStateCode())) {
            errors.put(STATE, AbhaConstants.INVALID_STATE);
        }
        if (isNullOrEmpty(demographic.getDistrictCode()) ||  !isValidDistrict(demographic.getDistrictCode())) {
            errors.put(DISTRICT, AbhaConstants.INVALID_DISTRICT);
        }
        if (StringUtils.isNotBlank(demographic.getProfilePhoto()) && !isValidConsentFormImage(demographic.getProfilePhoto())) {
            errors.put(CONSENT_FORM_IMAGE, AbhaConstants.INVALID_DOCUMENT_PHOTO_SIZE);
        } else if (StringUtils.isNotBlank(demographic.getProfilePhoto()) && !isValidConsentFormImageFormat(demographic.getProfilePhoto())) {
            errors.put(CONSENT_FORM_IMAGE, AbhaConstants.INVALID_FILE_FORMAT);
        }

        if (isNullOrEmpty(demographic.getDateOfBirth())
                || !isValidDateFormat(demographic.getDateOfBirth())) {
            errors.put(DATE_OF_BIRTH, AbhaConstants.INVALID_DATE_OF_BIRTH);
        }

        if (!isNullOrEmpty(demographic.getMobile()) && !isValidHealthWorkerMobile(demographic.getMobile())) {
            errors.put(MOBILE, AbhaConstants.INVALID_MOBILE_NUMBER);
        }

        if (!isNullOrEmpty(demographic.getAddress()) && !isValidAddress(demographic.getAddress())) {
            errors.put(ADDRESS, AbhaConstants.INVALID_ADDRESS);
        }
        if (errors.size() != 0) {
            throw new BadRequestException(errors);
        }
    }


    private boolean isValidHealthWorkerName(Demographic demographic) {
        return StringUtils.isEmpty(demographic.getHealthWorkerName())
                || (Common.validStringSize(demographic.getHealthWorkerName(), MAX_NAME_SIZE)
                && demographic.getHealthWorkerName().matches(alphabeticCharOnlyRegex));
    }

    private void validateNameAndDob(Demographic demographic, LinkedHashMap<String, String> errors) {
        boolean isValidMonthAndYear = true;
        if (!isValidDateOfBirth(demographic)) {
            errors.put(DAY_OF_BIRTH, AbhaConstants.INVALID_DAY_OF_BIRTH);
            isValidMonthAndYear = false;
        }
        if (!isValidMonthOfBirth(demographic)) {
            errors.put(MONTH_OF_BIRTH, AbhaConstants.INVALID_MONTH_OF_BIRTH);
            isValidMonthAndYear = false;
        }

        if (!isValidYearOfBirth(demographic)) {
            errors.put(YEAR_OF_BIRTH, AbhaConstants.INVALID_YEAR_OF_BIRTH);
            isValidMonthAndYear = false;
        }
        if (isValidMonthAndYear && !isValidDayOfBirth(demographic)) {
            errors.put(DAY_OF_BIRTH, AbhaConstants.INVALID_DOB);
        }

        if (!isValidFirstName(demographic.getFirstName())) {
            errors.put(FIRST_NAME, AbhaConstants.INVALID_FIRST_NAME);
        }
        if (!isValidMiddleName(demographic.getMiddleName())) {
            errors.put(MIDDLE_NAME, AbhaConstants.INVALID_MIDDLE_NAME);
        }
        if (!isValidLastName(demographic.getLastName())) {
            errors.put(LAST_NAME, AbhaConstants.INVALID_LAST_NAME);
        }
    }


    private boolean isValidMobileType(Demographic demographic) {
        return !demographic.getMobileType().equals(MobileType.WRONG);
    }

    private boolean isValidHealthWorkerMobile(String mobileNumber) {
        return Pattern.compile(MOBILE_NO_10_DIGIT_REGEX_PATTERN).matcher(mobileNumber).matches();
    }

    private boolean isValidMobileNumber(RequestHeaders requestHeaders, String mobile) {
        if (StringUtils.isEmpty(requestHeaders.getBenefitName()) && StringUtils.isEmpty(mobile)) {
            return true;
        } else if (!StringUtils.isEmpty(requestHeaders.getBenefitName()) && StringUtils.isEmpty(mobile)) {
            return false;
        }
        return Pattern.compile(MOBILE_NO_10_DIGIT_REGEX_PATTERN).matcher(mobile).matches();
    }


    private boolean isValidYearOfBirth(Demographic demographic) {
        return (StringUtils.isNotBlank(demographic.getYearOfBirth()) && demographic.getYearOfBirth().matches(only4Digit) && Integer.parseInt(demographic.getYearOfBirth()) <= LocalDateTime.now().getYear() && Integer.parseInt(demographic.getYearOfBirth()) >= 1900);
    }

    private boolean isValidMonthOfBirth(Demographic demographic) {
        return (StringUtils.isNotBlank(demographic.getMonthOfBirth()) && demographic.getMonthOfBirth().matches(only2Digit) && Integer.parseInt(demographic.getMonthOfBirth()) <= 12);
    }

    private boolean isValidDateOfBirth(Demographic demographic) {
        return (StringUtils.isNotBlank(demographic.getDayOfBirth()) && demographic.getDayOfBirth().matches(only2Digit) && Integer.parseInt(demographic.getDayOfBirth()) <= 31);
    }

    private boolean isValidDayOfBirth(Demographic demographic) {
        if (StringUtils.isEmpty(demographic.getDayOfBirth())) {
            return true;
        } else {
            YearMonth yearMonth = YearMonth.of(Integer.parseInt(demographic.getYearOfBirth()), Integer.parseInt(demographic.getMonthOfBirth()));
            return yearMonth.isValidDay(Integer.parseInt(demographic.getDayOfBirth()));
        }
    }

    private boolean isValidAadhaar(String aadhaar) {
        try {
            return rsaUtil.isRSAEncrypted(aadhaar) && GeneralUtils.isValidAadhaarNumber(rsaUtil.decrypt(aadhaar));
        } catch (Exception ex) {
            log.error("Invalid encryption value {}", ex.getMessage());
            return false;
        }
    }

    private boolean isValidConsentFormImage(String image) {
        double size = GeneralUtils.fileSize(image);
        return !(size < Integer.parseInt(documentPhotoMinSizeLimit)
                || size > Integer.parseInt(documentPhotoMaxSizeLimit));
    }

    private boolean isValidConsentFormImageFormat(String image) {
        return GeneralUtils.isFileFormat(image);
    }

    private boolean isValidDistrict(String district) {
        return !district.isBlank() && district.matches(alphabeticCharAndNumberRegexWithSpace);
    }

    private boolean isValidState(String state) {
        return !state.isBlank() && state.matches(alphabeticCharAndNumberRegexWithSpace);
    }

    private boolean isValidPinCode(String pinCode) {
        return !pinCode.isBlank() && pinCode.matches(onlyDigitRegex);
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    private boolean isValidLastName(String lastName) {
        return StringUtils.isEmpty(lastName)
                || !lastName.isBlank() && (Common.validStringSize(lastName, MAX_NAME_SIZE)
                && lastName.matches(alphabeticCharOnlyRegex));
    }

    private boolean isValidMiddleName(String middleName) {
        return StringUtils.isEmpty(middleName)
                || (Common.validStringSize(middleName, MAX_NAME_SIZE)
                && middleName.matches(alphabeticCharOnlyRegex));
    }

    private boolean isValidFirstName(String firstName) {
        return !firstName.isBlank() && Common.validStringSize(firstName, MAX_NAME_SIZE) && firstName.matches(alphabeticCharOnlyRegex);
    }

    private boolean isValidGender(String gender) {
        return !gender.isBlank() && (gender.equals(M) ||
                gender.equals(F) ||
                gender.equals(O));
    }

    private boolean isValidAddress(String address) {
        return !address.isBlank() &&
                address.matches(AbhaConstants.ADDRESS_VALIDATOR_REGEX);
    }




    private  boolean isValidDateFormat_(String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        Mono<Boolean> isValidFormatMono = Mono.defer(() -> {
            boolean yearOfBrith = !StringUtils.isEmpty(value) && value.matches("[0-9]+") && value.length() == 4;
            if (yearOfBrith) {
                return Mono.just(true); // If it's a 4-digit number, consider it valid
            } else {
                return validateDateFormat(value, DATE_FORMATTER)
                        .onErrorResume(ex -> Mono.just(false));
            }
        }).subscribeOn(Schedulers.parallel());
        return  isValidFormatMono.block();
    }

    private  Mono<Boolean> validateDateFormat(String value, String format) {
        return Mono.fromCallable(() -> {
                    SimpleDateFormat sdf = new SimpleDateFormat(format);
                    sdf.setLenient(false);
                    Date date = sdf.parse(value);
                    return value.equals(sdf.format(date));
                })
                .onErrorReturn(false);
    }

    private static boolean isValidDateFormat(String value) {
        boolean isNumeric = !StringUtils.isEmpty(value) && value.matches("[0-9]+") && value.length() == 4;
        if (isNumeric) {
            return true;
        } else {
            // Otherwise, try to validate against the specified date format
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMATTER);
                sdf.setLenient(false);
                Date date = sdf.parse(value);
                return value.equals(sdf.format(date));
            } catch (ParseException ex) {
                return false;
            }
        }
    }
}




