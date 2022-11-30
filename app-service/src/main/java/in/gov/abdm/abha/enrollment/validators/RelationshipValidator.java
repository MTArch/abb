package in.gov.abdm.abha.enrollment.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.gov.abdm.abha.enrollment.enums.link.parent.Relationship;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidRelationship;

public class RelationshipValidator implements ConstraintValidator<ValidRelationship, Relationship> {

    @Override
	public boolean isValid(Relationship relationship, ConstraintValidatorContext context) {

		return  Relationship.isValid(String.valueOf(relationship));

	}

}
