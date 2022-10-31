package in.gov.abdm.abha.enrollmentdb.model.dependent_account_relationship;

/**
 * It's a Data Transfer Object for Dependent_account_relationship
 */
public class Dependent_account_relationship_Dto {

    /**
     *  it is Id and Primary key
     */
    
    private Long id;

    /**
     * it is a parent_health_id_number it is a Foreign key
     */
    private String parent_health_id_number;

    /**
     *  it is dependent_health_id_number for child ABHA
     */
    private  String dependent_health_id_number;

    /**
     *  It is a relation with child
     */
    private String related_as;

    /**
     *  It is a relationship_proof_document_location
     */
    private String relationship_proof_document_location;

    //TODO : audit columns

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
    private String createdAt;

    /**
     * It is UpdatedAt
     */
    private String updatedAt;
}
