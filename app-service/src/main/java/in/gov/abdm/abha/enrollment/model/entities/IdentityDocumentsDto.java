package in.gov.abdm.abha.enrollment.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdentityDocumentsDto {

    private String id;

    private String healthIdNumber;

    private String documentNumber;

    private String documentType;

    private String firstName;

    private String middleName;

    private String lastName;

    private String dob;

    private String photo;

    private String photoBack;

    private String gender;

    private String status;

    private String createdBy;

    private LocalDateTime createdDate;

    private String modifiedBy;

    private LocalDateTime modifiedDate;
}
