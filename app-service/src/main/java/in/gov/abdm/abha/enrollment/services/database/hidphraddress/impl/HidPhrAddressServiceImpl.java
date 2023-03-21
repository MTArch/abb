package in.gov.abdm.abha.enrollment.services.database.hidphraddress.impl;

import in.gov.abdm.abha.enrollment.client.AbhaDBClient;
import in.gov.abdm.abha.enrollment.client.AbhaDBHidPhrAddressFClient;
import in.gov.abdm.abha.enrollment.configuration.ContextHolder;
import in.gov.abdm.abha.enrollment.configuration.FacilityContextHolder;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HidPhrAddressServiceImpl implements HidPhrAddressService {

	@Autowired
	AbhaDBHidPhrAddressFClient abhaDBHidPhrAddressFClient;

	@Override
	public Mono<HidPhrAddressDto> createHidPhrAddressEntity(HidPhrAddressDto hidPhrAddressDto) {
		hidPhrAddressDto.setCreatedBy(FacilityContextHolder.getSubject() != null ? FacilityContextHolder.getSubject()  : ContextHolder.getClientId());
		hidPhrAddressDto.setLastModifiedBy(FacilityContextHolder.getSubject() != null ? FacilityContextHolder.getSubject()  : ContextHolder.getClientId());
		return abhaDBHidPhrAddressFClient.createHidPhrAddress( hidPhrAddressDto)
				.onErrorResume((throwable->Mono.error(new AbhaDBGatewayUnavailableException())));
	}

	@Override
	public HidPhrAddressDto prepareNewHidPhrAddress(AccountDto accountDto,
													ABHAProfileDto abhaProfileDto) {

		return HidPhrAddressDto.builder()
				.healthIdNumber(abhaProfileDto.getAbhaNumber())
				.phrAddress(abhaProfileDto.getPhrAddress().get(0))
				.status("ACTIVE")
				.preferred(1)
				.lastModifiedBy(accountDto.getLstUpdatedBy())
				.lastModifiedDate(LocalDateTime.now())
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
				.lastModifiedDate(LocalDateTime.now())
				.linked(1)
				.cmMigrated(0)
				.isNewHidPhrAddress(true)
				.build();
	}

	@Override
	public Flux<HidPhrAddressDto> getHidPhrAddressByHealthIdNumbersAndPreferredIn(List<String> healthIdNumbers,
																				  List<Integer> preferred) {
		return abhaDBHidPhrAddressFClient.getHidPhrAddressByHealthIdNumbersAndPreferredIn(healthIdNumbers.stream().collect(Collectors.joining(",")),preferred.stream().map(n -> n.toString()).collect(Collectors.joining(",")))
				.onErrorResume((throwable->Mono.error(new AbhaDBGatewayUnavailableException())));
	}

	@Override
	public Flux<HidPhrAddressDto> findByPhrAddressIn(List<String> phrAddress) {
		return abhaDBHidPhrAddressFClient.findByPhrAddressIn(phrAddress.stream().collect(Collectors.joining(",")))
				.onErrorResume((throwable->Mono.error(new AbhaDBGatewayUnavailableException())));
	}
	@Override
	public Mono<HidPhrAddressDto> getPhrAddressByPhrAddress(String phrAddress) {
		return abhaDBHidPhrAddressFClient.getPhrAddress(phrAddress)
				.onErrorResume((throwable->Mono.error(new AbhaDBGatewayUnavailableException())));
	}

	@Override
	public Mono<HidPhrAddressDto> findByHealthIdNumber(String healthIdNumber) {
		return abhaDBHidPhrAddressFClient.findByByHealthIdNumber(healthIdNumber)
				.onErrorResume((throwable->Mono.error(new AbhaDBGatewayUnavailableException())));
	}

	@Override
	public Mono<HidPhrAddressDto> updateHidPhrAddressById(HidPhrAddressDto hidPhrAddressDto, Long hidPhrAddressId) {
		hidPhrAddressDto.setLastModifiedBy(ContextHolder.getClientId());
		return abhaDBHidPhrAddressFClient.updateHidPhrAddress(hidPhrAddressDto, hidPhrAddressId)
				.onErrorResume((throwable->Mono.error(new AbhaDBGatewayUnavailableException())));
	}

}
