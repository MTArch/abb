package in.gov.abdm.abha.enrollment.services.enrol.aadhaar.demographic;

import in.gov.abdm.abha.enrollment.client.HidBenefitDBFClient;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.PropertyConstants;
import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.enums.AccountAuthMethods;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.enums.childabha.AbhaType;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AadhaarMethod;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.enums.hidbenefit.HidBenefitStatus;
import in.gov.abdm.abha.enrollment.enums.request.AadhaarLogType;
import in.gov.abdm.abha.enrollment.exception.abha_db.AbhaDBGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.exception.application.AbhaUnProcessableException;
import in.gov.abdm.abha.enrollment.exception.application.BadRequestException;
import in.gov.abdm.abha.enrollment.exception.notification.NotificationGatewayUnavailableException;
import in.gov.abdm.abha.enrollment.model.aadhaar.verify_demographic.VerifyDemographicRequest;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.demographic.Demographic;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ResponseTokensDto;
import in.gov.abdm.abha.enrollment.model.entities.*;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.model.lgd.LgdDistrictResponse;
import in.gov.abdm.abha.enrollment.model.procedure.SaveAllDataRequest;
import in.gov.abdm.abha.enrollment.services.aadhaar.AadhaarAppService;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.account_auth_methods.AccountAuthMethodService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.de_duplication.DeDuplicationService;
import in.gov.abdm.abha.enrollment.services.document.IdentityDocumentDBService;
import in.gov.abdm.abha.enrollment.services.notification.NotificationService;
import in.gov.abdm.abha.enrollment.services.redis.RedisService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import in.gov.abdm.abha.enrollment.utilities.LgdUtility;
import in.gov.abdm.abha.enrollment.utilities.MapperUtils;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaAddressGenerator;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaNumberGenerator;
import in.gov.abdm.abha.enrollment.utilities.jwt.JWTUtil;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import in.gov.abdm.error.ABDMError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.*;
import static in.gov.abdm.abha.enrollment.constants.StringConstants.DEMO_AUTH;

@Slf4j
@Service
@SuppressWarnings("java:S3776")
public class EnrolByDemographicService extends EnrolByDemographicValidatorService {

    public static final int N10DigitMobileNumber = 10;
    public static final int N12DigitMobileNumber = 12;
    public static final int N13DigitMobileNumber = 13;
    public static final String N91 = "91";
    public static final String N091 = "091";
    public static final String PREFIX_PLUS_91 = "+91";
    public static final int BEGIN_INDEX_3 = 3;
    public static final int BEGIN_INDEX_2 = 2;
    @Autowired
    private AadhaarAppService aadhaarAppService;
    @Autowired
    private RSAUtil rsaUtils;
    @Autowired
    private AccountService accountService;
    @Autowired
    HidBenefitDBFClient hidBenefitDBFClient;
    @Autowired
    RedisService redisService;
    @Autowired
    private HidPhrAddressService hidPhrAddressService;
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private AbhaAddressGenerator abhaAddressGenerator;
    @Autowired
    private AccountAuthMethodService accountAuthMethodService;
    @Autowired
    NotificationService notificationService;
    @Autowired
    IdentityDocumentDBService identityDocumentDBService;
    @Autowired
    LgdUtility lgdUtility;
    @Autowired
    DeDuplicationService deDuplicationService;
    @Value(PropertyConstants.ENROLLMENT_MAX_MOBILE_LINKING_COUNT)
    private int maxMobileLinkingCount;
    @Value(PropertyConstants.ENROLLMENT_IS_TRANSACTION)
    private boolean isTransactionManagementEnable;

    private static final String STATE_DISTRICT = "districtCode";
    private String onlyDigitRegex = "^[0-9]*$";
    private static final String STATE = "State";
    private static final String DISTRICT = "District";

    private String validateMobile(String mobile) {
        if (mobile == null) {
            return null;
        }
        if (mobile.length() == N10DigitMobileNumber && mobile.matches(onlyDigitRegex)) {
            return mobile;
        } else if (mobile.length() == N12DigitMobileNumber && mobile.startsWith(N91) && mobile.matches(onlyDigitRegex)) {
            return mobile.substring(BEGIN_INDEX_2);
        } else if (mobile.length() == N13DigitMobileNumber && (mobile.startsWith(N091) || mobile.startsWith(PREFIX_PLUS_91)) && mobile.substring(BEGIN_INDEX_3).matches(onlyDigitRegex)) {
            return mobile.substring(BEGIN_INDEX_3);
        }
        return null;
    }

    public Mono<EnrolByAadhaarResponseDto> validateAndEnrolByDemoAuth(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, RequestHeaders requestHeaders) {
        Demographic demographic = enrolByAadhaarRequestDto.getAuthData().getDemographic();
        VerifyDemographicRequest verifyDemographicRequest = new VerifyDemographicRequest();
        verifyDemographicRequest.setAadhaarNumber(rsaUtils.decrypt(demographic.getAadhaarNumber()));
        List<AuthMethods> authMethods = enrolByAadhaarRequestDto.getAuthData().getAuthMethods();
        verifyDemographicRequest.setName(Common.removeNulls(Common.getName(demographic.getFirstName(), demographic.getMiddleName(), demographic.getLastName())));
        String dob = Objects.nonNull(demographic.getMonthOfBirth()) ? formatDob(demographic.getYearOfBirth(), demographic.getMonthOfBirth(), demographic.getDayOfBirth()) : Common.removeNulls(demographic.getYearOfBirth());
        verifyDemographicRequest.setDob(dob);
        verifyDemographicRequest.setGender(demographic.getGender());
        verifyDemographicRequest.setAadhaarLogType(AadhaarLogType.KYC_D_AUTH.name());
        String mobileNumber = enrolByAadhaarRequestDto.getAuthData().getDemographic().getMobile();
        Mono validateStateCode = Mono.just("");
        return validateStateCode.flatMap(data -> {
            Mono<Integer> mobileLinkedAccountCountMono = Mono.just(0);
            return mobileLinkedAccountCountMono.flatMap(mobileLinkedAccountCount -> {
                if (mobileLinkedAccountCount >= maxMobileLinkingCount) {
                    throw new AbhaUnProcessableException(ABDMError.MOBILE_ALREADY_LINKED_TO_6_ACCOUNTS);
                } else {
                    return aadhaarAppService.verifyDemographicDetails(verifyDemographicRequest)
                            .flatMap(verifyDemographicResponse -> {
                                if (verifyDemographicResponse.isVerified()) {
                                    // check if account exist
                                    return accountService.findByXmlUid(verifyDemographicResponse.getXmlUid())
                                            .flatMap(existingAccount -> {
                                                if (existingAccount.getStatus().equals(AccountStatus.DELETED.getValue())) {
                                                    return createNewAccount(enrolByAadhaarRequestDto, verifyDemographicResponse.getXmlUid(), requestHeaders);
                                                } else if (existingAccount.getStatus().equals(AccountStatus.DEACTIVATED.getValue())) {
                                                    return respondExistingAccount(existingAccount, false, AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST_AND_DEACTIVATED, requestHeaders);
                                                } else {
                                                    // existing account
                                                    return respondExistingAccount(existingAccount, true, AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST, requestHeaders);
                                                }
                                            })
                                            .switchIfEmpty(Mono.defer(() -> createNewAccount(enrolByAadhaarRequestDto, verifyDemographicResponse.getXmlUid(), requestHeaders)));
                                } else {
                                    throw new AbhaUnProcessableException(ABDMError.INVALID_DEMOGRAPHIC_DETAILS);
                                }
                            });
                }
            });
        });
    }

    private VerifyDemographicRequest setDemoAuth(String name, String gender, String aadhaar, String yearofBrith) {
        return VerifyDemographicRequest.builder().name(name).gender(gender).aadhaarNumber(aadhaar).dob(yearofBrith).build();
    }


    private Mono<EnrolByAadhaarResponseDto> createNewAccount(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, String xmlUid, RequestHeaders requestHeaders) {
        Demographic demographicdto = enrolByAadhaarRequestDto.getAuthData().getDemographic();
        List<AuthMethods> authMethods = enrolByAadhaarRequestDto.getAuthData().getAuthMethods();
        // Use the breakName method to process demographic data asynchronously
        return breakName(demographicdto, authMethods)
                .flatMap(demographic -> {
                    LinkedHashMap<String, String> errors = new LinkedHashMap<>();
                    if (StringUtils.isEmpty(demographic.getStateCode()) || !isValidState(demographic.getStateCode())) {
                        errors.put(STATE, AbhaConstants.INVALID_STATE);
                    }
                    if (StringUtils.isEmpty(demographic.getDistrictCode()) || !isValidDistrict(demographic.getDistrictCode())) {
                        errors.put(DISTRICT, AbhaConstants.INVALID_DISTRICT);
                    }
                    if (!errors.isEmpty()) {
                        throw new BadRequestException(errors);
                    }
                    String newAbhaNumber = AbhaNumberGenerator.generateAbhaNumber();
                    String defaultAbhaAddress = abhaAddressGenerator.generateDefaultAbhaAddress(newAbhaNumber);
                    AccountDto accountDto = new AccountDto();
                    accountDto.setFirstName(demographic.getFirstName() != null ? demographic.getFirstName().trim() : null);
                    accountDto.setMiddleName(demographic.getMiddleName() != null ? demographic.getMiddleName().trim() : null);
                    accountDto.setLastName(demographic.getLastName() != null ? demographic.getLastName().trim() : null);
                    accountDto.setName(Common.removeNulls(Common.getName(accountDto.getFirstName(), accountDto.getMiddleName(), accountDto.getLastName())));
                    accountDto.setConsentDate(LocalDateTime.now());
                    accountDto.setVerificationStatus(AbhaConstants.VERIFIED);
                    if (authMethods.contains(AuthMethods.DEMO_AUTH) && isValidProfilePhoto(demographic.getConsentFormImage())) {
                        accountDto.setProfilePhoto(demographic.getConsentFormImage());
                    } else {
                        accountDto.setVerificationType(AbhaConstants.OFFLINE_AADHAAR);
                    }
                    accountDto.setVerificationType(AbhaConstants.OFFLINE_AADHAAR);
                    accountDto.setYearOfBirth(demographic.getYearOfBirth());
                    accountDto.setMonthOfBirth(preFixZero(demographic.getMonthOfBirth()));
                    accountDto.setDayOfBirth(preFixZero(demographic.getDayOfBirth()));
                    accountDto.setKycdob(Common.getDob(accountDto.getDayOfBirth(), accountDto.getMonthOfBirth(), accountDto.getYearOfBirth()));
                    accountDto.setAddress(demographic.getAddress() != null ? demographic.getAddress().trim() : null);
                    accountDto.setGender(demographic.getGender());
                    accountDto.setConsentVersion(enrolByAadhaarRequestDto.getConsent().getVersion());
                    accountDto.setConsentDate(LocalDateTime.now());
                    accountDto.setHealthId(defaultAbhaAddress);
                    accountDto.setXmluid(xmlUid);
                    accountDto.setPincode(demographic.getPinCode());
                    accountDto.setHealthIdNumber(newAbhaNumber);
                    accountDto.setMobile(validateMobile(demographic.getMobile()));
                    accountDto.setHealthWorkerName(demographic.getHealthWorkerName());
                    accountDto.setHealthWorkerMobile(demographic.getHealthWorkerMobile());
                    accountDto.setStatus(AccountStatus.ACTIVE.getValue());
                    String mobileType = Objects.nonNull(demographic.getMobileType()) ? demographic.getMobileType().getValue() : null;
                    accountDto.setMobileType(mobileType);
                    accountDto.setSource(DEMO_AUTH);
                    accountDto.setApiEndPoint(URIConstant.ENROL_ENDPOINT + URIConstant.BY_ENROL_AADHAAR_ENDPOINT);
                    accountDto.setKycVerified(true);
                    accountDto.setFacilityId(requestHeaders.getFTokenClaims() != null && requestHeaders.getFTokenClaims().get(SUB) != null ? requestHeaders.getFTokenClaims().get(SUB).toString() : null);
                    return deDuplicationService.checkDeDuplication(deDuplicationService.prepareRequest(accountDto))
                            .flatMap(duplicateAccount -> respondExistingAccount(duplicateAccount, false, AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST, requestHeaders))
                            .switchIfEmpty(Mono.defer(() -> {
                                int age = Common.calculateYearDifference(accountDto.getYearOfBirth(), accountDto.getMonthOfBirth(), accountDto.getDayOfBirth());
                                if (age >= 18) {
                                    accountDto.setType(AbhaType.STANDARD);
                                } else {
                                    accountDto.setType(AbhaType.CHILD);
                                }
                                if (authMethods.contains(AuthMethods.DEMO_AUTH)) {
                                    return lgdUtility.getDistrictCode(demographic.getDistrictCode()).
                                            flatMap(res -> {
                                                if (Objects.isNull(res) || res.stream()
                                                        .anyMatch(vale -> !vale.getStateCode().equals(demographic.getStateCode()))) {
                                                    LinkedHashMap<String, String> error = new LinkedHashMap<>();
                                                    error.put(STATE_DISTRICT, AbhaConstants.INVALID_STATE_DISTRICT);
                                                    throw new BadRequestException(error);
                                                }
                                                return setLdgData(enrolByAadhaarRequestDto, requestHeaders, demographic, res, accountDto);
                                            });
                                } else {
                                    return lgdUtility.getLgdData(demographic.getPinCode(), demographic.getState())
                                            .flatMap(lgdDistrictResponses -> {
                                                if (!lgdDistrictResponses.isEmpty()) {
                                                    // Process LGD data
                                                    LgdDistrictResponse lgdDistrictResponse = Common.getLGDDetails(lgdDistrictResponses);
                                                    accountDto.setDistrictCode(lgdDistrictResponse.getDistrictCode());
                                                    accountDto.setDistrictName(lgdDistrictResponse.getDistrictName() == null || lgdDistrictResponse.getDistrictName().equalsIgnoreCase("Unknown") ? demographic.getDistrict() : lgdDistrictResponse.getDistrictName());
                                                    accountDto.setStateCode(lgdDistrictResponse.getStateCode());
                                                    accountDto.setStateName(lgdDistrictResponse.getStateName());
                                                } else {
                                                    accountDto.setDistrictName(demographic.getDistrict());
                                                    accountDto.setStateName(demographic.getState());
                                                }
                                                return saveAccountDetails(enrolByAadhaarRequestDto, accountDto, demographic.getConsentFormImage(), requestHeaders);
                                            }).switchIfEmpty(lgdUtility.getDistrictCode(demographic.getDistrictCode()).flatMap(res -> setLdgData(enrolByAadhaarRequestDto, requestHeaders, demographic, res, accountDto)));
                                }
                            }));
                });
    }

    private boolean isValidState(String state) {
        return StringUtils.isNotEmpty(state) && state.matches(onlyDigitRegex);
    }

    private boolean isValidDistrict(String district) {
        return StringUtils.isNotEmpty(district) && district.matches(onlyDigitRegex);
    }

    private Mono<EnrolByAadhaarResponseDto> setLdgData(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, RequestHeaders requestHeaders, Demographic demographic, List<LgdDistrictResponse> res, AccountDto accountDto) {
        LgdDistrictResponse lgdDistrictResponse = Common.getLGDDetails(res);
        accountDto.setDistrictCode(lgdDistrictResponse.getDistrictCode());
        accountDto.setDistrictName(lgdDistrictResponse.getDistrictName() == null || lgdDistrictResponse.getDistrictName().equalsIgnoreCase("Unknown") ? demographic.getDistrict() : lgdDistrictResponse.getDistrictName());
        accountDto.setStateCode(lgdDistrictResponse.getStateCode());
        accountDto.setStateName(lgdDistrictResponse.getStateName());
        return saveAccountDetails(enrolByAadhaarRequestDto, accountDto, demographic.getConsentFormImage(), requestHeaders);
    }

    private Mono<EnrolByAadhaarResponseDto> saveAccountDetails(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, AccountDto accountDto, String consentFormImage, RequestHeaders requestHeaders) {
        if (isTransactionManagementEnable) {
            return accountService.settingClientIdAndOrigin(enrolByAadhaarRequestDto, accountDto, requestHeaders)
                    .flatMap(accountDtoResponse -> callProcedureToCreateAccount(accountDtoResponse, requestHeaders, enrolByAadhaarRequestDto));
        } else {
            return accountService.createAccountEntity(enrolByAadhaarRequestDto, accountDto, requestHeaders).flatMap(accountDtoResponse -> {
                HidPhrAddressDto hidPhrAddressDto = hidPhrAddressService.prepareNewHidPhrAddress(accountDto);
                return hidPhrAddressService.createHidPhrAddressEntity(hidPhrAddressDto).flatMap(phrAddressDto -> addDocumentsInIdentityDocumentEntity(accountDto, consentFormImage)
                        .flatMap(identityDocumentsDto -> addAuthMethods(accountDto, hidPhrAddressDto, requestHeaders)));
            });
        }

    }

    private Mono<IdentityDocumentsDto> addDocumentsInIdentityDocumentEntity(AccountDto accountDto, String consentFromImage) {

        IdentityDocumentsDto identityDocumentsDto = new IdentityDocumentsDto();
        identityDocumentsDto.setDocumentNumber(accountDto.getXmluid());
        identityDocumentsDto.setDocumentType(AbhaConstants.OFFLINE_AADHAAR);
        identityDocumentsDto.setDob(Common.getDob(accountDto.getDayOfBirth(), accountDto.getMonthOfBirth(), accountDto.getYearOfBirth()));
        identityDocumentsDto.setGender(accountDto.getGender());
        identityDocumentsDto.setFirstName(accountDto.getFirstName());
        identityDocumentsDto.setLastName(accountDto.getLastName());
        identityDocumentsDto.setMiddleName(accountDto.getMiddleName());
        identityDocumentsDto.setStatus(accountDto.getStatus());
        identityDocumentsDto.setHealthIdNumber(accountDto.getHealthIdNumber());
        identityDocumentsDto.setPhoto(consentFromImage);
        identityDocumentsDto.setVerificationStatus(accountDto.getVerificationStatus());
        identityDocumentsDto.setVerificationType(AbhaConstants.OFFLINE_AADHAAR);

        return identityDocumentDBService.addIdentityDocuments(identityDocumentsDto);
    }

    private Mono<EnrolByAadhaarResponseDto> addAuthMethods(AccountDto accountDto, HidPhrAddressDto hidPhrAddressDto, RequestHeaders requestHeaders) {
        List<AccountAuthMethodsDto> accountAuthMethodsDtoList = new ArrayList<>();
        accountAuthMethodsDtoList.add(new AccountAuthMethodsDto(accountDto.getHealthIdNumber(), AccountAuthMethods.AADHAAR_OTP.getValue()));
        accountAuthMethodsDtoList.add(new AccountAuthMethodsDto(accountDto.getHealthIdNumber(), AccountAuthMethods.DEMOGRAPHICS.getValue()));
        accountAuthMethodsDtoList.add(new AccountAuthMethodsDto(accountDto.getHealthIdNumber(), AccountAuthMethods.AADHAAR_BIO.getValue()));
        accountAuthMethodsDtoList.add(new AccountAuthMethodsDto(accountDto.getHealthIdNumber(), AccountAuthMethods.MOBILE_OTP.getValue()));
        return accountAuthMethodService.addAccountAuthMethods(accountAuthMethodsDtoList)
                .flatMap(authMethods -> sendAccountCreatedSMS(accountDto, hidPhrAddressDto, requestHeaders));
    }

    private Mono<EnrolByAadhaarResponseDto> sendAccountCreatedSMS(AccountDto accountDto, HidPhrAddressDto hidPhrAddressDto, RequestHeaders requestHeaders) {
        if (accountDto.getMobile() != null && !accountDto.getMobile().isBlank() && !DEFAULT_CLIENT_ID.equalsIgnoreCase(requestHeaders.getClientId())) {
            return notificationService.sendABHACreationSMS(accountDto.getMobile(), accountDto.getName(), accountDto.getHealthIdNumber()).flatMap(notificationResponseDto -> {
                if (notificationResponseDto.getStatus().equals(SENT)) {
                    ResponseTokensDto responseTokensDto = ResponseTokensDto.builder()
                            .token(jwtUtil.generateToken(UUID.randomUUID().toString(), accountDto))
                            .expiresIn(jwtUtil.jwtTokenExpiryTime())
                            .refreshToken(jwtUtil.generateRefreshToken(accountDto.getHealthIdNumber()))
                            .refreshExpiresIn(jwtUtil.jwtRefreshTokenExpiryTime())
                            .build();
                    ABHAProfileDto abhaProfileDto = MapperUtils.mapProfileDetails(accountDto);
                    abhaProfileDto.setPhrAddress(Collections.singletonList(hidPhrAddressDto.getPhrAddress()));
                    // Final create account response
                    return Mono.just(EnrolByAadhaarResponseDto.builder()
                            .abhaProfileDto(abhaProfileDto)
                            .message(AbhaConstants.ACCOUNT_CREATED_SUCCESSFULLY)
                            .isNew(true)
                            .responseTokensDto(responseTokensDto).build());
                } else {
                    throw new NotificationGatewayUnavailableException();
                }
            });
        } else {
            ResponseTokensDto responseTokensDto = ResponseTokensDto.builder()
                    .token(jwtUtil.generateToken(UUID.randomUUID().toString(), accountDto))
                    .expiresIn(jwtUtil.jwtTokenExpiryTime())
                    .refreshToken(jwtUtil.generateRefreshToken(accountDto.getHealthIdNumber()))
                    .refreshExpiresIn(jwtUtil.jwtRefreshTokenExpiryTime())
                    .build();
            ABHAProfileDto abhaProfileDto = MapperUtils.mapProfileDetails(accountDto);
            abhaProfileDto.setPhrAddress(Collections.singletonList(hidPhrAddressDto.getPhrAddress()));
            // Final create account response
            return Mono.just(EnrolByAadhaarResponseDto.builder()
                    .abhaProfileDto(abhaProfileDto)
                    .message(AbhaConstants.ACCOUNT_CREATED_SUCCESSFULLY)
                    .isNew(true)
                    .responseTokensDto(responseTokensDto).build());
        }

    }

    private Mono<EnrolByAadhaarResponseDto> respondExistingAccount(AccountDto accountDto, boolean generateToken, String responseMessage, RequestHeaders rHeaders) {
        ABHAProfileDto abhaProfileDto = MapperUtils.mapProfileDetails(accountDto);
        String txnId = UUID.randomUUID().toString();
        Flux<String> fluxPhrAddress = hidPhrAddressService
                .getHidPhrAddressByHealthIdNumbersAndPreferredIn(Arrays.asList(accountDto.getHealthIdNumber()), Arrays.asList(1, 0))
                .map(HidPhrAddressDto::getPhrAddress);

        return fluxPhrAddress.collectList().flatMap(Mono::just).flatMap(phrAddressList -> {
            abhaProfileDto.setPhrAddress(phrAddressList);

            EnrolByAadhaarResponseDto enrolByAadhaarResponseDto = EnrolByAadhaarResponseDto.builder()
                    .abhaProfileDto(abhaProfileDto)
                    .message(responseMessage)
                    .isNew(false)
                    .build();
            accountService.reAttemptedAbha(abhaProfileDto.getAbhaNumber(), AadhaarMethod.AADHAAR_DEMO.code(), rHeaders)
                    .onErrorResume(thr -> {
                        log.info(ABHA_RE_ATTEMPTED, abhaProfileDto.getAbhaNumber());
                        return Mono.empty();
                    }).subscribe();

            hidBenefitDBFClient.existByHealthIdAndBenefit(accountDto.getHealthIdNumber(), rHeaders.getBenefitName())
                    .flatMap(exists -> {
                        if (!exists) {
                            return hidBenefitDBFClient.saveHidBenefit(prepareHidBenefitDto(accountDto, rHeaders, redisService.getIntegratedPrograms()));
                        }
                        return null;
                    }).subscribe();

            // Final response for existing user
            if (generateToken) {
                ResponseTokensDto responseTokensDto = ResponseTokensDto.builder()
                        .token(jwtUtil.generateToken(txnId, accountDto))
                        .expiresIn(jwtUtil.jwtTokenExpiryTime())
                        .refreshToken(jwtUtil.generateRefreshToken(accountDto.getHealthIdNumber()))
                        .refreshExpiresIn(jwtUtil.jwtRefreshTokenExpiryTime())
                        .build();
                enrolByAadhaarResponseDto.setResponseTokensDto(responseTokensDto);
            }
            return Mono.just(enrolByAadhaarResponseDto);
        });
    }

    public HidBenefitDto prepareHidBenefitDto(AccountDto accountDto, RequestHeaders requestHeaders, List<IntegratedProgramDto> integratedProgramDtos) {
        String benefitId = String.valueOf(Common.systemGeneratedBenefitId());
        List<IntegratedProgramDto> integratedProgramDtoList
                = integratedProgramDtos.stream().filter(v -> v.getBenefitName().equals(requestHeaders.getBenefitName())
                && v.getClientId().equals(requestHeaders.getClientId())).collect(Collectors.toList());
        List<String> programName = integratedProgramDtoList.stream().map(IntegratedProgramDto::getProgramName).collect(Collectors.toList());

        String clientId = requestHeaders.getClientId() != null ? requestHeaders.getClientId() : null;

        return HidBenefitDto.builder()
                .benefitName(requestHeaders.getBenefitName())
                .programName(programName.get(0) != null ? programName.get(0) : null)
                .benefitId(benefitId)
                .status(HidBenefitStatus.LINKED.value())
                .createdBy(clientId)
                .stateCode(accountDto.getStateCode())
                .linkedBy(clientId)
                .linkedDate(LocalDateTime.now())
                .healthIdNumber(accountDto.getHealthIdNumber())
                .updatedBy(clientId)
                .updatedDate(LocalDateTime.now())
                .mobileNumber(accountDto.getMobile())
                .build();
    }

    private String preFixZero(String value) {
        return value != null && value.length() == 1 ? "0" + value : value;
    }

    private String formatDob(String year, String month, String day) {
        return year + StringConstants.DASH + preFixZero(month) + StringConstants.DASH + preFixZero(day);
    }


    private Mono<EnrolByAadhaarResponseDto> callProcedureToCreateAccount(AccountDto accountDtoResponse, RequestHeaders requestHeaders, EnrolByAadhaarRequestDto enrolByAadhaarRequestDto) {
        List<AccountDto> accountList = new ArrayList<>();
        List<HidPhrAddressDto> hidPhrAddressDtoList = new ArrayList<>();
        accountList.add(accountDtoResponse);
        HidPhrAddressDto hidPhrAddressDto = hidPhrAddressService.prepareNewHidPhrAddress(accountDtoResponse);
        hidPhrAddressDtoList.add(hidPhrAddressDto);
        if (!accountDtoResponse.getHealthIdNumber().isEmpty()) {
            List<AccountAuthMethodsDto> accountAuthMethodsDtos = new ArrayList<>();
            accountAuthMethodsDtos.add(new AccountAuthMethodsDto(accountDtoResponse.getHealthIdNumber(), AccountAuthMethods.AADHAAR_OTP.getValue()));
            accountAuthMethodsDtos.add(new AccountAuthMethodsDto(accountDtoResponse.getHealthIdNumber(), AccountAuthMethods.DEMOGRAPHICS.getValue()));
            accountAuthMethodsDtos.add(new AccountAuthMethodsDto(accountDtoResponse.getHealthIdNumber(), AccountAuthMethods.AADHAAR_BIO.getValue()));
            if (accountDtoResponse.getMobile() != null) {
                accountAuthMethodsDtos.add(new AccountAuthMethodsDto(accountDtoResponse.getHealthIdNumber(), AccountAuthMethods.MOBILE_OTP.getValue()));
            }

            log.info("going to call procedure to create account using demographic");
            return accountService.saveAllData(SaveAllDataRequest.builder().accounts(accountList).hidPhrAddress(hidPhrAddressDtoList).accountAuthMethods(accountAuthMethodsDtos).build())
                    .flatMap(v -> addDocumentsInIdentityDocumentEntity(accountDtoResponse, enrolByAadhaarRequestDto.getAuthData().getDemographic().getConsentFormImage()).flatMap(v1 -> sendAccountCreatedSMS(accountDtoResponse, hidPhrAddressDto, requestHeaders)));
        } else {
            throw new AbhaDBGatewayUnavailableException();
        }
    }

    private Mono<Demographic> breakName(Demographic demographic, List<AuthMethods> authMethods) {
        if (authMethods.contains(AuthMethods.DEMO_AUTH)) {
            log.info("Executed in case of demo Auth");
            String firstName = "";
            String lastName = "";
            String middleName = "";
            if (!StringUtils.isEmpty(demographic.getFirstName())) {
                String[] nameParts = Common.removeNulls(demographic.getFirstName()).split(" ");
                if (nameParts.length == 1) {
                    firstName = nameParts[0];
                } else if (nameParts.length == 2) {
                    firstName = nameParts[0];
                    lastName = nameParts[1];
                } else {
                    firstName = nameParts[0];
                    lastName = nameParts[nameParts.length - 1];
                    middleName = String.join(" ", Arrays.copyOfRange(nameParts, 1, nameParts.length - 1));
                }
            }
            demographic.setFirstName(GeneralUtils.stringTrimmer(firstName));
            demographic.setLastName(GeneralUtils.stringTrimmer(lastName));
            demographic.setMiddleName(GeneralUtils.stringTrimmer(middleName));
        }
        return Mono.just(demographic);
    }
}





