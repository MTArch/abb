package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.model.link.parent.request.LinkParentRequestDto;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidChildAbhaRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ChildAbhaRequestValidator implements ConstraintValidator<ValidChildAbhaRequest, LinkParentRequestDto> {

    @Override
    public boolean isValid(LinkParentRequestDto value, ConstraintValidatorContext context) {
        return false;
    }
}
