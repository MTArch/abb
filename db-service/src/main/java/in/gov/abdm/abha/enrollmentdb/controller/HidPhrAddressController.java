package in.gov.abdm.abha.enrollmentdb.controller;

import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.domain.hid_phr_address.HidPhrAddressService;
import in.gov.abdm.abha.enrollmentdb.model.hid_phr_address.HidPhrAddressDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.*;

@RequestMapping(ABHAEnrollmentDBConstant.HID_PHR_ADDRESS_ENDPOINT)
@Slf4j
@RestController
public class HidPhrAddressController {

    @Autowired
    HidPhrAddressService hidPhrAddressService;

    @PostMapping
    public ResponseEntity<Mono<HidPhrAddressDto>> createHidPhrAddress(@RequestBody HidPhrAddressDto hidPhrAddressDto) {
        log.info(ENROLLMENT_DB_LOG_MSG + "save data" + ENROLLMENT_DB_HID_PHR_ADDRESS);
        return ResponseEntity.ok(hidPhrAddressService.addHidPhrAddress(hidPhrAddressDto));
    }

    @PatchMapping(value = ABHAEnrollmentDBConstant.UPDATE_HID_PHR_ADDRESS_BY_ID)
    public ResponseEntity<Mono<HidPhrAddressDto>> updateHidPhrAddress(@RequestBody HidPhrAddressDto hidPhrAddressDto,
                                                                      @PathVariable("hidPhrAddressId") Long hidPhrAddressId) {
        log.info(ENROLLMENT_DB_LOG_MSG + "update data based on hidPhrAddressId=" + hidPhrAddressId + ENROLLMENT_DB_HID_PHR_ADDRESS);
        return ResponseEntity.ok(hidPhrAddressService.updateHidPhrAddressById(hidPhrAddressDto, hidPhrAddressId));
    }

    @GetMapping(value = ABHAEnrollmentDBConstant.GET_HID_PHR_ADDRESS_BY_ID)
    public ResponseEntity<Mono<HidPhrAddressDto>> getHidPhrAddress(@PathVariable("hidPhrAddressId") Long hidPhrAddressId) {
        log.info(ENROLLMENT_DB_LOG_MSG + "get data based on hidPhrAddressId=" + hidPhrAddressId + ENROLLMENT_DB_HID_PHR_ADDRESS);
        return ResponseEntity.ok(hidPhrAddressService.getHidPhrAddressById(hidPhrAddressId));
    }

    @DeleteMapping(value = ABHAEnrollmentDBConstant.DELETE_HID_PHR_ADDRESS_BY_ID)
    public ResponseEntity<Mono<Void>> deleteHidPhrAddress(@PathVariable("hidPhrAddressId") Long hidPhrAddressId) {
        log.info(ENROLLMENT_DB_LOG_MSG + "delete data based on hidPhrAddressId=" + hidPhrAddressId + ENROLLMENT_DB_HID_PHR_ADDRESS);
        return ResponseEntity.ok(hidPhrAddressService.deleteHidPhrAddressById(hidPhrAddressId));
    }

    @GetMapping
    public ResponseEntity<Flux<HidPhrAddressDto>> getHidPhrAddressByHealthIdNumbersAndPreferredIn(
            @RequestParam("healthIdNumber") List<String> healthIdNumbers,
            @RequestParam("preferred") List<Integer> preferred) {
        log.info(ENROLLMENT_DB_LOG_MSG + "get data based on healthIdNumbers and preferred flag=" + healthIdNumbers + ENROLLMENT_DB_HID_PHR_ADDRESS);
        return ResponseEntity.ok(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(healthIdNumbers, preferred));
    }

    @GetMapping(HID_CHECK)
    public ResponseEntity<Flux<HidPhrAddressDto>> findByPhrAddressIn(
            @RequestParam("phrAddress") List<String> phrAddress) {
        log.info(ENROLLMENT_DB_LOG_MSG + "get data based on phrAddress list =" + phrAddress + ENROLLMENT_DB_HID_PHR_ADDRESS);
        return ResponseEntity.ok(hidPhrAddressService.findByPhrAddressIn(phrAddress));
    }

    @GetMapping(value = ABHAEnrollmentDBConstant.GET_HID_PHR_ADDRESS_BY_PHR_ADDRESS)
    public ResponseEntity<Mono<HidPhrAddressDto>> getPhrAddress(@PathVariable("phrAddress") String phrAddress) {
        log.info(ENROLLMENT_DB_LOG_MSG + "get data based on phrAddress=" + phrAddress + ENROLLMENT_DB_HID_PHR_ADDRESS);
        return ResponseEntity.ok(hidPhrAddressService.getPhrAddressByPhrAddress(phrAddress));
    }

    @GetMapping(value = ABHAEnrollmentDBConstant.GET_HID_PHR_ADDRESS_BY_HEALTH_ID_NUMBER)
    public ResponseEntity<Mono<HidPhrAddressDto>> findByHealthIdNumber(@PathVariable("healthIdNumber") String healthIdNumber) {
        log.info(ENROLLMENT_DB_LOG_MSG + "get data based on healthIdNumber=" + healthIdNumber + ENROLLMENT_DB_HID_PHR_ADDRESS);
        return ResponseEntity.ok(hidPhrAddressService.findByHealthIdNumber(healthIdNumber));
    }

}
