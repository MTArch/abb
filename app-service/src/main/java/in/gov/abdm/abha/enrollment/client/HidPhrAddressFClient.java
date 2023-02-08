package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.configuration.BeanConfiguration;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import org.springframework.web.bind.annotation.*;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name="hidPhrAddress-enrolment-db-client", url="${enrollment.gateway.enrollmentdb.baseuri}", configuration = BeanConfiguration.class)
public interface HidPhrAddressFClient {

    @PostMapping(URIConstant.DB_ADD_HID_PHR_ADDRESS_URI)
    public Mono<HidPhrAddressDto> createHidPhrAddress(@RequestBody HidPhrAddressDto hidPhrAddressDto);

    @GetMapping(URIConstant.DB_GET_HID_PHR_ADDRESS_BY_PHR_ADDRESS)
    public Mono<HidPhrAddressDto> getPhrAddress(@PathVariable("phrAddress") String phrAddress);

    @GetMapping(URIConstant.DB_GET_HID_PHR_ADDRESS_BY_HEALTH_ID_NUMBER+"{healthIdNumber}")
    public Mono<HidPhrAddressDto>findByByHealthIdNumber(@PathVariable("healthIdNumber") String healthIdNumber);

    @PatchMapping(URIConstant.DB_UPDATE_HID_PHR_ADDRESS_BY_HID_PHR_ADDRESS_ID)
    public Mono<HidPhrAddressDto>updateHidPhrAddress(@RequestBody HidPhrAddressDto hidPhrAddressDto,
                        @PathVariable("hidPhrAddressId") Long hidPhrAddressId);

    @GetMapping(URIConstant.DB_ADD_HID_PHR_ADDRESS_URI)
    Flux<HidPhrAddressDto>getHidPhrAddressByHealthIdNumbersAndPreferredIn(
            @RequestParam("healthIdNumber") String healthIdNumbers,
            @RequestParam("preferred") String preferred);

    @GetMapping(URIConstant.DB_GET_HID_PHR_ADDRESS_BY_PHR_ADDRESS_LIST)
    Flux<HidPhrAddressDto>findByPhrAddressIn(
            @RequestParam("phrAddress") String phrAddress);

}
