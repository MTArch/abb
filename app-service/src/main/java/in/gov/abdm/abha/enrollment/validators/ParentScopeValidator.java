package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.link.parent.request.LinkParentRequestDto;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidParentScope;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ParentScopeValidator implements ConstraintValidator<ValidParentScope, LinkParentRequestDto> {

    @Override
    public boolean isValid(LinkParentRequestDto linkParentRequestDto, ConstraintValidatorContext context) {
        List<Scopes> requestScopes = linkParentRequestDto.getScope();
        List<Scopes> enumNames = Stream.of(Scopes.values())
                .filter(name -> name.equals(Scopes.PARENT_ABHA_LINK))
                .collect(Collectors.toList());
        return requestScopes != null &&  !requestScopes.isEmpty() && Common.isAllScopesAvailable(enumNames, requestScopes);
    }
}

