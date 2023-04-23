package in.gov.abdm.abha.enrollmentdb.model.hidbenefit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HidBenefit implements Persistable<String> {
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

    @Transient
    private boolean isNewHidBenefit;

    @Override
    public String getId() {
        return this.getHidBenefitId();
    }

    @Override
    @Transient
    public boolean isNew() {
        return this.isNewHidBenefit || hidBenefitId == null ;
    }

    public HidBenefit setAsNew() {
        isNewHidBenefit = true;
        return this;
    }

}
