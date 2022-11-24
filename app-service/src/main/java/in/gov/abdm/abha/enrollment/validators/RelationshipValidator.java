package in.gov.abdm.abha.enrollment.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.gov.abdm.abha.enrollment.enums.link.parent.Relationship;
import in.gov.abdm.abha.enrollment.model.link.parent.request.ParentAbhaRequestDto;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidRelationship;

public class RelationshipValidator implements ConstraintValidator<ValidRelationship, ParentAbhaRequestDto> {

    @Override
	public boolean isValid(ParentAbhaRequestDto parentAbhaRequestDto, ConstraintValidatorContext context) {

		return parentAbhaRequestDto != null
				&& Relationship.isValid(String.valueOf(parentAbhaRequestDto.getRelationship()));

	}

}
