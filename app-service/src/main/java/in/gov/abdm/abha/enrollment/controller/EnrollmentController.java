package in.gov.abdm.abha.enrollment.controller;

import in.gov.abdm.abha.enrollment.constants.EnrollConstant;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.EnrolUsingAadhaarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping(EnrollConstant.ENROL_ENDPOINT)
public class EnrollmentController {

    @Autowired
    EnrolUsingAadhaarService enrolUsingAadhaarService;

    @PostMapping(EnrollConstant.BY_ENROL_AADHAAR_ENDPOINT)
    public Mono<EnrolByAadhaarResponseDto> enrolUsingAadhaar(@Valid @RequestBody EnrolByAadhaarRequestDto enrolByAadhaarRequestDto){
        return enrolUsingAadhaarService.verifyOtp(enrolByAadhaarRequestDto);
    }
}
