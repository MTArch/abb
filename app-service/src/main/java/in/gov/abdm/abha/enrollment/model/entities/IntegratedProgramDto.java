package in.gov.abdm.abha.enrollment.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntegratedProgramDto implements Serializable {
    private String id;
    private String integratedProgramId;
    private String programName;
    private String benefitName;
    private String clientId;
    private String endPointUrlSearch;
    private String endPointUrlLink;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String createdBy;
    private String updatedBy;
}
