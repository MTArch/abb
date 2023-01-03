package in.gov.abdm.abha.enrollment.services.database.hidphraddress.impl;

import java.util.List;
import java.util.stream.Collectors;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gov.abdm.abha.enrollment.client.AbhaDBClient;
import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class HidPhrAddressServiceImpl implements HidPhrAddressService {

	@Autowired
	AbhaDBClient abhaDBClient;

	@Override
	public Mono<HidPhrAddressDto> createHidPhrAddressEntity(HidPhrAddressDto hidPhrAddressDto) {
		return abhaDBClient.addEntity(HidPhrAddressDto.class, hidPhrAddressDto);
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
		StringBuilder sb = new StringBuilder(URIConstant.DB_ADD_HID_PHR_ADDRESS_URI)
				.append(StringConstants.QUESTION)
				.append("healthIdNumber")
				.append(StringConstants.EQUAL)
				.append(healthIdNumbers.stream().collect(Collectors.joining(",")))
				.append(StringConstants.AMPERSAND)
				.append("preferred")
				.append(StringConstants.EQUAL)
				.append(preferred.stream().map(n -> n.toString()).collect(Collectors.joining(",")));

		return abhaDBClient.GetFluxDatabase(HidPhrAddressDto.class, sb.toString());
	}

}
