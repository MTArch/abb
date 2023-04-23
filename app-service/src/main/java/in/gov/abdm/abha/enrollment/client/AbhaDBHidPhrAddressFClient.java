package in.gov.abdm.abha.enrollment.client;

import in.gov.abdm.abha.enrollment.configuration.BeanConfiguration;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import org.springframework.web.bind.annotation.*;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static in.gov.abdm.abha.enrollment.constants.PropertyConstants.ENROLLMENT_GATEWAY_ENROLLMENTDB_BASEURI;

@ReactiveFeignClient(name= AbhaConstants.ABHA_DB_HID_PHR_ADDRESS_CLIENT, url=ENROLLMENT_GATEWAY_ENROLLMENTDB_BASEURI, configuration = BeanConfiguration.class)
public interface AbhaDBHidPhrAddressFClient {

    @PostMapping(URIConstant.DB_ADD_HID_PHR_ADDRESS_URI)
    public Mono<HidPhrAddressDto> createHidPhrAddress(@RequestBody HidPhrAddressDto hidPhrAddressDto);

    @GetMapping(URIConstant.DB_GET_HID_PHR_ADDRESS_BY_PHR_ADDRESS)
    public Mono<HidPhrAddressDto> getPhrAddress(@PathVariable("phrAddress") String phrAddress);

    @GetMapping(URIConstant.FDB_GET_HID_PHR_ADDRESS_BY_HEALTH_ID_NUMBER)
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
