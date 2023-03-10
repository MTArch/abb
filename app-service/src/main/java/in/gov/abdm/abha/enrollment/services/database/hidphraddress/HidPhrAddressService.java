package in.gov.abdm.abha.enrollment.services.database.hidphraddress;

import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface HidPhrAddressService {

    Mono<HidPhrAddressDto> createHidPhrAddressEntity(HidPhrAddressDto hidPhrAddressDto);

    HidPhrAddressDto prepareNewHidPhrAddress(AccountDto accountDto, ABHAProfileDto abhaProfileDto);

    HidPhrAddressDto prepareNewHidPhrAddress(AccountDto accountDto);

    /**
     * to fetch hid phr addresses by list of healthId numbers
     * and preferred values
     *
     * @param healthIdNumbers
     * @param preferred
     * @return Flux<HidPhrAddressDto>
     */
    Flux<HidPhrAddressDto> getHidPhrAddressByHealthIdNumbersAndPreferredIn(List<String> healthIdNumbers,
                                                                           List<Integer> preferred);

    Flux<HidPhrAddressDto> findByPhrAddressIn(List<String> phrAddress);

    Mono<HidPhrAddressDto> getPhrAddressByPhrAddress(String phrAddress);

    Mono<HidPhrAddressDto> findByHealthIdNumber(String healthIdNumber);
    Mono<HidPhrAddressDto> updateHidPhrAddressById(HidPhrAddressDto hidPhrAddressDto, Long hidPhrAddressId);
}
