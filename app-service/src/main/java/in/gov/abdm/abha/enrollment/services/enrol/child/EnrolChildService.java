package in.gov.abdm.abha.enrollment.services.enrol.child;

import in.gov.abdm.abha.enrollment.client.AbhaDBAccountFClient;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.PropertyConstants;
import in.gov.abdm.abha.enrollment.enums.AccountAuthMethods;
import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.enums.childabha.AbhaType;
import in.gov.abdm.abha.enrollment.exception.application.AbhaNotFountException;
import in.gov.abdm.abha.enrollment.exception.application.AbhaUnProcessableException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.ChildDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ResponseTokensDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountAuthMethodsDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.account_auth_methods.AccountAuthMethodService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.demographic.EnrolByDemographicService;
import in.gov.abdm.abha.enrollment.services.notification.NotificationService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.GeneralUtils;
import in.gov.abdm.abha.enrollment.utilities.MapperUtils;
import in.gov.abdm.abha.enrollment.utilities.abha_generator.AbhaNumberGenerator;
import in.gov.abdm.abha.enrollment.utilities.jwt.JWTUtil;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import in.gov.abdm.error.ABDMError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class EnrolChildService {

    public static final String REQUEST_CHILD_MATCH_FOUND_CHILD_NAME_P_HID = "Request child Match Found: ChildName- {}, P-HID- {}";
    public static final String REQUEST_CHILD_MATCH_NOT_FOUND_CHILD_NAME_P_HID = "Request child Match Not Found: ChildName- {}, P-HID- {}";
    public static final String ACTIVE = "ACTIVE";
    public static final String CHILD_ABHA_VERIFIER = "ChildAbhaVerifier";

    @Value(PropertyConstants.CHILD_ENROLLMENT_LIMIT)
    private int childAbhaAccountLimit;

    @Autowired
    private AccountService accountService;
    @Autowired
    private EnrolByDemographicService validator;
    @Autowired
    private AbhaDBAccountFClient abhaDBAccountFClient;
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private HidPhrAddressService hidPhrAddressService;
    @Autowired
    private RSAUtil rsaUtil;
    @Autowired
    private PasswordEncoder bcryptEncoder;
    @Autowired
    private AccountAuthMethodService accountAuthMethodService;
    @Autowired
    NotificationService notificationService;

    public Mono<EnrolByAadhaarResponseDto> enrol(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, RequestHeaders requestHeaders) {
        return accountService.getAccountByHealthIdNumber(requestHeaders.getXToken().getHealthIdNumber())
                .flatMap(accountDto -> {
                    isValidParentAccount(accountDto);
                    return checkChildAbhaExistAndNotMoreThanLimit(enrolByAadhaarRequestDto, accountDto)
                            .flatMap(child -> respondExistingAccount(child, true, AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST))
                            .switchIfEmpty(Mono.defer(() -> createNewAccount(enrolByAadhaarRequestDto, requestHeaders, accountDto)));
                }).switchIfEmpty(Mono.error(new AbhaNotFountException(ABDMError.ABHA_USER_NOT_FOUND)));
    }

    private Mono<EnrolByAadhaarResponseDto> createNewAccount(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, RequestHeaders requestHeaders, AccountDto parentEntity) {
        ChildDto childDto = enrolByAadhaarRequestDto.getAuthData().getChildDto();
        List<AccountAuthMethodsDto> authMethods = new ArrayList<>();
        AccountDto childAccount = populateChildEntity(enrolByAadhaarRequestDto, parentEntity);
        if (StringUtils.isNotBlank(parentEntity.getMobile())) {
            authMethods.add(new AccountAuthMethodsDto(childAccount.getHealthIdNumber(), AccountAuthMethods.MOBILE_OTP.getValue()));
        }
        String decryptedPwd = rsaUtil.decrypt(childDto.getPassword());
        decryptedPwd = StringUtils.isNotBlank(decryptedPwd) ? decryptedPwd : childDto.getPassword();
        if (StringUtils.isNotBlank(decryptedPwd)) {
            authMethods.add(new AccountAuthMethodsDto(childAccount.getHealthIdNumber(), AccountAuthMethods.PASSWORD.getValue()));
        }

        return accountAuthMethodService.addAccountAuthMethods(authMethods)
                .flatMap(accountAuthMethodsDto -> accountService.createAccountEntity(enrolByAadhaarRequestDto, childAccount, requestHeaders)
                        .flatMap(this::sendNotification));
    }

    private Mono<EnrolByAadhaarResponseDto> sendNotification(AccountDto accountDto) {
        if (accountDto.getMobile() != null && !accountDto.getMobile().isBlank()) {
            return notificationService.sendABHACreationSMS(accountDto.getMobile(), accountDto.getName(), accountDto.getHealthIdNumber())
                    .flatMap(notificationResponseDto -> generateTokenAndRespond(accountDto));
        } else {
            return generateTokenAndRespond(accountDto);
        }
    }

    private Mono<EnrolByAadhaarResponseDto> generateTokenAndRespond(AccountDto accountDto) {
        ResponseTokensDto responseTokensDto = ResponseTokensDto.builder()
                .token(jwtUtil.generateToken(UUID.randomUUID().toString(), accountDto))
                .expiresIn(jwtUtil.jwtTokenExpiryTime())
                .refreshToken(jwtUtil.generateRefreshToken(accountDto.getHealthIdNumber()))
                .refreshExpiresIn(jwtUtil.jwtRefreshTokenExpiryTime())
                .build();
        return Mono.just(EnrolByAadhaarResponseDto.builder()
                .abhaProfileDto(MapperUtils.mapProfileDetails(accountDto)).responseTokensDto(responseTokensDto)
                .message(AbhaConstants.ACCOUNT_CREATED_SUCCESSFULLY)
                .isNew(true)
                .build());
    }

    public void isValidParentAccount(AccountDto accountDto) {
        validator.isValidParentAge(accountDto);
        if (StringUtils.isEmpty(accountDto.getMobile())) {
            throw new AbhaUnProcessableException(ABDMError.INVALID_PARENTS_MOBILE_NUMBER);
        }
    }

    private Mono<AccountDto> checkChildAbhaExistAndNotMoreThanLimit(EnrolByAadhaarRequestDto requestChildData, AccountDto parentEntity) {
        ChildDto childDto = requestChildData.getAuthData().getChildDto();

        Flux<AccountDto> childAccounts = abhaDBAccountFClient.getAccountsEntityByDocumentCode(parentEntity.getHealthIdNumber());

        return childAccounts
                .filter(user -> user.getName().trim().equalsIgnoreCase(childDto.getName().trim())
                        && user.getKycdob().equals(Common.populateDOB(childDto.getDayOfBirth(), childDto.getMonthOfBirth(), childDto.getYearOfBirth()))
                        && user.getGender().equals(childDto.getGender()))
                .next()
                .switchIfEmpty(childAccounts
                        .count()
                        .filter(count -> count > childAbhaAccountLimit)
                        .flatMapMany(countExceeded ->
                                Mono.error(new AbhaUnProcessableException(ABDMError.CHILD_ENROLLMENT_LIMIT_EXCEEDED.getCode(),
                                        String.format(ABDMError.CHILD_ENROLLMENT_LIMIT_EXCEEDED.getMessage(), parentEntity.getHealthIdNumber()))))
                        .then(Mono.empty())
                );
    }

    private Mono<EnrolByAadhaarResponseDto> respondExistingAccount(AccountDto accountDto, boolean generateToken, String responseMessage) {
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

    public AccountDto populateChildEntity(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, AccountDto parentEntity) {
        ChildDto requestChildData = enrolByAadhaarRequestDto.getAuthData().getChildDto();
        String kycDobYob = Common.populateDOB(requestChildData.getDayOfBirth(),
                requestChildData.getMonthOfBirth(), requestChildData.getYearOfBirth());

        String decryptedPwd = rsaUtil.decrypt(requestChildData.getPassword());
        decryptedPwd = StringUtils.isNotBlank(decryptedPwd) ? decryptedPwd : requestChildData.getPassword();
        String encPass = null;
        if (StringUtils.isNotBlank(decryptedPwd)) {
            encPass = bcryptEncoder.encode(decryptedPwd);
        }

        AccountDto accountDto = AccountDto.builder().healthIdNumber(AbhaNumberGenerator.generateAbhaNumber())
                .name(requestChildData.getName()).address(parentEntity.getAddress())
                .stateName(parentEntity.getStateName()).stateCode(parentEntity.getStateCode()).districtName(parentEntity.getDistrictName())
                .districtCode(parentEntity.getDistrictCode()).subDistrictCode(parentEntity.getSubDistrictCode())
                .subDistrictName(parentEntity.getSubDistrictName()).villageCode(parentEntity.getVillageCode())
                .villageName(parentEntity.getVillageName()).townCode(parentEntity.getTownCode())
                .townName(parentEntity.getTownName()).wardCode(parentEntity.getWardCode())
                .wardName(parentEntity.getWardCode()).pincode(parentEntity.getPincode())
                .profilePhoto(requestChildData.getProfilePhoto()).profilePhotoCompressed(false)
                .dayOfBirth(requestChildData.getDayOfBirth()).monthOfBirth(requestChildData.getMonthOfBirth())
                .yearOfBirth(requestChildData.getYearOfBirth()).gender(requestChildData.getGender())
                .verificationStatus(AbhaConstants.VERIFIED).status(AccountStatus.ACTIVE.getValue())
                .mobile(parentEntity.getMobile()).documentCode(parentEntity.getHealthIdNumber())
                .verificationType(CHILD_ABHA_VERIFIER).password(encPass).kycdob(kycDobYob).type(AbhaType.CHILD)
                .consentVersion(enrolByAadhaarRequestDto.getConsent().getVersion()).consentDate(LocalDateTime.now())
                .mobileType(parentEntity.getMobileType()).build();
        breakName(accountDto);
        return accountDto;
    }

    private void breakName(AccountDto accountDto) {
        String firstName = "";
        String lastName = "";
        String middleName = "";
        if (!StringUtils.isEmpty(accountDto.getFirstName())) {
            String[] nameParts = Common.removeNulls(accountDto.getFirstName()).split(" ");
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
        accountDto.setFirstName(GeneralUtils.stringTrimmer(firstName));
        accountDto.setLastName(GeneralUtils.stringTrimmer(lastName));
        accountDto.setMiddleName(GeneralUtils.stringTrimmer(middleName));
    }
}
