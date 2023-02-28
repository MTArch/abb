package in.gov.abdm.abha.enrollment.services.link.parent.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import in.gov.abdm.abha.enrollment.exception.application.AbhaUnProcessableException;
import in.gov.abdm.error.ABDMError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.exception.abha_db.TransactionNotFoundException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.DependentAccountRelationshipDto;
import in.gov.abdm.abha.enrollment.model.link.parent.request.LinkParentRequestDto;
import in.gov.abdm.abha.enrollment.model.link.parent.request.ParentAbhaRequestDto;
import in.gov.abdm.abha.enrollment.model.link.parent.response.LinkParentResponseDto;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.dependent_account_relationship.DependentAccountRelationshipService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.link.parent.LinkParentService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class LinkParentServiceImpl implements LinkParentService {

    @Autowired
    TransactionService transactionService;
    
    @Autowired
    AccountService accountService;

    @Autowired
    DependentAccountRelationshipService dependentAccountRelationshipService;

    @Autowired
    HidPhrAddressService hidPhrAddressService;
    
    @Override
    public Mono<LinkParentResponseDto> linkDependentAccount(LinkParentRequestDto linkParentRequestDto) {

        return validateLinkRequest(linkParentRequestDto)
                .flatMap(exists->{
                    if(exists)
                    {
                        List<DependentAccountRelationshipDto> dependentAccountList = dependentAccountRelationshipService
                                .prepareDependentAccount(linkParentRequestDto);

                        Mono<DependentAccountRelationshipDto> dependentAccountRelationshipDtoMono = dependentAccountRelationshipService
                                .createDependentAccountEntity(dependentAccountList);

                        return dependentAccountRelationshipDtoMono.flatMap(accountRelationshipDto -> {
                            return updateDependentAccount(linkParentRequestDto);
                        }).switchIfEmpty(Mono.defer(() -> {
                            return updateDependentAccount(linkParentRequestDto);
                        }));
                    }
                    else {
                        throw new AbhaUnProcessableException(ABDMError.INVALID_LINK_REQUEST);
                    }
                });
    }

    private Mono<Boolean> validateLinkRequest(LinkParentRequestDto linkParentRequestDto) {
        return transactionService.findTransactionDetailsFromDB(linkParentRequestDto.getTxnId()).flatMap(transactionDto->{
            if(transactionDto!=null && transactionDto.getTxnResponse()!=null) {

                List<String> txnResponseHealthIdNumbers = Stream.of(transactionDto.getTxnResponse().split(","))
                        .collect(Collectors.toList());

                List<String> parentHealthIdNumbers = linkParentRequestDto.getParentAbhaRequestDtoList().stream()
                        .map(ParentAbhaRequestDto:: getABHANumber)
                        .collect(Collectors.toList());

                boolean flag1 = isParentValid(txnResponseHealthIdNumbers, parentHealthIdNumbers);
                boolean flag2 = isChildValid(transactionDto.getHealthIdNumber(),linkParentRequestDto.getChildAbhaRequestDto().getABHANumber());
                if(!flag1 || !flag2) {
					throw new AbhaUnProcessableException(ABDMError.INVALID_LINK_REQUEST);
                }
            }
            return Mono.just(true);
        }).switchIfEmpty(Mono.error(new TransactionNotFoundException(AbhaConstants.TRANSACTION_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    private boolean isChildValid(String healthIdNumberFromTxn,String healthIdNumberFromRequest) {
        return healthIdNumberFromTxn!=null && healthIdNumberFromTxn.equals(healthIdNumberFromRequest);
    }

    public boolean isParentValid(List<String> txnResponseHealthIdNumbers,List<String> parentHealthIdNumbers)
    {
        return new HashSet<>(txnResponseHealthIdNumbers).containsAll(parentHealthIdNumbers);
    }

	private Mono<LinkParentResponseDto> updateDependentAccount(LinkParentRequestDto linkParentRequestDto) {
		return accountService.getAccountByHealthIdNumber(linkParentRequestDto.getChildAbhaRequestDto().getABHANumber())
				.flatMap(res -> {
					res.setStatus(AccountStatus.ACTIVE.getValue());
					return accountService.updateAccountByHealthIdNumber(res, res.getHealthIdNumber())
							.flatMap(result -> {
								if (result != null) {
									return handleDependentAccountResponse(result, linkParentRequestDto);
								}
								return Mono.empty();
							});
				});
	}

	private Mono<LinkParentResponseDto> handleDependentAccountResponse(AccountDto accountDto,
			LinkParentRequestDto linkParentRequestDto) {
		Flux<String> fluxPhrAaddress = hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(
				new ArrayList<>(Collections.singleton(accountDto.getHealthIdNumber())),
				new ArrayList<>(Collections.singleton(1))).map(h -> h.getPhrAddress());

		return fluxPhrAaddress.collectList().flatMap(Mono::just).flatMap(result -> {
			return mapAccountToProfile(result, accountDto, linkParentRequestDto);
		}).switchIfEmpty(Mono.defer(() -> {
			return mapAccountToProfile(null, accountDto, linkParentRequestDto);
		}));
	}
	
	private Mono<LinkParentResponseDto> mapAccountToProfile(List<String> phrAddress, AccountDto accountDto,
			LinkParentRequestDto linkParentRequestDto) {
		return Mono.just(LinkParentResponseDto.builder()
				.txnId(linkParentRequestDto.getTxnId())
				.abhaProfileDto(ABHAProfileDto.builder()
						.abhaNumber(accountDto.getHealthIdNumber())
						.abhaStatus(AccountStatus.ACTIVE)
						.ABHAType(accountDto.getType())
						.firstName(accountDto.getFirstName())
						.middleName(accountDto.getMiddleName())
						.lastName(accountDto.getLastName())
						.dob(accountDto.getKycdob())
						.gender(accountDto.getGender())
						// TODO
//						.photo(accountDto.getPhoto())
						.mobile(accountDto.getMobile())
						.email(accountDto.getEmail())
						.phrAddress(phrAddress)
						.address(accountDto.getAddress())
						.districtCode(accountDto.getDistrictCode())
						.stateCode(accountDto.getStateCode())
						.pinCode(accountDto.getPincode())
						// TODO
						// .qrCode(null)
						// .pdfData(null)
						.build())
				.build());
	}

}
