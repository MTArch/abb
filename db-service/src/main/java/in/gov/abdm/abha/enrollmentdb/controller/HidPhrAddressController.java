package in.gov.abdm.abha.enrollmentdb.controller;
import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.domain.HidPhrAddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollmentdb.model.HidPhrAddress.HidPhrAddressDto;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.ENROLLMENT_DB_HID_PHR_ADDRESS;
import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.ENROLLMENT_DB_LOG_MSG;

@RequestMapping(ABHAEnrollmentDBConstant.HID_PHR_ADDRESS_ENDPOINT)
@Slf4j
@RestController
public class HidPhrAddressController {

    @Autowired
    HidPhrAddressService hidPhrAddressService;

    @PostMapping
    public ResponseEntity<?>  createHidPhrAddress(@RequestBody HidPhrAddressDto hidPhrAddressDto) {
        log.info(ENROLLMENT_DB_LOG_MSG+"save data"+ENROLLMENT_DB_HID_PHR_ADDRESS);
        return ResponseEntity.ok(hidPhrAddressService.addHidPhrAddress(hidPhrAddressDto));
    }

    @PatchMapping(value = ABHAEnrollmentDBConstant.UPDATE_HID_PHR_ADDRESS_BY_ID)
    public ResponseEntity<?> updateHidPhrAddress(@RequestBody HidPhrAddressDto hidPhrAddressDto,
                                                 @PathVariable("hidPhrAddressId") Long hidPhrAddressId) {
        log.info(ENROLLMENT_DB_LOG_MSG+"update data based on hidPhrAddressId="+hidPhrAddressId+ENROLLMENT_DB_HID_PHR_ADDRESS);
        return ResponseEntity.ok(hidPhrAddressService.updateHidPhrAddressById(hidPhrAddressDto, hidPhrAddressId));
    }

    @GetMapping(value = ABHAEnrollmentDBConstant.GET_HID_PHR_ADDRESS_BY_ID)
    public ResponseEntity<?> getHidPhrAddress(@PathVariable("hidPhrAddressId") Long hidPhrAddressId) {
        log.info(ENROLLMENT_DB_LOG_MSG+"get data based on hidPhrAddressId="+hidPhrAddressId+ENROLLMENT_DB_HID_PHR_ADDRESS);
        return ResponseEntity.ok(hidPhrAddressService.getHidPhrAddressById(hidPhrAddressId));
    }

    @DeleteMapping(value = ABHAEnrollmentDBConstant.DELETE_HID_PHR_ADDRESS_BY_ID)
    public ResponseEntity<?> deleteHidPhrAddress(@PathVariable("hidPhrAddressId") Long hidPhrAddressId) {
        log.info(ENROLLMENT_DB_LOG_MSG+"delete data based on hidPhrAddressId="+hidPhrAddressId+ENROLLMENT_DB_HID_PHR_ADDRESS);
        return ResponseEntity.ok(hidPhrAddressService.deleteHidPhrAddressById(hidPhrAddressId));
    }

    @GetMapping
    public ResponseEntity<?> getHidPhrAddressByHealthIdNumbersAndPreferredIn(
            @RequestParam("healthIdNumber") List<String> healthIdNumbers,
            @RequestParam("preferred") List<Integer> preferred) {
        log.info(ENROLLMENT_DB_LOG_MSG+"get data based on healthIdNumbers and preferred flag="+healthIdNumbers+ENROLLMENT_DB_HID_PHR_ADDRESS);
        return ResponseEntity.ok(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(healthIdNumbers, preferred));
    }
    @GetMapping("/check")
    public ResponseEntity<?> findByPhrAddressIn(
            @RequestParam("phrAddress") List<String> phrAddress) {
        log.info(ENROLLMENT_DB_LOG_MSG+"get data based on phrAddress list ="+phrAddress+ENROLLMENT_DB_HID_PHR_ADDRESS);
        return ResponseEntity.ok(hidPhrAddressService.findByPhrAddressIn(phrAddress));
    }

    @GetMapping(value = ABHAEnrollmentDBConstant.GET_HID_PHR_ADDRESS_BY_PHR_ADDRESS)
    public ResponseEntity<?> getPhrAddress(@PathVariable("phrAddress") String phrAddress) {
        log.info(ENROLLMENT_DB_LOG_MSG+"get data based on phrAddress="+phrAddress+ENROLLMENT_DB_HID_PHR_ADDRESS);
        return ResponseEntity.ok(hidPhrAddressService.getPhrAddressByPhrAddress(phrAddress));
    }
    @GetMapping(value = ABHAEnrollmentDBConstant.GET_HID_PHR_ADDRESS_BY_HEALTH_ID_NUMBER)
    public ResponseEntity<?> findByHealthIdNumber(@PathVariable("healthIdNumber") String healthIdNumber) {
        log.info(ENROLLMENT_DB_LOG_MSG+"get data based on healthIdNumber="+healthIdNumber+ENROLLMENT_DB_HID_PHR_ADDRESS);
        return ResponseEntity.ok(hidPhrAddressService.findByHealthIdNumber(healthIdNumber));
    }

}
