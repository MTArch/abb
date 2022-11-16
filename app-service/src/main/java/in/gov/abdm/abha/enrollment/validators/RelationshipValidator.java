package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.model.link.parent.request.ParentAbhaRequestDto;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidRelationship;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RelationshipValidator implements ConstraintValidator<ValidRelationship, ParentAbhaRequestDto> {
    @Override
    public boolean isValid(ParentAbhaRequestDto value, ConstraintValidatorContext context) {
        return false;
    }
}
