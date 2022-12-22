package in.gov.abdm.abha.enrollment.services.enrol_by_document;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.exception.application.BadRequestException;
import in.gov.abdm.abha.enrollment.exception.application.GenericExceptionMessage;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentRequestDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private String TxnId = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";
    private String Dob = "^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$";

    private LinkedHashMap<String, String> errors;

    public void validateEnrolByDocument(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        errors = new LinkedHashMap<>();
        if (!isValidTxnId(enrolByDocumentRequestDto)) {
            errors.put(TXN_ID, AbhaConstants.VALIDATION_TXN_ID_FIELD_ERROR);
        }
        if (!isValidDocumentType(enrolByDocumentRequestDto)) {
            errors.put(DOCUMENT_TYPE, AbhaConstants.VALIDATION_DOCUMENT_TYPE_FIELD_ERROR);
        }
        if (!isValidDob(enrolByDocumentRequestDto)) {
            errors.put(DOB, AbhaConstants.VALIDATION_DOB_FIELD_ERROR);
        }
        if (!isValidGender(enrolByDocumentRequestDto)) {
            errors.put(GENDER, AbhaConstants.VALIDATION_GENDER_FIELD_ERROR);
        }

        if (errors.size() != 0) {
            throw new BadRequestException(errors);
        }
    }

    private boolean isValidDocumentType(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        if (enrolByDocumentRequestDto.getDocumentType().equals(DRIVING_LICENCE)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidDob(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
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
