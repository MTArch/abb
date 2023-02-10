package in.gov.abdm.abha.enrollment.services.database.hidphraddress.impl;

import in.gov.abdm.abha.enrollment.client.AbhaDBClient;
import in.gov.abdm.abha.enrollment.configuration.ContextHolder;
import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HidPhrAddressServiceImpl extends AbhaDBClient implements HidPhrAddressService {

	@Override
	public Mono<HidPhrAddressDto> createHidPhrAddressEntity(HidPhrAddressDto hidPhrAddressDto) {
		hidPhrAddressDto.setCreatedBy(ContextHolder.getClientId());
		hidPhrAddressDto.setLastModifiedBy(ContextHolder.getClientId());
		return addEntity(HidPhrAddressDto.class, hidPhrAddressDto);
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
				.lastModifiedDate(accountDto.getCreatedDate())
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
		StringBuilder sb = new StringBuilder(URIConstant.DB_ADD_HID_PHR_ADDRESS_URI)
				.append(StringConstants.QUESTION)
				.append("healthIdNumber")
				.append(StringConstants.EQUAL)
				.append(healthIdNumbers.stream().collect(Collectors.joining(",")))
				.append(StringConstants.AMPERSAND)
				.append("preferred")
				.append(StringConstants.EQUAL)
				.append(preferred.stream().map(n -> n.toString()).collect(Collectors.joining(",")));

		return GetFluxDatabase(HidPhrAddressDto.class, sb.toString());
	}

	@Override
	public Flux<HidPhrAddressDto> findByPhrAddressIn(List<String> phrAddress) {
		StringBuilder sb = new StringBuilder(URIConstant.DB_GET_HID_PHR_ADDRESS_BY_PHR_ADDRESS_LIST)
				.append(StringConstants.QUESTION)
				.append("phrAddress")
				.append(StringConstants.EQUAL)
				.append(phrAddress.stream().collect(Collectors.joining(",")));

		return GetFluxDatabase(HidPhrAddressDto.class, sb.toString());
	}
	@Override
	public Mono<HidPhrAddressDto> getPhrAddressByPhrAddress(String phrAddress) {
		return getHidPhrAddressByPhrAddress(HidPhrAddressDto.class,phrAddress);
	}

	@Override
	public Mono<HidPhrAddressDto> findByHealthIdNumber(String healthIdNumber) {
		return getEntityById(HidPhrAddressDto.class,healthIdNumber);
	}

	@Override
	public Mono<HidPhrAddressDto> updateHidPhrAddressById(HidPhrAddressDto hidPhrAddressDto, Long hidPhrAddressId) {
		hidPhrAddressDto.setLastModifiedBy(ContextHolder.getClientId());
		return updateEntity(HidPhrAddressDto.class,hidPhrAddressDto, String.valueOf(hidPhrAddressId));
	}

}
