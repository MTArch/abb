package in.gov.abdm.abha.enrollment.controller;

import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.exception.application.AbhaBadRequestException;
import in.gov.abdm.abha.enrollment.exception.application.BadRequestException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.demographic.Demographic;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.demographic.DemographicAuth;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.abha_address.request.AbhaAddressRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.abha_address.response.AbhaAddressResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.abha_address.response.SuggestAbhaResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentResponseDto;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.model.notification.SendNotificationRequestDto;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.EnrolUsingAadhaarService;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.bio.EnrolByBioService;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.demographic.EnrolByDemographicService;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.iris.EnrolByIrisService;
import in.gov.abdm.abha.enrollment.services.enrol.abha_address.AbhaAddressService;
import in.gov.abdm.abha.enrollment.services.enrol.child.EnrolChildService;
import in.gov.abdm.abha.enrollment.services.enrol.document.EnrolByDocumentValidatorService;
import in.gov.abdm.abha.enrollment.services.enrol.document.EnrolUsingDrivingLicence;
import in.gov.abdm.abha.enrollment.utilities.BenefitMapper;
import in.gov.abdm.abha.enrollment.utilities.DataMapper;
import in.gov.abdm.abha.enrollment.utilities.RequestMapper;
import in.gov.abdm.error.ABDMError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import java.util.*;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.*;

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
    @Autowired
    EnrolByIrisService enrolByIrisService;
    @Autowired
    EnrolChildService enrolChildService;

    @PostMapping(URIConstant.BY_ENROL_AADHAAR_ENDPOINT)
    public Mono<Object> enrolUsingAadhaar(@RequestHeader(value = REQUEST_ID, required = false) final UUID requestId,
                                          @RequestHeader(value = TIMESTAMP, required = false) final String timestamp,
                                          @Valid @RequestBody EnrolByAadhaarRequestDto enrolByAadhaarRequestDto,
                                          @RequestHeader(value = AbhaConstants.BENEFIT_NAME, required = false) String benefitName,
                                          @RequestHeader(value = AbhaConstants.AUTHORIZATION, required = false) String authorization,
                                          @RequestHeader(value = AbhaConstants.F_TOKEN, required = false) String fToken,
                                          @RequestHeader(value = AbhaConstants.X_TOKEN, required = false) String xToken) {
        List<AuthMethods> authMethods = enrolByAadhaarRequestDto.getAuthData().getAuthMethods();
        RequestHeaders requestHeaders = RequestMapper.prepareRequestHeaders(benefitName, authorization, fToken, xToken);
        return enrolUsingAadhaarService.validateHeaders(requestHeaders, authMethods, fToken)
                .flatMap(aBoolean -> {
                    if (authMethods.contains(AuthMethods.OTP)) {
                        return enrolUsingAadhaarService.verifyOtp(enrolByAadhaarRequestDto, requestHeaders).subscribeOn(Schedulers.parallel());
                    } else if (authMethods.contains(AuthMethods.DEMO)) {
                        enrolByDemographicService.validateEnrolByDemographic(enrolByAadhaarRequestDto, requestHeaders);
                        return enrolByDemographicService.validateAndEnrolByDemoAuth(enrolByAadhaarRequestDto, requestHeaders);
                    } else if (authMethods.contains(AuthMethods.FACE)) {
                        return enrolUsingAadhaarService.faceAuth(enrolByAadhaarRequestDto, requestHeaders);
                    } else if (authMethods.contains(AuthMethods.BIO)) {
                        enrolByBioService.validateEnrolByBio(enrolByAadhaarRequestDto, fToken);
                        return enrolByBioService.verifyBio(enrolByAadhaarRequestDto, requestHeaders);
                    } else if (authMethods.contains(AuthMethods.IRIS)) {
                        enrolByIrisService.validateEnrolByIris(enrolByAadhaarRequestDto);
                        return enrolByIrisService.verifyIris(enrolByAadhaarRequestDto, requestHeaders);
                    } else if (authMethods.contains(AuthMethods.CHILD)) {
                        enrolByDemographicService.validateEnrolChild(enrolByAadhaarRequestDto);
                        return enrolChildService.enrol(enrolByAadhaarRequestDto, requestHeaders);
                    } else if (authMethods.contains(AuthMethods.DEMO_AUTH)) {
                        DemographicAuth demoAuth = enrolByAadhaarRequestDto.getAuthData().getDemographicAuth();
                        if(null == demoAuth){
                            throw new AbhaBadRequestException(ABDMError.INVALID_COMBINATIONS_OF_SCOPES.getCode(), ABDMError.INVALID_COMBINATIONS_OF_SCOPES.getMessage());
                        }
                        enrolByDemographicService.validateEnrolByDemographic(demoAuth, requestHeaders);
                        Demographic demographic = new DataMapper<Demographic>().mapper(demoAuth, Demographic.class);
                        if (demoAuth.getDateOfBirth().length() == 4) {
                            demographic.setYearOfBirth(demoAuth.getDateOfBirth());
                        } else {
                            String[] parts = demoAuth.getDateOfBirth().split("-");
                            demographic.setDayOfBirth(parts[0]);
                            demographic.setMonthOfBirth(parts[1]);
                            demographic.setYearOfBirth(parts[2]);
                        }
                        demographic.setFirstName(demoAuth.getName());
                        demographic.setConsentFormImage(demoAuth.getProfilePhoto());
                        enrolByAadhaarRequestDto.getAuthData().setDemographic(demographic);
                        return enrolByDemographicService.validateAndEnrolByDemoAuth(enrolByAadhaarRequestDto, requestHeaders)
                                .map(BenefitMapper::mapHidBenefitRequestPayload);
                    }
                    throw new AbhaBadRequestException(ABDMError.INVALID_COMBINATIONS_OF_SCOPES.getCode(), ABDMError.INVALID_COMBINATIONS_OF_SCOPES.getMessage());
                });
    }

    @PostMapping(URIConstant.ENROL_BY_DOCUMENT_ENDPOINT)
    public Mono<EnrolByDocumentResponseDto> enrolByDocument(@RequestHeader(value = REQUEST_ID, required = false) final UUID requestId,
                                                            @RequestHeader(value = TIMESTAMP, required = false) final String timestamp,
                                                            @Valid @RequestBody EnrolByDocumentRequestDto enrolByDocumentRequestDto,
                                                            @RequestHeader(value = AbhaConstants.F_TOKEN, required = false) String fToken,
                                                            @RequestHeader(value = AbhaConstants.AUTHORIZATION, required = false) String authorization) {

        List<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.WRONG);
        RequestHeaders requestHeaders = RequestMapper.prepareRequestHeaders(null, authorization, fToken, null);
        return enrolUsingAadhaarService.validateHeaders(requestHeaders, authMethods, fToken)
                .flatMap(aBoolean -> {
                    if (enrolByDocumentRequestDto.getDocumentType().equals(AbhaConstants.DRIVING_LICENCE)) {
                        enrolByDocumentValidatorService.validateEnrolByDocument(enrolByDocumentRequestDto);
                        return enrolUsingDrivingLicence.verifyAndCreateAccount(enrolByDocumentRequestDto, requestHeaders);
                    } else {
                        throw new BadRequestException(new LinkedHashMap<>(Collections.singletonMap(AbhaConstants.DOCUMENT_TYPE, AbhaConstants.INVALID_DOCUMENT_TYPE)));
                    }
                });
    }

    @GetMapping(URIConstant.ENROL_SUGGEST_ABHA_ENDPOINT)
    public Mono<SuggestAbhaResponseDto> getAbhaAddressSuggestion(@RequestHeader(value = REQUEST_ID, required = false) final UUID requestId,
                                                                 @RequestHeader(value = TIMESTAMP, required = false) final String timestamp,
                                                                 @RequestHeader(value = TRANSACTION_ID) String txnId) {
        abhaAddressService.validateRequest(txnId);
        return abhaAddressService.getAbhaAddress(txnId);
    }

    @PostMapping(URIConstant.ENROL_ABHA_ADDRESS_ENDPOINT)
    public Mono<AbhaAddressResponseDto> createAbhaAddress(@RequestHeader(value = REQUEST_ID, required = false) final UUID requestId,
                                                          @RequestHeader(value = TIMESTAMP, required = false) final String timestamp,
                                                          @Valid @RequestBody AbhaAddressRequestDto abhaAddressRequestDto) {
        abhaAddressService.validateAbhaAddress(abhaAddressRequestDto);
        return abhaAddressService.createAbhaAddress(abhaAddressRequestDto);
    }

    @PostMapping(URIConstant.ENROL_REQUEST_NOTIFICATION_ENDPOINT)
    public Mono<String> requestNotification(@RequestHeader(value = REQUEST_ID, required = false) final UUID requestId,
                                            @RequestHeader(value = TIMESTAMP, required = false) final String timestamp,
                                            @Valid @RequestBody SendNotificationRequestDto sendNotificationRequestDto,
                                            @RequestHeader(value = AbhaConstants.AUTHORIZATION, required = false) String authorization) {

        RequestHeaders requestHeaders = RequestMapper.prepareRequestHeaders(null, authorization, null, null);
        enrolUsingAadhaarService.validateNotificationRequest(sendNotificationRequestDto);
        return enrolUsingAadhaarService.requestNotification(sendNotificationRequestDto, requestHeaders);
    }
}
