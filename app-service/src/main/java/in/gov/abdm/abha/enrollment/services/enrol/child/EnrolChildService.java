package in.gov.abdm.abha.enrollment.services.enrol.child;

import in.gov.abdm.abha.enrollment.client.AbhaDBAccountFClient;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AadhaarMethod;
import in.gov.abdm.abha.enrollment.exception.application.AbhaNotFountException;
import in.gov.abdm.abha.enrollment.exception.application.AbhaUnProcessableException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.ChildDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ResponseTokensDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.entities.HidPhrAddressDto;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.demographic.EnrolByDemographicValidatorService;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.utilities.MapperUtils;
import in.gov.abdm.abha.enrollment.utilities.jwt.JWTUtil;
import in.gov.abdm.error.ABDMError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.UUID;

import static in.gov.abdm.abha.enrollment.constants.AbhaConstants.ABHA_RE_ATTEMPTED;

@Slf4j
@Service
public class EnrolChildService {

    public static final String REQUEST_CHILD_MATCH_FOUND_CHILD_NAME_P_HID = "Request child Match Found: ChildName- {}, P-HID- {}";
    public static final String REQUEST_CHILD_MATCH_NOT_FOUND_CHILD_NAME_P_HID = "Request child Match Not Found: ChildName- {}, P-HID- {}";
    public static final String ACTIVE = "ACTIVE";
    private int childAbhaAccountLimit = 15;

    @Autowired
    private AccountService accountService;
    @Autowired
    private EnrolByDemographicValidatorService validator;
    @Autowired
    private AbhaDBAccountFClient abhaDBAccountFClient;
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private HidPhrAddressService hidPhrAddressService;

    public Mono<EnrolByAadhaarResponseDto> enrol(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, RequestHeaders requestHeaders) {
        return accountService.getAccountByHealthIdNumber(requestHeaders.getXToken().getHealthIdNumber())
                .flatMap(accountDto -> {
                    isValidParentAccount(accountDto);
                    return checkChildAbhaExist(enrolByAadhaarRequestDto, accountDto)
                            .flatMap(child -> respondExistingAccount(child, true, AbhaConstants.THIS_ACCOUNT_ALREADY_EXIST))
                            .switchIfEmpty(Mono.defer(() -> {

                                return Mono.just(new EnrolByAadhaarResponseDto());
                            }));
                }).switchIfEmpty(Mono.error(new AbhaNotFountException(ABDMError.ABHA_USER_NOT_FOUND)));
    }

    public void isValidParentAccount(AccountDto accountDto) {
        validator.isValidParentAge(accountDto);
        if (StringUtils.isEmpty(accountDto.getMobile())) {
            throw new AbhaUnProcessableException(ABDMError.INVALID_PARENTS_MOBILE_NUMBER);
        }
    }

    private Mono<AccountDto> checkChildAbhaExist(EnrolByAadhaarRequestDto requestChildData, AccountDto parentEntity) {
        ChildDto childDto = requestChildData.getAuthData().getChildDto();
        return abhaDBAccountFClient.getAccountsEntityByDocumentCode(parentEntity.getHealthIdNumber())
                .filter(user -> user.getName().trim().equalsIgnoreCase(childDto.getName().trim())
                        && user.getKycdob()
                        .equals(Common.populateDOB(childDto.getDayOfBirth(),
                                childDto.getMonthOfBirth(), childDto.getYearOfBirth()))
                        && user.getGender().equals(childDto.getGender()))
                .next()
                .switchIfEmpty(Mono.defer(() -> Mono.empty()));
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
}
