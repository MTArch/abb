package in.gov.abdm.abha.enrollmentdb.controller;
import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.domain.HidPhrAddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollmentdb.model.HidPhrAddress.HidPhrAddressDto;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(ABHAEnrollmentDBConstant.HID_PHR_ADDRESS_ENDPOINT)
@RestController
public class HidPhrAddressController {

    @Autowired
    HidPhrAddressService hidPhrAddressService;

    @PostMapping
    public ResponseEntity<?>  createHidPhrAddress(@RequestBody HidPhrAddressDto hidPhrAddressDto) {
        return ResponseEntity.ok(hidPhrAddressService.addHidPhrAddress(hidPhrAddressDto));
    }

    @PatchMapping(value = ABHAEnrollmentDBConstant.UPDATE_HID_PHR_ADDRESS_BY_ID)
    public ResponseEntity<?> updateHidPhrAddress(@RequestBody HidPhrAddressDto hidPhrAddressDto,
                                                 @PathVariable("hidPhrAddressId") Long hidPhrAddressId) {
        return ResponseEntity.ok(hidPhrAddressService.updateHidPhrAddressById(hidPhrAddressDto, hidPhrAddressId));
    }

    @GetMapping(value = ABHAEnrollmentDBConstant.GET_HID_PHR_ADDRESS_BY_ID)
    public ResponseEntity<?> getHidPhrAddress(@PathVariable("hidPhrAddressId") Long hidPhrAddressId) {
        return ResponseEntity.ok(hidPhrAddressService.getHidPhrAddressById(hidPhrAddressId));
    }

    @DeleteMapping(value = ABHAEnrollmentDBConstant.DELETE_HID_PHR_ADDRESS_BY_ID)
    public ResponseEntity<?> deleteHidPhrAddress(@PathVariable("hidPhrAddressId") Long hidPhrAddressId) {
        return ResponseEntity.ok(hidPhrAddressService.deleteHidPhrAddressById(hidPhrAddressId));
    }

    @GetMapping
    public ResponseEntity<?> getHidPhrAddressByHealthIdNumbersAndPreferredIn(
            @RequestParam("healthIdNumber") List<String> healthIdNumbers,
            @RequestParam("preferred") List<Integer> preferred) {
        return ResponseEntity.ok(hidPhrAddressService.getHidPhrAddressByHealthIdNumbersAndPreferredIn(healthIdNumbers, preferred));
    }
    @GetMapping("/check")
    public ResponseEntity<?> findByPhrAddressIn(
            @RequestParam("phrAddress") List<String> phrAddress) {
        return ResponseEntity.ok(hidPhrAddressService.findByPhrAddressIn(phrAddress));
    }

    @GetMapping(value = ABHAEnrollmentDBConstant.GET_HID_PHR_ADDRESS_BY_PHR_ADDRESS)
    public ResponseEntity<?> getPhrAddress(@PathVariable("phrAddress") String phrAddress) {
        return ResponseEntity.ok(hidPhrAddressService.getPhrAddressByPhrAddress(phrAddress));
    }
    @GetMapping(value = ABHAEnrollmentDBConstant.GET_HID_PHR_ADDRESS_BY_HEALTH_ID_NUMBER)
    public ResponseEntity<?> findByHealthIdNumber(@PathVariable("healthIdNumber") String healthIdNumber) {
        return ResponseEntity.ok(hidPhrAddressService.findByHealthIdNumber(healthIdNumber));
    }

}
