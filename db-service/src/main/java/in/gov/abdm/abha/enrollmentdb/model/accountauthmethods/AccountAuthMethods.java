package in.gov.abdm.abha.enrollmentdb.model.accountauthmethods;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountAuthMethods implements Persistable<String> {

    private String healthIdNumber;

    private String authMethods;

    @Transient
    private boolean isNewAccountAuthMethods;

    @Override
    public String getId() {
        return this.healthIdNumber;
    }

    @Override
    @Transient
    public boolean isNew() {
        return this.isNewAccountAuthMethods;
    }

    /**
     * setAsNew() method sets an entity as a new record.
     */
    public AccountAuthMethods setAsNew() {
        isNewAccountAuthMethods = true;
        return this;
    }
}
