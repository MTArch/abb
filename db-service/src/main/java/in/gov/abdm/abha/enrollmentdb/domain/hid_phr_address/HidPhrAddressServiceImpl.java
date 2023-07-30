package in.gov.abdm.abha.enrollmentdb.domain.hid_phr_address;


import in.gov.abdm.abha.enrollmentdb.domain.account.AccountService;
import in.gov.abdm.abha.enrollmentdb.domain.kafka.KafkaService;
import in.gov.abdm.abha.enrollmentdb.model.hid_phr_address.HidPhrAddress;
import in.gov.abdm.abha.enrollmentdb.model.hid_phr_address.HidPhrAddressDto;
import in.gov.abdm.abha.enrollmentdb.repository.HidPhrAddressRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * A class which implements Business logic.
 */

@Service
@Slf4j
public class HidPhrAddressServiceImpl implements HidPhrAddressService {

    @Autowired
    HidPhrAddressRepository hidPhrAddressRepository;

    @Autowired
    private AccountService accountService;

    /**
     * Here we are creating a ModelMapper object and putting into IOC
     * for implementing singleton, with the reference all its methods can be utilized.
     */
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    KafkaService kafkaService;

    @Override
    public Mono<HidPhrAddressDto> addHidPhrAddress(HidPhrAddressDto hidPhrAddressDto) {
        HidPhrAddress hidPhrAddress = modelMapper.map(hidPhrAddressDto, HidPhrAddress.class).setAsNew();
        return hidPhrAddressRepository.save(hidPhrAddress)
                .map(hidPhrAdd ->{
                    kafkaService.publishPhrUserPatientEvent(hidPhrAddress).subscribe();
                    return modelMapper.map(hidPhrAdd, HidPhrAddressDto.class);
                });
    }

    @Override
    public Mono<HidPhrAddressDto> updateHidPhrAddressById(HidPhrAddressDto hidPhrAddressDto, Long hidPhrAddressId) {
        HidPhrAddress hidPhrAddress = modelMapper.map(hidPhrAddressDto, HidPhrAddress.class);
        return hidPhrAddressRepository.save(hidPhrAddress)
                .map(hidPhrAdd -> {
                    kafkaService.publishPhrUserPatientEvent(hidPhrAddress).subscribe();
                    return modelMapper.map(hidPhrAdd, HidPhrAddressDto.class);
                });
    }

    @Override
    public Mono<HidPhrAddressDto> getHidPhrAddressById(Long hidPhrAddressId) {
        return hidPhrAddressRepository.findById(hidPhrAddressId).
                map(hidPhrAddress -> modelMapper.map(hidPhrAddress, HidPhrAddressDto.class));
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
    public Mono<HidPhrAddressDto> findByHealthIdNumber(String healthIdNumber) {
        return hidPhrAddressRepository.findByHealthIdNumber(healthIdNumber)
                .map(hidPhrAddress -> modelMapper.map(hidPhrAddress,HidPhrAddressDto.class));
    }
}
