package in.gov.abdm.abha.enrollmentdb.model.hid_phr_address;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;

/**
 * It's a Hid Phr Address POJO class
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HidPhrAddress implements Persistable<Long> {

    /**
     *  it is Id and Primary key
     */

    @Id
    private Long hidPhrAddressId;

    /**
     * it is a health_id_number it is a Foreign key
     */
    private String healthIdNumber;

    /**
     * it is a Hid phr_address
     */
    private String phrAddress;

    /**
     * it is status of phr address
     */
    private String status;

    /**
     * it is preferred
     */
    private Integer preferred;

    /**
     * it is last modified by
     */
    private String lastModifiedBy;

    /**
     * it is last modified date
     */
    private LocalDateTime lastModifiedDate;

    /**
     * it is has migrated
     */
    private String hasMigrated;

    /**
     * it is created by
     */
    private String createdBy;

    /**
     * it is created date
     */
    private LocalDateTime createdDate;

    /**
     * it is linked
     */
    private Integer linked;

    /**
     * it is cm migrated
     */
    private Integer cmMigrated;

    @Transient
    private boolean isNewHidPhrAddress;


    @Override
    public Long getId() {
        return this.getHidPhrAddressId();
    }

    @Override
    @Transient
    public boolean isNew() {
        return this.isNewHidPhrAddress;
    }

    /**
     * setAsNew() method sets an entity as a new record.
     */
    public HidPhrAddress setAsNew() {
        isNewHidPhrAddress = true;
        return this;
    }
}
