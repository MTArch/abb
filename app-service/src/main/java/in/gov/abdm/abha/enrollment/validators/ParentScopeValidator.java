package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.model.link.parent.request.LinkParentRequestDto;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidParentScope;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ParentScopeValidator implements ConstraintValidator<ValidParentScope, LinkParentRequestDto> {

    @Override
    public boolean isValid(LinkParentRequestDto linkParentRequestDto, ConstraintValidatorContext context) {
        return linkParentRequestDto.getScope() != null
                && !linkParentRequestDto.getScope().isEmpty();
    }
}

