package in.gov.abdm.abha.enrollment.model.entities;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * It's a Data Transfer Object for hid_phr_address
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HidPhrAddressDto {


    /**
     * it is Id and Primary key
     */

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

    /**
     * isNewTemplate of boolean type that stores the state of an entity object.
     */
    private boolean isNewHidPhrAddress;

}
