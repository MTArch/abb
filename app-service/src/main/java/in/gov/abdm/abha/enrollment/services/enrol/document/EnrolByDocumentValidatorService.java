package in.gov.abdm.abha.enrollment.services.enrol.document;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.exception.application.BadRequestException;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentRequestDto;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import static in.gov.abdm.abha.enrollment.constants.PropertyConstants.*;

@Service
@Slf4j
public class EnrolByDocumentValidatorService {

    public static final String TXN_ID = "TxnId";
    private static final String DRIVING_LICENCE = "DRIVING_LICENCE";
    private static final String M = "M";
    private static final String F = "F";
    private static final String O = "O";
    private static final String DOCUMENT_TYPE = "DocumentType";
    private static final String DOB = "Dob";
    private static final String GENDER = "Gender";
    private static final String FIRST_NAME = "FirstName";
    private static final String MIDDLE_NAME = "MiddleName";
    private static final String LAST_NAME = "LastName";
    private static final String PIN_CODE = "PinCode";
    private static final String STATE = "State";
    private static final String DISTRICT = "District";
    private static final String FRONT_SIDE_PHOTO = "FrontSidePhoto";
    private static final String BACK_SIDE_PHOTO = "BackSidePhoto";
    private static final String CONSENT = "Consent";
    public static final int MAX_NAME_SIZE = 255;
    private String txnIdRegex = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";
    private String dobRegex = "^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$";
    private String alphabeticCharOnlyRegex = "^[A-Za-z' ]+$";
    private String alphabeticCharOnlyRegexWithSpace = "^[A-Za-z ]+$";
    private String onlyDigitRegex = "^[0-9]{6}$";
    private String base64Regex = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$";


    @Value(ENROLLMENT_PHOTO_MIN_SIZE_IN_KB)
    private String photoMinSizeLimit;

    @Value(ENROLLMENT_PHOTO_MAX_SIZE_IN_KB)
    private String photoMaxSizeLimit;

    @Value(ENROLLMENT_DOCUMENT_PHOTO_MIN_SIZE_IN_KB)
    private String documentPhotoMinSizeLimit;

    @Value(ENROLLMENT_DOCUMENT_PHOTO_MAX_SIZE_IN_KB)
    private String documentPhotoMaxSizeLimit;

    public void validateEnrolByDocument(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        LinkedHashMap<String, String> errors;
        errors = new LinkedHashMap<>();

        isValidTxnId(enrolByDocumentRequestDto,errors);

        isValidDocumentType(enrolByDocumentRequestDto,errors);

        if (!isValidDob(enrolByDocumentRequestDto)) {
            errors.put(DOB, AbhaConstants.INVALID_DOB);
        }
        if (!isValidGender(enrolByDocumentRequestDto)) {
            errors.put(GENDER, AbhaConstants.VALIDATION_ERROR_GENDER_FIELD);
        }
        if (!isValidFirstName(enrolByDocumentRequestDto)) {
            errors.put(FIRST_NAME, AbhaConstants.INVALID_FIRST_NAME);
        }
        if (!isValidMiddleName(enrolByDocumentRequestDto)) {
            errors.put(MIDDLE_NAME, AbhaConstants.INVALID_MIDDLE_NAME);
        }
        if (!isValidLastName(enrolByDocumentRequestDto)) {
            errors.put(LAST_NAME, AbhaConstants.INVALID_LAST_NAME);
        }
        if (!isValidPinCode(enrolByDocumentRequestDto)) {
            errors.put(PIN_CODE, AbhaConstants.INVALID_PIN_CODE);
        }
        if (!isValidState(enrolByDocumentRequestDto)) {
            errors.put(STATE, AbhaConstants.INVALID_STATE);
        }
        if (!isValidDistrict(enrolByDocumentRequestDto)) {
            errors.put(DISTRICT, AbhaConstants.INVALID_DISTRICT);
        }
        if (!isValidFrontSidePhotoFormat(enrolByDocumentRequestDto)) {
            errors.put(FRONT_SIDE_PHOTO, AbhaConstants.INVALID_PHOTO_FORMAT);
        } else if (!isValidFrontSidePhotoSize(enrolByDocumentRequestDto)) {
            errors.put(FRONT_SIDE_PHOTO, AbhaConstants.INVALID_DOCUMENT_PHOTO_SIZE);
        }
        if (!isValidBackSidePhotoFormat(enrolByDocumentRequestDto)) {
            errors.put(BACK_SIDE_PHOTO, AbhaConstants.INVALID_PHOTO_FORMAT);
        } else if (!isValidBackSidePhotoSize(enrolByDocumentRequestDto)) {
            errors.put(BACK_SIDE_PHOTO, AbhaConstants.INVALID_DOCUMENT_PHOTO_SIZE);
        }
        if(isSameFrontSideBackSidePhoto(enrolByDocumentRequestDto)){
            errors.put(BACK_SIDE_PHOTO, AbhaConstants.SAME_PHOTO_EXCEPTION);
        }
        if (enrolByDocumentRequestDto.getConsent() == null) {
            errors.put(CONSENT, AbhaConstants.VALIDATION_ERROR_CONSENT_FIELD);
        }
        if (!isValidBase64(enrolByDocumentRequestDto.getFrontSidePhoto())) {
            errors.put(FRONT_SIDE_PHOTO, AbhaConstants.INVALID_PHOTO_FORMAT);
        }
        if (!isValidBase64(enrolByDocumentRequestDto.getBackSidePhoto())) {
            errors.put(BACK_SIDE_PHOTO, AbhaConstants.INVALID_PHOTO_FORMAT);
        }
        if (errors.size() != 0) {
            throw new BadRequestException(errors);
        }
    }

    private boolean isValidBase64(String value) {
        return Pattern.matches(base64Regex,value);
    }

    private void isValidTxnId(EnrolByDocumentRequestDto enrolByDocumentRequestDto,  LinkedHashMap<String, String> errors){
        if (!isValidTxnId(enrolByDocumentRequestDto)) {
            errors.put(TXN_ID, AbhaConstants.VALIDATION_ERROR_TRANSACTION_FIELD);
        }
    }

    private void isValidDocumentType(EnrolByDocumentRequestDto enrolByDocumentRequestDto, LinkedHashMap<String, String> errors){
        if (!isValidDocumentType(enrolByDocumentRequestDto)) {
            errors.put(DOCUMENT_TYPE, AbhaConstants.INVALID_DOCUMENT_TYPE);
        }
    }
    private boolean isSameFrontSideBackSidePhoto(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        if(isValidBase64(enrolByDocumentRequestDto.getBackSidePhoto()) && isValidBase64(enrolByDocumentRequestDto.getFrontSidePhoto())) {
            byte[] imageBytes1 = Common.base64Decode(enrolByDocumentRequestDto.getFrontSidePhoto());
            byte[] imageBytes2 = Common.base64Decode(enrolByDocumentRequestDto.getBackSidePhoto());
            return Arrays.equals(imageBytes1, imageBytes2);
        }
        return false;
    }

    private boolean isValidFrontSidePhotoSize(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        double size = GeneralUtils.fileSize(enrolByDocumentRequestDto.getFrontSidePhoto());
        return !(size < Integer.parseInt(documentPhotoMinSizeLimit)
                || size > Integer.parseInt(documentPhotoMaxSizeLimit));
    }

    private boolean isValidFrontSidePhotoFormat(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        return GeneralUtils.isImageFileFormat(enrolByDocumentRequestDto.getFrontSidePhoto());
    }

    private boolean isValidBackSidePhotoSize(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        double size = GeneralUtils.fileSize(enrolByDocumentRequestDto.getBackSidePhoto());
        return !(size < Integer.parseInt(documentPhotoMinSizeLimit)
                || size > Integer.parseInt(documentPhotoMaxSizeLimit));
    }

    private boolean isValidBackSidePhotoFormat(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        return GeneralUtils.isImageFileFormat(enrolByDocumentRequestDto.getBackSidePhoto());
    }

    private boolean isValidDistrict(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        return enrolByDocumentRequestDto.getDistrict().matches(alphabeticCharOnlyRegexWithSpace);
    }

    private boolean isValidState(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        return enrolByDocumentRequestDto.getState().matches(alphabeticCharOnlyRegexWithSpace);
    }

    private boolean isValidPinCode(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        return enrolByDocumentRequestDto.getPinCode().matches(onlyDigitRegex);
    }

    private boolean isValidLastName(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        return Common.validStringSize(enrolByDocumentRequestDto.getLastName(), MAX_NAME_SIZE) && enrolByDocumentRequestDto.getLastName().matches(alphabeticCharOnlyRegex);
    }

    private boolean isValidMiddleName(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        return StringUtils.isEmpty(enrolByDocumentRequestDto.getMiddleName())
                || (Common.validStringSize(enrolByDocumentRequestDto.getMiddleName(), MAX_NAME_SIZE)
                && enrolByDocumentRequestDto.getMiddleName().matches(alphabeticCharOnlyRegex));
    }

    private boolean isValidFirstName(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        return Common.validStringSize(enrolByDocumentRequestDto.getFirstName(), MAX_NAME_SIZE) && enrolByDocumentRequestDto.getFirstName().matches(alphabeticCharOnlyRegex);
    }

    private boolean isValidDocumentType(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        return enrolByDocumentRequestDto.getDocumentType().equals(DRIVING_LICENCE);
    }

    private boolean isValidDob(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        try {
            if (Pattern.compile(dobRegex).matcher(enrolByDocumentRequestDto.getDob()).matches()) {
                LocalDate inputDob = LocalDate.parse(enrolByDocumentRequestDto.getDob());
                LocalDate today = LocalDate.now();

                if (inputDob.getYear() != 0) {

                    int diff = today.compareTo(inputDob);
                    if (diff > 0 || diff == 0) {
                        return true;
                    } else if (diff < 0) {
                        return false;
                    }
                } else {
                    return false;
                }
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    private boolean isValidGender(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        return enrolByDocumentRequestDto.getGender().equals(M) ||
                enrolByDocumentRequestDto.getGender().equals(F) ||
                enrolByDocumentRequestDto.getGender().equals(O);
    }

    private boolean isValidTxnId(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        return Pattern.compile(txnIdRegex).matcher(enrolByDocumentRequestDto.getTxnId()).matches();
    }
}
