package in.gov.abdm.abha.enrollment.services.enrol.document;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.exception.application.BadRequestException;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentRequestDto;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

@Service
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
    private static final String ADDRESS = "Address";
    private static final String PIN_CODE = "PinCode";
    private static final String STATE = "State";
    private static final String DISTRICT = "District";
    private static final String FRONT_SIDE_PHOTO = "FrontSidePhoto";
    private static final String BACK_SIDE_PHOTO = "BackSidePhoto";
    private static final String CONSENT = "Consent";
    public static final int MAX_NAME_SIZE = 255;
    private String TxnId = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";
    private String Dob = "^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$";
    private String alphabeticCharOnlyRegex = "^[A-Za-z' ]+$";
    private String alphabeticCharOnlyRegexWithSpace = "^[A-Za-z ]+$";
    private String onlyDigitRegex = "^[0-9]{6}$";

    private LinkedHashMap<String, String> errors;

    @Value("${enrollment.photo.minSizeInKB}")
    private String photoMinSizeLimit;

    @Value("${enrollment.photo.maxSizeInKB}")
    private String photoMaxSizeLimit;

    @Value("${enrollment.documentPhoto.minSizeInKB}")
    private String documentPhotoMinSizeLimit;

    @Value("${enrollment.documentPhoto.maxSizeInKB}")
    private String documentPhotoMaxSizeLimit;

    public void validateEnrolByDocument(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        errors = new LinkedHashMap<>();
        if (!isValidTxnId(enrolByDocumentRequestDto)) {
            errors.put(TXN_ID, AbhaConstants.VALIDATION_ERROR_TRANSACTION_FIELD);
        }
        if (!isValidDocumentType(enrolByDocumentRequestDto)) {
            errors.put(DOCUMENT_TYPE, AbhaConstants.INVALID_DOCUMENT_TYPE);
        }
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
        if (errors.size() != 0) {
            throw new BadRequestException(errors);
        }
    }

    private boolean isSameFrontSideBackSidePhoto(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        byte[] imageBytes1 = Common.base64Decode(enrolByDocumentRequestDto.getFrontSidePhoto());
        byte[] imageBytes2 = Common.base64Decode(enrolByDocumentRequestDto.getBackSidePhoto());
        return Arrays.equals(imageBytes1, imageBytes2);
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
        if (enrolByDocumentRequestDto.getDocumentType().equals(DRIVING_LICENCE)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidDob(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        try {
            if (Pattern.compile(Dob).matcher(enrolByDocumentRequestDto.getDob()).matches()) {
                LocalDate DOB = LocalDate.parse(enrolByDocumentRequestDto.getDob());
                LocalDate today = LocalDate.now();

                if (DOB.getYear() != 0) {

                    int diff = today.compareTo(DOB);
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
            return false;
        }
    }

    private boolean isValidGender(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        if (enrolByDocumentRequestDto.getGender().equals(M) ||
                enrolByDocumentRequestDto.getGender().equals(F) ||
                enrolByDocumentRequestDto.getGender().equals(O)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidTxnId(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        if (Pattern.compile(TxnId).matcher(enrolByDocumentRequestDto.getTxnId()).matches()) {
            return true;
        } else {
            return false;
        }
    }
}
