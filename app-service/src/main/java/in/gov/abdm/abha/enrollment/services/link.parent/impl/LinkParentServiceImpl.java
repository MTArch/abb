package in.gov.abdm.abha.enrollment.services.link.parent.impl;
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
import in.gov.abdm.abha.enrollment.services.link.parent.LinkParentService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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


    @Override
    public Mono<LinkParentResponseDto> linkDependentAccount(LinkParentRequestDto linkParentRequestDto) {

        Mono<TransactionDto> transactionDtoMono = transactionService.findTransactionDetailsFromDB(linkParentRequestDto.getTxnId());

        return transactionDtoMono.flatMap(res->
        {
            AccountDto accountDto = dependentAccountRelationshipService.prepareUpdateAccount(res,linkParentRequestDto);
            accountDto.setHealthIdNumber(linkParentRequestDto.getChildAbhaRequestDto().getABHANumber());
            accountDto.setXmlUID(null); /**FOR testing**/
            Mono<AccountDto> accountDtoResponse = accountService.createAccountEntity(accountDto);
            return accountDtoResponse.flatMap(value->
            {
                if(value!=null)
                {
                    List<DependentAccountRelationshipDto> dependentAccountList = dependentAccountRelationshipService.prepareDependentAccount(linkParentRequestDto,value);
                    Flux<DependentAccountRelationshipDto> dependentAccountRelationshipDtoFlux = dependentAccountRelationshipService.createDependentAccountEntity(dependentAccountList);
                    return dependentAccountRelationshipDtoFlux.flatMap(accountRelationshipDto->
                    {
                        if(accountRelationshipDto!=null && accountRelationshipDto.getId()!=null)
                        {
                            return handleDependentAccountResponse(value,linkParentRequestDto,accountRelationshipDto);
                        }
                        return Mono.empty();
                    });
                }
                return Mono.empty();
            });
        });
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

    private Mono<LinkParentResponseDto> handleDependentAccountResponse(AccountDto accountDto, LinkParentRequestDto linkParentRequestDto, DependentAccountRelationshipDto accountRelationshipDto) {
        return Mono.just(LinkParentResponseDto.builder()
                .txnId(linkParentRequestDto.getTxnId())
                .abhaProfileDto(mapAccountToProfile(accountDto,accountRelationshipDto))
                .build()
        );
    }

    private ABHAProfileDto mapAccountToProfile(AccountDto accountDto,DependentAccountRelationshipDto accountRelationshipDto) {
        return ABHAProfileDto.builder()
                .abhaNumber(accountRelationshipDto.getDependentHealthIdNumber())
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
