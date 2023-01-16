package in.gov.abdm.abha.enrollmentdb.domain.HidPhrAddress;


import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gov.abdm.abha.enrollmentdb.model.HidPhrAddress.HidPhrAddress;
import in.gov.abdm.abha.enrollmentdb.model.HidPhrAddress.HidPhrAddressDto;
import in.gov.abdm.abha.enrollmentdb.repository.HidPhrAddressRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * A class which implements Business logic.
 */

@Service
public class HidPhrAddressServiceImpl implements HidPhrAddressService {

    @Autowired
    HidPhrAddressRepository hidPhrAddressRepository;

    /**
     * Here we are creating a ModelMapper object and putting into IOC
     * for implementing singleton, with the reference all its methods can be utilized.
     */
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private HidPhrAddressSubscriber hidPhrAddressSubscriber;

    @Override
	public Mono<HidPhrAddressDto> addHidPhrAddress(HidPhrAddressDto hidPhrAddressDto) {
		HidPhrAddress hidPhrAddress = modelMapper.map(hidPhrAddressDto, HidPhrAddress.class).setAsNew();
		return hidPhrAddressRepository.save(hidPhrAddress)
				.map(hidPhrAdd -> modelMapper.map(hidPhrAdd, HidPhrAddressDto.class));
	}

    @Override
	public Mono<HidPhrAddressDto> updateHidPhrAddressById(HidPhrAddressDto hidPhrAddressDto, Long hidPhrAddressId) {
		HidPhrAddress hidPhrAddress = modelMapper.map(hidPhrAddressDto, HidPhrAddress.class);
		return hidPhrAddressRepository.save(hidPhrAddress)
				.map(hidPhrAdd -> modelMapper.map(hidPhrAdd, HidPhrAddressDto.class));
	}

    @Override
    public Mono<HidPhrAddressDto> getHidPhrAddressById(Long hidPhrAddressId) {
        return hidPhrAddressRepository.findById(hidPhrAddressId).
                map(HidPhrAddress -> modelMapper.map(HidPhrAddress, HidPhrAddressDto.class));
    }

    @Override
    public Mono<Void> deleteHidPhrAddressById(Long hidPhrAddressId) {
        return hidPhrAddressRepository.deleteById(hidPhrAddressId);
    }

	@Override
	public Flux<HidPhrAddressDto> getHidPhrAddressByHealthIdNumbersAndPreferredIn(List<String> healthIdNumbers,
			List<Integer> preferred) {
		return hidPhrAddressRepository.findByHealthIdNumberInAndPreferredIn(healthIdNumbers, preferred)
				.map(hidPhrAdd -> modelMapper.map(hidPhrAdd, HidPhrAddressDto.class));
	}
    @Override
    public Flux<HidPhrAddressDto> findByPhrAddressIn(List<String> phrAddress) {
        return hidPhrAddressRepository.findByPhrAddressIn(phrAddress)
                .map(hidPhrAddress -> modelMapper.map(hidPhrAddress,HidPhrAddressDto.class));
    }

    @Override
    public Mono<HidPhrAddressDto> getPhrAddressByPhrAddress(String phrAddress) {
        return hidPhrAddressRepository.getPhrAddressByPhrAddress(phrAddress)
                .map(hidPhrAddress -> modelMapper.map(hidPhrAddress,HidPhrAddressDto.class));
    }

    @Override
    public Mono<HidPhrAddressDto> findByByHealthIdNumber(String healthIdNumber) {
        return hidPhrAddressRepository.findByByHealthIdNumber(healthIdNumber)
                .map(hidPhrAddress -> modelMapper.map(hidPhrAddress,HidPhrAddressDto.class));
    }
}
