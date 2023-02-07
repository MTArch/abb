package in.gov.abdm.abha.enrollment.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountActions implements Persistable<String> {
    @Id
    private BigInteger id;
    private String action;
    @CreatedDate
    private LocalDateTime createdDate;
    private String field;
    private String healthIdNumber;
    private String newValue;
    private String previousValue;
    private String reactivationDate;
    private String reason;
    private String reasons;

    @Transient
    private boolean isNewAccount;

    @Override
    @Transient
    public boolean isNew() {
        return this.isNewAccount || healthIdNumber == null;
    }

    /**
     * setAsNew() method sets an entity as a new record.
     */
    public AccountActions setAsNew() {
        isNewAccount = true;
        return this;
    }

    @Override
    public String getId() {
        return this.getHealthIdNumber();
    }

}
