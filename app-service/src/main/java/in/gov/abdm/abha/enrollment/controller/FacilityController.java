package in.gov.abdm.abha.enrollment.controller;

import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.enums.request.OtpSystem;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.exception.application.AbhaBadRequestException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.request.AuthRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AuthResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.facility.EnrollmentResponse;
import in.gov.abdm.abha.enrollment.model.enrol.facility.EnrollmentStatusUpdate;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpResponseDto;
import in.gov.abdm.abha.enrollment.services.auth.abdm.AuthByAbdmService;
import in.gov.abdm.abha.enrollment.services.facility.FacilityRequestService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import in.gov.abdm.error.ABDMError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static in.gov.abdm.abha.enrollment.constants.URIConstant.VERIFY_ENROLLMENT_ENDPOINT;
import static in.gov.abdm.abha.enrollment.constants.URIConstant.VERIFY_FACILITY_OTP_ENDPOINT;

@RestController
@CrossOrigin
@RequestMapping(URIConstant.FACILITY_ENDPOINT)
public class FacilityController {

    @Autowired
    FacilityRequestService facilityRequestService;

    @Autowired
    RSAUtil rsaUtil;

    @Autowired
    AuthByAbdmService authByAbdmService;

    /**
     * endpoint to generate mobile or email otp for abha creation using aadhaar
     *
     * @param mobileOrEmailOtpRequestDto
     * @return txnId and success or failed message as part of responseDto
     */
    @PostMapping(URIConstant.FACILITY_OTP_ENDPOINT)
    public Mono<MobileOrEmailOtpResponseDto> mobileOrEmailOtp(@Valid @RequestBody MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto) {

        //filter scope
        List<Scopes> requestScopes = mobileOrEmailOtpRequestDto.getScope().stream().distinct().collect(Collectors.toList());
        // If scope -abha-enrol and verify-enrolment and otpSystem -abdm
        if (Common.isAllScopesAvailable(requestScopes, List.of(Scopes.ABHA_ENROL, Scopes.VERIFY_ENROLMENT))
                && mobileOrEmailOtpRequestDto.getOtpSystem().equals(OtpSystem.ABDM)) {
            return facilityRequestService.sendOtpForEnrollmentNumberService(mobileOrEmailOtpRequestDto);
        }
        // other case
        else {
            throw new AbhaBadRequestException(ABDMError.INVALID_COMBINATIONS_OF_SCOPES.getCode(), ABDMError.INVALID_COMBINATIONS_OF_SCOPES.getMessage());
        }
    }

    @GetMapping("enc/{encrypt}")
    public String getEncrypted(@Valid @PathVariable String encrypt) {

        return rsaUtil.encrypt(encrypt);
    }

    @GetMapping(URIConstant.FACILITY_PROFILE_DETAILS_BY_ENROLLMENT_NUMBER_ENDPOINT)
    public Mono<EnrolByDocumentResponseDto> getDetailsByEnrolmentNumber(@Valid @PathVariable String enrollmentNumber) {
        return facilityRequestService.fetchDetailsByEnrollmentNumber(enrollmentNumber);
    }

    @PostMapping(VERIFY_FACILITY_OTP_ENDPOINT)
    public Mono<AuthResponseDto> authByAbdm(@Valid @RequestBody AuthRequestDto authByAbdmRequest) {
        authByAbdmRequest.getAuthData().getOtp().setOtpValue(rsaUtil.decrypt(authByAbdmRequest.getAuthData().getOtp().getOtpValue()));
        if (Common.isAllScopesAvailable(authByAbdmRequest.getScope(), List.of(Scopes.ABHA_ENROL, Scopes.VERIFY_ENROLMENT))) {
            return authByAbdmService.verifyOtpViaNotificationDLFlow(authByAbdmRequest);
        } else {
            throw new AbhaBadRequestException(ABDMError.INVALID_COMBINATIONS_OF_SCOPES.getCode(), ABDMError.INVALID_COMBINATIONS_OF_SCOPES.getMessage());
        }

    }

    @PostMapping(VERIFY_ENROLLMENT_ENDPOINT)
    public Mono<EnrollmentResponse> verifyEnrollment(@Valid @RequestBody EnrollmentStatusUpdate enrollmentStatusUpdate) {
        return facilityRequestService.verifyFacilityByEnroll(enrollmentStatusUpdate);
    }

}
