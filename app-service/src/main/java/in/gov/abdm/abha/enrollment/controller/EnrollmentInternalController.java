package in.gov.abdm.abha.enrollment.controller;

import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.pid.PidDto;
import in.gov.abdm.abha.enrollment.services.enrol.pid.PidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.TRANSACTION_ID;

@RestController
@CrossOrigin
@RequestMapping(URIConstant.ENROL_ENDPOINT)
@ResponseStatus(HttpStatus.OK)
public class EnrollmentInternalController {

    @Autowired
    PidService pidService;

    @PostMapping(URIConstant.ENROL_ABHA_RD_PID)
    public Mono<EnrolByAadhaarResponseDto> addPidToRedis(@Valid @RequestBody PidDto pidDto) {
        return pidService.addPidAndScanStatus(pidDto);
    }

    @GetMapping(URIConstant.ENROL_ABHA_RD_PID)
    public Mono<PidDto> addPidToRedis(@Valid @RequestHeader(value = TRANSACTION_ID) String txnId) {
        return pidService.getPidAndScanStatus(txnId);
    }
}
