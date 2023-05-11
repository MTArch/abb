package in.gov.abdm.abha.enrollmentdb.model.integrated_program;

import in.gov.abdm.abha.enrollmentdb.model.account.Accounts;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntegratedProgram implements Persistable<String> {
    @Id
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

    @Transient
    private boolean isNewIntegratedProgram;

    @Override
    public String getId() {
        return this.getIntegratedProgramId();
    }

    @Override
    @Transient
    public boolean isNew() {
        return this.isNewIntegratedProgram || integratedProgramId == null;
    }

    public IntegratedProgram setAsNew() {
        isNewIntegratedProgram = true;
        return this;
    }
}
