package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.model.link.parent.request.LinkParentRequestDto;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidParentAbhaRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ParentAbhaRequestValidator implements ConstraintValidator<ValidParentAbhaRequest, LinkParentRequestDto> {


    @Override
	public boolean isValid(LinkParentRequestDto linkParentRequestDto, ConstraintValidatorContext context) {
		return linkParentRequestDto.getParentAbhaRequestDtoList() != null && !linkParentRequestDto.getParentAbhaRequestDtoList().isEmpty();
	}
}
