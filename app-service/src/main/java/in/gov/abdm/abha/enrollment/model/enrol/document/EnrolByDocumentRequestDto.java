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

    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_TRANSACTION_FIELD)
    private String txnId;

    @NotEmpty(message = AbhaConstants.INVALID_DOCUMENT_TYPE)
    private String documentType;

    @NotEmpty(message = AbhaConstants.INVALID_DOCUMENT_ID)
    private String documentId;

    @NotEmpty(message = AbhaConstants.INVALID_FIRST_NAME)
    private String firstName;

    private String middleName;

    @NotEmpty(message = AbhaConstants.INVALID_LAST_NAME)
    private String lastName;

    @NotEmpty(message = AbhaConstants.INVALID_DOB)
    private String dob;

    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_GENDER_FIELD)
    private String gender;

    @NotEmpty(message = AbhaConstants.INVALID_FRONT_SIDE_PHOTO)
    private String frontSidePhoto;

    @NotEmpty(message = AbhaConstants.INVALID_BACK_SIDE_PHOTO)
    private String backSidePhoto;

    @NotEmpty(message = AbhaConstants.INVALID_ADDRESS)
    private String address;

    @NotEmpty(message = AbhaConstants.INVALID_STATE)
    private String state;

    @NotEmpty(message = AbhaConstants.INVALID_DISTRICT)
    private String district;

    @NotEmpty(message = AbhaConstants.INVALID_PIN_CODE)
    private String pinCode;
}
