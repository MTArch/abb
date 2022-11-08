package in.gov.abdm.abha.enrollment.model.entities;

import java.time.LocalDateTime;

/**
 * It's a Data Transfer Object for Dependent_account_relationship
 */
public class DependentAccountRelationshipDto {

    /**
     *  it is Id and Primary key
     */
    
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
}
