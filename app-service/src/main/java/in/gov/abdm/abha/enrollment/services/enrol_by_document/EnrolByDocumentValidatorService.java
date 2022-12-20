package in.gov.abdm.abha.enrollment.services.enrol_by_document;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.exception.application.GenericExceptionMessage;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentRequestDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.regex.Pattern;

@Service
public class EnrolByDocumentValidatorService {

    private String TxnId = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";

    private String Dob = "^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$";

    public void validateEnrolByDocument(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {

        if (!isValidTxnId(enrolByDocumentRequestDto)) {
            throw new GenericExceptionMessage(AbhaConstants.VALIDATION_TXN_ID_FIELD_ERROR);
        } else if (!isValidDocumentType(enrolByDocumentRequestDto)) {
            throw new GenericExceptionMessage(AbhaConstants.VALIDATION_DOCUMENT_TYPE_FIELD_ERROR);
        } else if (!isValidDob(enrolByDocumentRequestDto)) {
            throw new GenericExceptionMessage(AbhaConstants.VALIDATION_DOB_FIELD_ERROR);
        } else if (!isValidGender(enrolByDocumentRequestDto)) {
            throw new GenericExceptionMessage(AbhaConstants.VALIDATION_GENDER_FIELD_ERROR);
        }

    }

    private boolean isValidDocumentType(EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        if (enrolByDocumentRequestDto.getDocumentType().equals("DRIVING_LICENCE")) {
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
        if (enrolByDocumentRequestDto.getGender().equals("M") ||
                enrolByDocumentRequestDto.getGender().equals("F") ||
                enrolByDocumentRequestDto.getGender().equals("O")) {
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
