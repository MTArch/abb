package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.enums.link.parent.Relationship;
import in.gov.abdm.abha.enrollment.model.link.parent.request.ParentAbhaRequestDto;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidRelationship;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RelationshipValidator implements ConstraintValidator<ValidRelationship, ParentAbhaRequestDto> {

    @Override
    public boolean isValid(ParentAbhaRequestDto parentAbhaRequestDto, ConstraintValidatorContext context) {

        if (!StringUtils.isEmpty(parentAbhaRequestDto) &&
                parentAbhaRequestDto != null ) {
            return Relationship.isValid(String.valueOf(parentAbhaRequestDto.getRelationship()));

        }
        return true;
    }

}
