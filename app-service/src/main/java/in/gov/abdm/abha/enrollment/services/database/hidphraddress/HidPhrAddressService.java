package in.gov.abdm.abha.enrollment.services.database.hidphraddress;

import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import reactor.core.publisher.Mono;

public interface HidPhrAddressService {
	
	Mono<HidPhrAddressDto> createHidPhrAddressEntity(HidPhrAddressDto hidPhrAddressDto);
	
	HidPhrAddressDto prepareNewHidPhrAddress(TransactionDto transactionDto, AccountDto accountDto, ABHAProfileDto abhaProfileDto);
	    

}
