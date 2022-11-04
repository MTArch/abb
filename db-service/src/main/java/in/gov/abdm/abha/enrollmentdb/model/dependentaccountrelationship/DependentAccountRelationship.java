package in.gov.abdm.abha.enrollmentdb.model.dependentaccountrelationship;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;

/**
 * It's a Dependent_account_relationship POJO class
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DependentAccountRelationship implements Persistable<Long> {

    /**
     *  it is Id and Primary key
     */

    @Id
    private Long id;

    /**
     * it is a parent_health_id_number it is a Foreign key
     */
    private String parentHealthIdNumber;

    /**
     *  it is dependent_health_id_number for child ABHA
     */
    private  String dependentHealthIdNumber;

    /**
     *  It is a relation with child
     */
    private String relatedAs;

    /**
     *  It is a relationship_proof_document_location
     */
    private String relationshipProofDocumentLocation;
    /**
     * It is createdBy
     */
    private String createdBy;

    /**
     * It is UpdatedBy
     */
    private String updatedBy;
    /**
     * It is createdAt
     */
    private LocalDateTime createdDate;

    /**
     * It is UpdatedAt
     */
    private LocalDateTime updatedDate;

    @Override
    public boolean isNew() {
        return false;
    }
}
