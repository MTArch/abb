package in.gov.abdm.abha.enrollment.model.enrol.document;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrolByDocumentRequestDto {

    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_TRANSACTION_ID_FIELD)
    private String txnId;

    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_DOCUMENT_TYPE_FIELD)
    private String documentType;

    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_DOCUMENT_ID_FIELD)
    private String documentId;

    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_FIRST_NAME_FIELD)
    private String firstName;

    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_MIDDLE_NAME_FIELD)
    private String middleName;

    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_LAST_NAME_FIELD)
    private String lastName;

    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_DOB_FIELD)
    private String dob;

    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_OF_GENDER_FIELD)
    private String gender;

    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_FRONT_SIDE_PHOTO_FIELD)
    private String frontSidePhoto;

    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_BACK_SIDE_PHOTO_FIELD)
    private String backSidePhoto;

    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_ADDRESS_FIELD)
    private String address;

    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_STATE_FIELD)
    private String state;

    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_DISTRICT_FIELD)
    private String district;
<<<<<<< HEAD
=======

    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_PINCODE_FIELD)
>>>>>>> d125c63 (subtask-abha-CAI-3-dl-validations completed.)
    private String pinCode;
}
