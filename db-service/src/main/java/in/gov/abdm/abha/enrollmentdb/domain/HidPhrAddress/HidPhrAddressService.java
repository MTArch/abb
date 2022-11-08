package in.gov.abdm.abha.enrollmentdb.domain.HidPhrAddress;


import in.gov.abdm.abha.enrollmentdb.model.HidPhrAddress.HidPhrAddressDto;
import reactor.core.publisher.Mono;

/**
 * Service layer to perform crud operations on HidPhrAddress Entity
 */
public interface HidPhrAddressService {

    /**
     * to fetch Hid Phr Address details by id
     *
     * @param hidPhrAddressId
     * @return
     */

    Mono<HidPhrAddressDto> getHidPhrAddressById(Long hidPhrAddressId);

    /**
     * to delete Hid Phr Address details by Id
     * @param hidPhrAddressDto
     * @param hidPhrAddressId
     * @return
     */
    Mono deleteHidPhrAddressById(HidPhrAddressDto hidPhrAddressDto, Long hidPhrAddressId);

}
