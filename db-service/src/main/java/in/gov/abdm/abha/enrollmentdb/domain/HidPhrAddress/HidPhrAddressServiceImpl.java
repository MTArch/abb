package in.gov.abdm.abha.enrollmentdb.domain.HidPhrAddress;


import in.gov.abdm.abha.enrollmentdb.model.HidPhrAddress.HidPhrAddress;
import in.gov.abdm.abha.enrollmentdb.model.HidPhrAddress.HidPhrAddressDto;
import in.gov.abdm.abha.enrollmentdb.repository.HidPhrAddressRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * A class which implements Business logic.
 */

@Service
public class HidPhrAddressServiceImpl implements HidPhrAddressService{


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
    public Mono<HidPhrAddressDto> getHidPhrAddressById(Long hidPhrAddressId) {

        return hidPhrAddressRepository.findById(hidPhrAddressId).
                map(HidPhrAddress -> modelMapper.map(HidPhrAddress, HidPhrAddressDto.class));
    }

    @Override
    public Mono deleteHidPhrAddressById(HidPhrAddressDto hidPhrAddressDto, Long hidPhrAddressId) {

        HidPhrAddress hidPhrAddress= modelMapper.map(hidPhrAddressDto,HidPhrAddress.class);
        return hidPhrAddressRepository.deleteById(hidPhrAddressId);
    }
}
