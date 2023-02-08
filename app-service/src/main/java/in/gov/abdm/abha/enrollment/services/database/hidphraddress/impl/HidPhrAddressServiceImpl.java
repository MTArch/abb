package in.gov.abdm.abha.enrollment.services.database.hidphraddress.impl;

import in.gov.abdm.abha.enrollment.client.AbhaDBClient;
import in.gov.abdm.abha.enrollment.client.HidPhrAddressFClient;
import in.gov.abdm.abha.enrollment.configuration.ContextHolder;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.exception.abha_db.AbhaDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HidPhrAddressServiceImpl extends AbhaDBClient implements HidPhrAddressService {

	@Autowired
	HidPhrAddressFClient hidPhrAddressFClient;

	@Override
	public Mono<HidPhrAddressDto> createHidPhrAddressEntity(HidPhrAddressDto hidPhrAddressDto) {
		hidPhrAddressDto.setCreatedBy(ContextHolder.getClientId());
		hidPhrAddressDto.setLastModifiedBy(ContextHolder.getClientId());
		return hidPhrAddressFClient.createHidPhrAddress( hidPhrAddressDto)
				.doOnError((throwable->Mono.error(new AbhaDBGatewayUnavailableException())));
	}

	@Override
	public HidPhrAddressDto prepareNewHidPhrAddress(TransactionDto transactionDto, AccountDto accountDto,
													ABHAProfileDto abhaProfileDto) {

		return HidPhrAddressDto.builder()
				.healthIdNumber(abhaProfileDto.getAbhaNumber())
				.phrAddress(abhaProfileDto.getPhrAddress().get(0))
				.status("ACTIVE")
				.preferred(1)
				.lastModifiedBy(accountDto.getLstUpdatedBy())
				.hasMigrated("N")
				.createdBy(accountDto.getLstUpdatedBy())
				.createdDate(accountDto.getCreatedDate())
				.linked(1)
				.cmMigrated(0)
				.isNewHidPhrAddress(true)
				.build();
	}

	@Override
	public HidPhrAddressDto prepareNewHidPhrAddress(AccountDto accountDto) {

		return HidPhrAddressDto.builder()
				.healthIdNumber(accountDto.getHealthIdNumber())
				.phrAddress(accountDto.getHealthId())
				.status(AccountStatus.ACTIVE.getValue())
				.preferred(1)
				.lastModifiedBy(accountDto.getLstUpdatedBy())
				.hasMigrated("N")
				.createdBy(accountDto.getLstUpdatedBy())
				.createdDate(accountDto.getCreatedDate())
				.linked(1)
				.cmMigrated(0)
				.isNewHidPhrAddress(true)
				.build();
	}

	@Override
	public Flux<HidPhrAddressDto> getHidPhrAddressByHealthIdNumbersAndPreferredIn(List<String> healthIdNumbers,
																				  List<Integer> preferred) {
		return hidPhrAddressFClient.getHidPhrAddressByHealthIdNumbersAndPreferredIn(healthIdNumbers.stream().collect(Collectors.joining(",")),preferred.stream().map(n -> n.toString()).collect(Collectors.joining(",")))
				.doOnError((throwable->Mono.error(new AbhaDBGatewayUnavailableException())));
	}

	@Override
	public Flux<HidPhrAddressDto> findByPhrAddressIn(List<String> phrAddress) {
		return hidPhrAddressFClient.findByPhrAddressIn(phrAddress.stream().collect(Collectors.joining(",")))
				.doOnError((throwable->Mono.error(new AbhaDBGatewayUnavailableException())));
	}
	@Override
	public Mono<HidPhrAddressDto> getPhrAddressByPhrAddress(String phrAddress) {
		return hidPhrAddressFClient.getPhrAddress(phrAddress)
				.doOnError((throwable->Mono.error(new AbhaDBGatewayUnavailableException())));
	}

	@Override
	public Mono<HidPhrAddressDto> findByHealthIdNumber(String healthIdNumber) {
		return hidPhrAddressFClient.findByByHealthIdNumber(healthIdNumber)
				.doOnError((throwable->Mono.error(new AbhaDBGatewayUnavailableException())));
	}

	@Override
	public Mono<HidPhrAddressDto> updateHidPhrAddressById(HidPhrAddressDto hidPhrAddressDto, Long hidPhrAddressId) {
		hidPhrAddressDto.setLastModifiedBy(ContextHolder.getClientId());
		return hidPhrAddressFClient.updateHidPhrAddress(hidPhrAddressDto, hidPhrAddressId)
				.doOnError((throwable->Mono.error(new AbhaDBGatewayUnavailableException())));
	}

}
