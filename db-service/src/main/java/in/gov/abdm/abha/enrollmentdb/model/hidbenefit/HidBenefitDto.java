package in.gov.abdm.abha.enrollmentdb.model.hidbenefit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class HidBenefitDto {

    @Id
    private String hidBenefitId;
    private String programName;
    private String benefitName;
    private String benefitId;
    private String stateCode;
    private LocalDateTime validTill;
    private LocalDateTime linkedDate;
    private String linkedBy;
    private String healthIdNumber;
    private String mobileNumber;
    private Integer status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String createdBy;
    private String updatedBy;
}
