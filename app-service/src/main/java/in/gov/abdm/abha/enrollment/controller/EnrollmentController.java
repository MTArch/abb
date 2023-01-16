package in.gov.abdm.abha.enrollment.controller;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.exception.application.GenericExceptionMessage;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.abha_address.request.AbhaAddressRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.abha_address.response.AbhaAddressResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.abha_address.response.SuggestAbhaResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentResponseDto;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.EnrolUsingAadhaarService;
import in.gov.abdm.abha.enrollment.services.enrol.abha_address.AbhaAddressService;
import in.gov.abdm.abha.enrollment.services.enrol.driving_licence.EnrolUsingDrivingLicence;
import in.gov.abdm.abha.enrollment.services.enrol_by_document.EnrolByDocumentValidatorService;
import in.gov.abdm.abha.enrollment.validators.annotations.Uuid;
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

    @Autowired
    AbhaAddressService abhaAddressService;

    @PostMapping(URIConstant.BY_ENROL_AADHAAR_ENDPOINT)
    public Mono<EnrolByAadhaarResponseDto> enrolUsingAadhaar(@Valid @RequestBody EnrolByAadhaarRequestDto enrolByAadhaarRequestDto) {
        return enrolUsingAadhaarService.verifyOtp(enrolByAadhaarRequestDto);
    }

    @PostMapping(URIConstant.ENROL_BY_DOCUMENT_ENDPOINT)
    public Mono<EnrolByDocumentResponseDto> enrolByDocument(@Valid @RequestBody EnrolByDocumentRequestDto enrolByDocumentRequestDto) {
        if (enrolByDocumentRequestDto.getDocumentType().equals(AbhaConstants.DRIVING_LICENCE)) {
            enrolByDocumentValidatorService.validateEnrolByDocument(enrolByDocumentRequestDto);
            return enrolUsingDrivingLicence.verifyAndCreateAccount(enrolByDocumentRequestDto);
        }else{
            throw new GenericExceptionMessage(AbhaConstants.INVALID_DOCUMENT_TYPE);
        }
    }

    @GetMapping(URIConstant.ENROL_SUGGEST_ABHA_ENDPOINT)
    public Mono<SuggestAbhaResponseDto> getAbhaAddressSuggestion(@PathVariable("txnId") String txnId) {
        abhaAddressService.validateRequest(txnId);
        return abhaAddressService.getAbhaAddress(txnId);
    }

    @PostMapping(URIConstant.ENROL_ABHA_ADDRESS_ENDPOINT)
    public Mono<AbhaAddressResponseDto> createAbhaAddress(@Valid @RequestBody AbhaAddressRequestDto abhaAddressRequestDto) {
        return abhaAddressService.createAbhaAddress(abhaAddressRequestDto);
    }
}
