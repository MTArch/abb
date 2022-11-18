package in.gov.abdm.abha.enrollment.services.database.dependent_account_relationship;

import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.DependentAccountRelationshipDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.link.parent.request.LinkParentRequestDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface DependentAccountRelationshipService {

//    AccountDto prepareUpdateAccount(TransactionDto transactionDto, LinkParentRequestDto linkParentRequestDto);

//    Mono<DependentAccountRelationshipDto> createDependentAccountEntity(DependentAccountRelationshipDto dependentAccountRelationshipDto);
    Mono<DependentAccountRelationshipDto> createDependentAccountEntity(List<DependentAccountRelationshipDto> dependentAccountRelationshipList);

//    DependentAccountRelationshipDto prepareDependentAccount(LinkParentRequestDto linkParentRequestDto, AccountDto accountDto);
   // List<DependentAccountRelationshipDto> prepareDependentAccount(LinkParentRequestDto linkParentRequestDto, AccountDto accountDto);

    List<DependentAccountRelationshipDto> prepareDependentAccount(LinkParentRequestDto linkParentRequestDto);
}
