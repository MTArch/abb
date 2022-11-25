package in.gov.abdm.abha.enrollmentdb.domain.HidPhrAddress;


import in.gov.abdm.abha.enrollmentdb.model.HidPhrAddress.HidPhrAddress;
import in.gov.abdm.abha.enrollmentdb.model.HidPhrAddress.HidPhrAddressDto;
import in.gov.abdm.abha.enrollmentdb.repository.HidPhrAddressRepository;
import in.gov.abdm.abha.enrollmentdb.utilities.phr_address_generator.PhrAddressGenerator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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
    public Mono<HidPhrAddress> addHidPhrAddress(HidPhrAddressDto hidPhrAddressDto) {


        HidPhrAddress hidPhrAddress = modelMapper.map(hidPhrAddressDto, HidPhrAddress.class);
        hidPhrAddress.setAsNew();
        return hidPhrAddressRepository.save(hidPhrAddress);
    }

    @Override
    public Mono<HidPhrAddress> updateHidPhrAddressById(HidPhrAddressDto hidPhrAddressDto, Long hidPhrAddressId) {
        HidPhrAddress hidPhrAddress = modelMapper.map(hidPhrAddressDto, HidPhrAddress.class);
        return hidPhrAddressRepository.save(hidPhrAddress);
    }

    @Override
    public Mono<HidPhrAddressDto> getHidPhrAddressById(Long hidPhrAddressId) {

        return hidPhrAddressRepository.findById(hidPhrAddressId).
                map(HidPhrAddress -> modelMapper.map(HidPhrAddress, HidPhrAddressDto.class));
    }

    @Override
    public Mono deleteHidPhrAddressById(Long hidPhrAddressId) {

        return hidPhrAddressRepository.deleteById(hidPhrAddressId);
    }
}
