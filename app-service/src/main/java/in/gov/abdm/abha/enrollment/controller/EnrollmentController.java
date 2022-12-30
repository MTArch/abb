package in.gov.abdm.abha.enrollment.controller;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentResponseDto;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.EnrolUsingAadhaarService;
import in.gov.abdm.abha.enrollment.services.enrol.driving_licence.EnrolUsingDrivingLicence;
import in.gov.abdm.abha.enrollment.services.enrol_by_document.EnrolByDocumentValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping(URIConstant.ENROL_ENDPOINT)
public class EnrollmentController {

    @Autowired
    EnrolUsingAadhaarService enrolUsingAadhaarService;

    @Autowired
    EnrolUsingDrivingLicence enrolUsingDrivingLicence;

    @Autowired
    EnrolByDocumentValidatorService enrolByDocumentValidatorService;

    @PostMapping(URIConstant.BY_ENROL_AADHAAR_ENDPOINT)
    public Mono<EnrolByAadhaarResponseDto> enrolUsingAadhaar(@Valid @RequestBody EnrolByAadhaarRequestDto enrolByAadhaarRequestDto) {
        return enrolUsingAadhaarService.verifyOtp(enrolByAadhaarRequestDto);
    }

    @PostMapping(URIConstant.ENROL_BY_DOCUMENT_ENDPOINT)
    public Mono<EnrolByDocumentResponseDto> enrolByDocument(@Valid @RequestBody EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        if (enrolByDocumentRequestDto.getDocumentType().equals(AbhaConstants.DRIVING_LICENCE)) {
            enrolByDocumentValidatorService.validateEnrolByDocument(enrolByDocumentRequestDto);
            return enrolUsingDrivingLicence.verifyAndCreateAccount(enrolByDocumentRequestDto);
        }
        return Mono.empty();
    }
}
