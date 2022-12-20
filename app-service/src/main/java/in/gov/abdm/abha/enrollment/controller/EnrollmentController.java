package in.gov.abdm.abha.enrollment.controller;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentResponseDto;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.EnrolUsingAadhaarService;
<<<<<<< HEAD
import in.gov.abdm.abha.enrollment.services.enrol.driving_licence.EnrolUsingDrivingLicence;
=======
import in.gov.abdm.abha.enrollment.services.enrol_by_document.EnrolByDocumentValidatorService;
>>>>>>> d125c63 (subtask-abha-CAI-3-dl-validations completed.)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.DocumentType;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping(URIConstant.ENROL_ENDPOINT)
public class EnrollmentController {

    @Autowired
    EnrolUsingAadhaarService enrolUsingAadhaarService;

    @Autowired
<<<<<<< HEAD
    EnrolUsingDrivingLicence enrolUsingDrivingLicence;
=======
    EnrolByDocumentValidatorService enrolByDocumentValidatorService;
>>>>>>> d125c63 (subtask-abha-CAI-3-dl-validations completed.)

    @PostMapping(URIConstant.BY_ENROL_AADHAAR_ENDPOINT)
    public Mono<EnrolByAadhaarResponseDto> enrolUsingAadhaar(@Valid @RequestBody EnrolByAadhaarRequestDto enrolByAadhaarRequestDto) {
        return enrolUsingAadhaarService.verifyOtp(enrolByAadhaarRequestDto);
    }

    @PostMapping(URIConstant.ENROL_BY_DOCUMENT_ENDPOINT)
<<<<<<< HEAD
    public Mono<EnrolByDocumentResponseDto> enrolByDocument(@Valid @RequestBody EnrolByDocumentRequestDto enrolByDocumentRequestDto){
        if(enrolByDocumentRequestDto.getDocumentType().equals(AbhaConstants.DRIVING_LICENCE)) {
             return enrolUsingDrivingLicence.verifyAndCreateAccount(enrolByDocumentRequestDto);
        }
=======
    public Mono<EnrolByDocumentResponseDto> enrolByDocument(@Valid @RequestBody EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        enrolByDocumentValidatorService.validateEnrolByDocument(enrolByDocumentRequestDto);
>>>>>>> d125c63 (subtask-abha-CAI-3-dl-validations completed.)
        return Mono.empty();
    }
}
