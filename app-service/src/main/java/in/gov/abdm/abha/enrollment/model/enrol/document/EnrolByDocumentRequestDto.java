package in.gov.abdm.abha.enrollment.model.enrol.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrolByDocumentRequestDto {
    private String txnId;
    private String documentType;
    private String documentId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String dob;
    private String gender;
    private String frontSidePhoto;
    private String backSidePhoto;
    private String address;
    private String state;
    private String district;
    private String pinCode;
}
