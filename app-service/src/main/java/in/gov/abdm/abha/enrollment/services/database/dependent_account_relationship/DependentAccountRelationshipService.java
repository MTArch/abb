package in.gov.abdm.abha.enrollment.services.database.dependent_account_relationship;

import java.util.List;

import in.gov.abdm.abha.enrollment.model.entities.DependentAccountRelationshipDto;
import in.gov.abdm.abha.enrollment.model.link.parent.request.LinkParentRequestDto;
import reactor.core.publisher.Mono;

public interface DependentAccountRelationshipService {

    Mono<DependentAccountRelationshipDto> createDependentAccountEntity(List<DependentAccountRelationshipDto> dependentAccountRelationshipList);

    List<DependentAccountRelationshipDto> prepareDependentAccount(LinkParentRequestDto linkParentRequestDto);
}
