package in.gov.abdm.abha.enrollmentdb.controller;


import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.domain.HidPhrAddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollmentdb.model.HidPhrAddress.HidPhrAddressDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(ABHAEnrollmentDBConstant.HID_PHR_ADDRESS_ENDPOINT)
@RestController
public class HidPhrAddressController {

    @Autowired
    HidPhrAddressService hidPhrAddressService;

    @GetMapping(value = ABHAEnrollmentDBConstant.GET_HID_PHR_ADDRESS_BY_ID)
    public ResponseEntity<?>getHidPhrAddress(@PathVariable("hidPhrAddressId") Long hidPhrAddressId){
        return ResponseEntity.ok(hidPhrAddressService.getHidPhrAddressById(hidPhrAddressId));
    }

    @DeleteMapping(value = ABHAEnrollmentDBConstant.DELETE_HID_PHR_ADDRESS_BY_ID)
    public ResponseEntity<?>deleteHidPhrAddress(@RequestBody HidPhrAddressDto hidPhrAddressDto,
                                                @PathVariable("hidPhrAddressId") Long hidPhrAddressId){
        return ResponseEntity.ok(hidPhrAddressService.deleteHidPhrAddressById(hidPhrAddressDto,hidPhrAddressId));
    }
}
