package in.gov.abdm.abha.enrollment.model.enrol.document;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.ConsentDto;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidDLNumber;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrolByDocumentRequestDto {

    @NotEmpty(message = AbhaConstants.VALIDATION_ERROR_TRANSACTION_FIELD)
    private String txnId;

    @NotEmpty(message = AbhaConstants.INVALID_DOCUMENT_TYPE)
    private String documentType;

    @NotEmpty(message = AbhaConstants.INVALID_DOCUMENT_ID)
    @ValidDLNumber
    private String documentId;
    
    @NotEmpty(message = AbhaConstants.INVALID_FIRST_NAME)
    @Size(max = 255,message = FIRST_NAME_EXCEED)
    private String firstName;

    @Size(max = 255,message = MIDDLE_NAME_EXCEED)
    private String middleName;

    @NotEmpty(message = AbhaConstants.INVALID_LAST_NAME)
    @Size(max = 255,message = LAST_NAME_EXCEED)
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
    @Size(max = 255,message = ADDRESS_EXCEED)
    private String address;

    @NotEmpty(message = AbhaConstants.INVALID_STATE)
    private String state;

    @NotEmpty(message = AbhaConstants.INVALID_DISTRICT)
    private String district;

    @NotEmpty(message = AbhaConstants.INVALID_PIN_CODE)
    private String pinCode;

    @Valid
    ConsentDto consent;
}
