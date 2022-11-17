package in.gov.abdm.abha.enrollment.services.link.parent.impl;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.enums.childabha.AbhaType;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.DependentAccountRelationshipDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.model.link.parent.request.LinkParentRequestDto;
import in.gov.abdm.abha.enrollment.model.link.parent.response.LinkParentResponseDto;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.dependent.account.relationship.DependentAccountRelationshipService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.link.parent.LinkParentService;
import reactor.core.publisher.Mono;

@Service
public class LinkParentServiceImpl implements LinkParentService {

    @Autowired
    TransactionService transactionService;
    @Autowired
    AccountService accountService;

    @Autowired
    DependentAccountRelationshipService dependentAccountRelationshipService;

//    @Override
//    public Mono<LinkParentResponseDto> linkDependentAccount(LinkParentRequestDto linkParentRequestDto) {
//
//        Mono<TransactionDto> transactionDtoMono = transactionService.findTransactionDetailsFromDB(linkParentRequestDto.getTxnId());
//
//        return transactionDtoMono.flatMap(res->
//        {
//            AccountDto accountDto = dependentAccountRelationshipService.prepareUpdateAccount(res,linkParentRequestDto);
//            accountDto.setHealthIdNumber(linkParentRequestDto.getChildAbhaRequestDto().getABHANumber());
//            accountDto.setXmlUID(null);
//            Mono<AccountDto> accountDtoResponse = accountService.createAccountEntity(accountDto);
//            return accountDtoResponse.flatMap(value->
//            {
//                if(value!=null)
//                {
//                    DependentAccountRelationshipDto dependentAccountDto = dependentAccountRelationshipService.prepareDependentAccount(linkParentRequestDto,value);
//                    Mono<DependentAccountRelationshipDto> dependentAccountRelationshipDtoMono = dependentAccountRelationshipService.createDependentAccountEntity(dependentAccountDto);
//                    return dependentAccountRelationshipDtoMono.flatMap(d->{
//                        if(d!=null)
//                        {
//                            return handleDependentAccountResponse(value,linkParentRequestDto);
//                        }
//                        return Mono.empty();
//                    });
//                }
//                return Mono.empty();
//            });
//        });
//    }


//    @Override
//    public Mono<LinkParentResponseDto> linkDependentAccount(LinkParentRequestDto linkParentRequestDto) {
//
//        Mono<TransactionDto> transactionDtoMono = transactionService.findTransactionDetailsFromDB(linkParentRequestDto.getTxnId());
//
//        return transactionDtoMono.flatMap(res->
//        {
//            AccountDto accountDto = dependentAccountRelationshipService.prepareUpdateAccount(res,linkParentRequestDto);
//            accountDto.setHealthIdNumber(linkParentRequestDto.getChildAbhaRequestDto().getABHANumber());
//            accountDto.setXmlUID(null); /**FOR testing**/
//            Mono<AccountDto> accountDtoResponse = accountService.createAccountEntity(accountDto);
//            return accountDtoResponse.flatMap(value->
//            {
//                if(value!=null)
//                {
//                    List<DependentAccountRelationshipDto> dependentAccountList = dependentAccountRelationshipService.prepareDependentAccount(linkParentRequestDto,value);
//                    Mono<DependentAccountRelationshipDto> dependentAccountRelationshipDtoFlux = dependentAccountRelationshipService.createDependentAccountEntity(dependentAccountList);
//                    return dependentAccountRelationshipDtoFlux.flatMap(accountRelationshipDto->
//                    {
//                        if(accountRelationshipDto!=null && accountRelationshipDto.getId()!=null)
//                        {
//                            return handleDependentAccountResponse(value,linkParentRequestDto);
//                        }
//                        return Mono.empty();
//                    }).switchIfEmpty(Mono.defer(() -> {
//                    	return handleDependentAccountResponse(value,linkParentRequestDto);
//                    }));
//                }
//                return Mono.empty();
//            });
//        });
//    }

    @Override
    public Mono<LinkParentResponseDto> linkDependentAccount(LinkParentRequestDto linkParentRequestDto) {
        List<DependentAccountRelationshipDto> dependentAccountList =
                dependentAccountRelationshipService.prepareDependentAccount(linkParentRequestDto);

        Mono<DependentAccountRelationshipDto> dependentAccountRelationshipDtoMono =
                dependentAccountRelationshipService.createDependentAccountEntity(dependentAccountList);

        dependentAccountRelationshipDtoMono.flatMap(accountRelationshipDto ->
        {
            return Mono.empty();

        }).switchIfEmpty(Mono.defer(() ->
                accountService.getAccountByHealthIdNumber(linkParentRequestDto.getChildAbhaRequestDto().getABHANumber())
                        .flatMap(res -> accountService.updateAccountByHealthIdNumber(dependentAccountRelationshipService.prepareUpdateAccount(res, linkParentRequestDto), linkParentRequestDto.getChildAbhaRequestDto().getABHANumber())
                                .flatMap(result ->
                                {
                                    if (result != null) {
                                        return handleDependentAccountResponse(result, linkParentRequestDto);
                                    }
                                    return Mono.empty();
                                }))));
        return Mono.empty();
    }


//    @Override
//    public Mono<LinkParentResponseDto> linkDependentAccount(LinkParentRequestDto linkParentRequestDto) {
//
//        Mono<TransactionDto> transactionDtoMono = transactionService.findTransactionDetailsFromDB(linkParentRequestDto.getTxnId());
//
//        return transactionDtoMono.flatMap(res->
//        {
//            AccountDto accountDto = prepareUpdateAccount(res,linkParentRequestDto);
//            accountDto.setHealthIdNumber(linkParentRequestDto.getChildAbhaRequestDto().getABHANumber());
//            return accountService.createAccountEntity(accountDto).flatMap(value->
//            {
//                if(value!=null)
//                {
//                    DependentAccountRelationshipDto dependentAccountDto = prepareDependentAccount(linkParentRequestDto,value);
//                    return linkParentService.createDependentAccountEntity(dependentAccountDto).
//                    flatMap(d->
//                    {
//                        if(d!=null)
//                        {
//                            return handleDependentAccountResponse(value,linkParentRequestDto);
//                        }
//                        return Mono.empty();
//                    });
//                }
//                return Mono.empty();
//            });
//        });
//    }

    private Mono<LinkParentResponseDto> handleDependentAccountResponse(AccountDto accountDto, LinkParentRequestDto linkParentRequestDto) {
        return Mono.just(LinkParentResponseDto.builder()
                .txnId(linkParentRequestDto.getTxnId())
                .abhaProfileDto(mapAccountToProfile(accountDto))
                .build()
        );
    }

    private ABHAProfileDto mapAccountToProfile(AccountDto accountDto) {
        return ABHAProfileDto.builder()
                .abhaNumber(accountDto.getHealthIdNumber())
                .abhaStatus(AccountStatus.ACTIVE)
                .ABHAType(AbhaType.CHILD)
                .abhaStatusReasonCode(StringConstants.EMPTY)
                .poi("aadhaar")
                .firstName(accountDto.getFirstName())
                .middleName(accountDto.getMiddleName())
                .lastName(accountDto.getLastName())
                .dob(accountDto.getKycDob())
                .gender(accountDto.getGender())
                .photo(null)
                .mobile(accountDto.getMobile())
                .email(accountDto.getEmail())
                .phrAddress(null)
                .addressLine1(accountDto.getAddress())
                .districtCode(accountDto.getDistrictCode())
                .stateCode(accountDto.getStateCode())
                .pinCode(accountDto.getPincode())
                .qrCode(null)
                .pdfData(null)
                .build();
    }
}
