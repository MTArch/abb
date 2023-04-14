package in.gov.abdm.abha.enrollment.controller;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.exception.application.AbhaBadRequestException;
import in.gov.abdm.abha.enrollment.exception.application.BadRequestException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.abha_address.request.AbhaAddressRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.abha_address.response.AbhaAddressResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.abha_address.response.SuggestAbhaResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentResponseDto;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.EnrolUsingAadhaarService;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.bio.EnrolByBioService;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.demographic.EnrolByDemographicService;
import in.gov.abdm.abha.enrollment.services.enrol.abha_address.AbhaAddressService;
import in.gov.abdm.abha.enrollment.services.enrol.document.EnrolUsingDrivingLicence;
import in.gov.abdm.abha.enrollment.services.enrol.document.EnrolByDocumentValidatorService;
import in.gov.abdm.error.ABDMError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.TRANSACTION_ID;

@RestController
@CrossOrigin
@RequestMapping(URIConstant.ENROL_ENDPOINT)
@ResponseStatus(HttpStatus.OK)
public class EnrollmentController {

    @Autowired
    EnrolUsingAadhaarService enrolUsingAadhaarService;

    @Autowired
    EnrolUsingDrivingLicence enrolUsingDrivingLicence;

    @Autowired
    EnrolByDocumentValidatorService enrolByDocumentValidatorService;

    @Autowired
    AbhaAddressService abhaAddressService;

    @Autowired
    EnrolByDemographicService enrolByDemographicService;
    @Autowired
    EnrolByBioService enrolByBioService;

    @PostMapping(URIConstant.BY_ENROL_AADHAAR_ENDPOINT)
    public Mono<EnrolByAadhaarResponseDto> enrolUsingAadhaar(@Valid @RequestBody EnrolByAadhaarRequestDto enrolByAadhaarRequestDto) {
        List<AuthMethods> authMethods = enrolByAadhaarRequestDto.getAuthData().getAuthMethods();
        if (authMethods.contains(AuthMethods.OTP)) {
            return enrolUsingAadhaarService.verifyOtp(enrolByAadhaarRequestDto).subscribeOn(Schedulers.parallel());
        } else if (authMethods.contains(AuthMethods.DEMO)) {
            enrolByDemographicService.validateEnrolByDemographic(enrolByAadhaarRequestDto);
            return enrolByDemographicService.validateAndEnrolByDemoAuth(enrolByAadhaarRequestDto);
        } else if (authMethods.contains(AuthMethods.FACE)) {
            return enrolUsingAadhaarService.faceAuth(enrolByAadhaarRequestDto);
        }else if(authMethods.contains(AuthMethods.BIO)){
            enrolByBioService.validateEnrolByBio(enrolByAadhaarRequestDto);
            return enrolByBioService.verifyBio(enrolByAadhaarRequestDto);
        }
        throw new AbhaBadRequestException(ABDMError.INVALID_COMBINATIONS_OF_SCOPES.getCode(), ABDMError.INVALID_COMBINATIONS_OF_SCOPES.getMessage());
    }

    @PostMapping(URIConstant.ENROL_BY_DOCUMENT_ENDPOINT)
    public Mono<EnrolByDocumentResponseDto> enrolByDocument(@Valid @RequestBody EnrolByDocumentRequestDto enrolByDocumentRequestDto,
                                                            @RequestHeader(value = "F-token", required = false) String fToken) {
        if (enrolByDocumentRequestDto.getDocumentType().equals(AbhaConstants.DRIVING_LICENCE)) {
            enrolByDocumentValidatorService.validateEnrolByDocument(enrolByDocumentRequestDto);
            return enrolUsingDrivingLicence.verifyAndCreateAccount(enrolByDocumentRequestDto, fToken);
        } else {
            throw new BadRequestException(new LinkedHashMap<>(Collections.singletonMap(AbhaConstants.DOCUMENT_TYPE, AbhaConstants.INVALID_DOCUMENT_TYPE)));
        }
    }

    @GetMapping(URIConstant.ENROL_SUGGEST_ABHA_ENDPOINT)
    public Mono<SuggestAbhaResponseDto> getAbhaAddressSuggestion(
            @RequestHeader(value = TRANSACTION_ID) String txnId) {
        abhaAddressService.validateRequest(txnId);
        return abhaAddressService.getAbhaAddress(txnId);
    }

    @PostMapping(URIConstant.ENROL_ABHA_ADDRESS_ENDPOINT)
    public Mono<AbhaAddressResponseDto> createAbhaAddress(@Valid @RequestBody AbhaAddressRequestDto abhaAddressRequestDto) {
        return abhaAddressService.createAbhaAddress(abhaAddressRequestDto);
    }
}
