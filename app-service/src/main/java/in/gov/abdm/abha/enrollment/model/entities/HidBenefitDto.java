package in.gov.abdm.abha.enrollment.model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HidBenefitDto {

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
    private int status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String createdBy;
    private String updatedBy;
}
