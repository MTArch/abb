package in.gov.abdm.abha.enrollment.services.database.hidphraddress.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gov.abdm.abha.enrollment.client.AbhaDBClient;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import lombok.extern.slf4j.Slf4j;
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
				.status(abhaProfileDto.getAbhaStatus().getValue())
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

}
