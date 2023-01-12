package in.gov.abdm.abha.enrollment.services.database.hidphraddress;
import java.util.List;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import in.gov.abdm.abha.enrollment.model.entities.TransactionDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HidPhrAddressService {
	
	Mono<HidPhrAddressDto> createHidPhrAddressEntity(HidPhrAddressDto hidPhrAddressDto);
	
	HidPhrAddressDto prepareNewHidPhrAddress(TransactionDto transactionDto, AccountDto accountDto, ABHAProfileDto abhaProfileDto);
	    
	/**
	 * to fetch hid phr addresses by list of healthId numbers
	 * and preferred values
	 *
	 * @param healthIdNumbers
	 * @param preferred
	 * 
	 * @return Flux<HidPhrAddressDto>
	 */
	Flux<HidPhrAddressDto> getHidPhrAddressByHealthIdNumbersAndPreferredIn(List<String> healthIdNumbers,
			List<Integer> preferred);

	Flux<HidPhrAddressDto> findByPhrAddressIn(List<String> phrAddress);

	Mono<HidPhrAddressDto> getPhrAddressByPhrAddress(String phrAddress);

	Mono<HidPhrAddressDto> findByByHealthIdNumber(String healthIdNumber);
	Mono<HidPhrAddressDto> updateHidPhrAddressById(HidPhrAddressDto hidPhrAddressDto, Long hidPhrAddressId);

}
