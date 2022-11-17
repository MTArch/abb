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

	@Override
	public Mono<LinkParentResponseDto> linkDependentAccount(LinkParentRequestDto linkParentRequestDto) {
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

	private Mono<LinkParentResponseDto> updateDependentAccount(LinkParentRequestDto linkParentRequestDto) {
		return accountService.getAccountByHealthIdNumber(linkParentRequestDto.getChildAbhaRequestDto().getABHANumber())
				.flatMap(res -> {
					res.setStatus(AccountStatus.ACTIVE.getValue());
					return accountService.updateAccountByHealthIdNumber(
							res,res.getHealthIdNumber()).flatMap(result -> {
								if (result != null) {
									return handleDependentAccountResponse(result, linkParentRequestDto);
								}
								return Mono.empty();
							});
				});
	}

	private Mono<LinkParentResponseDto> handleDependentAccountResponse(AccountDto accountDto,
			LinkParentRequestDto linkParentRequestDto) {
		return Mono.just(LinkParentResponseDto.builder().txnId(linkParentRequestDto.getTxnId())
				.abhaProfileDto(mapAccountToProfile(accountDto)).build());
	}

	private ABHAProfileDto mapAccountToProfile(AccountDto accountDto) {
		return ABHAProfileDto.builder().abhaNumber(accountDto.getHealthIdNumber()).abhaStatus(AccountStatus.ACTIVE)
				.ABHAType(AbhaType.CHILD).abhaStatusReasonCode(StringConstants.EMPTY).poi("aadhaar")
				.firstName(accountDto.getFirstName()).middleName(accountDto.getMiddleName())
				.lastName(accountDto.getLastName()).dob(accountDto.getKycDob()).gender(accountDto.getGender())
				.photo(null).mobile(accountDto.getMobile()).email(accountDto.getEmail()).phrAddress(null)
				.addressLine1(accountDto.getAddress()).districtCode(accountDto.getDistrictCode())
				.stateCode(accountDto.getStateCode()).pinCode(accountDto.getPincode()).qrCode(null).pdfData(null)
				.build();
	}
}
