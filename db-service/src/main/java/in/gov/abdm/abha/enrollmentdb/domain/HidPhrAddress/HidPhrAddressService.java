package in.gov.abdm.abha.enrollmentdb.domain.HidPhrAddress;


import java.util.List;
import java.util.Set;

import in.gov.abdm.abha.enrollmentdb.model.HidPhrAddress.HidPhrAddress;
import in.gov.abdm.abha.enrollmentdb.model.HidPhrAddress.HidPhrAddressDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service layer to perform crud operations on HidPhrAddress Entity
 */
public interface HidPhrAddressService {

	/**
	 * to add new Hid Phr address
	 *
	 * @param hidPhrAddressDto
	 * @return
	 */
	Mono<HidPhrAddressDto> addHidPhrAddress(HidPhrAddressDto hidPhrAddressDto);

	/**
	 * to update HidPhrAddress by Id
	 *
	 * @param hidPhrAddressDto
	 * @param hidPhrAddressId
	 * @return
	 */
	Mono<HidPhrAddressDto> updateHidPhrAddressById(HidPhrAddressDto hidPhrAddressDto, Long hidPhrAddressId);

	/**
	 * to fetch Hid Phr Address details by id
	 *
	 * @param hidPhrAddressId
	 * @return
	 */

	Mono<HidPhrAddressDto> getHidPhrAddressById(Long hidPhrAddressId);

	/**
	 * to delete Hid Phr Address details by Id
	 *
	 * @param hidPhrAddressId
	 * @return
	 */
	Mono<Void> deleteHidPhrAddressById(Long hidPhrAddressId);

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

	Mono<HidPhrAddressDto> findByHealthIdNumber(String healthIdNumber);

}
