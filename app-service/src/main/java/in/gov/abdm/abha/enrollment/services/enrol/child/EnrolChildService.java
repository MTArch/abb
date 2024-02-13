package in.gov.abdm.abha.enrollment.services.enrol.child;

import in.gov.abdm.abha.enrollment.exception.application.AbhaNotFountException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.demographic.EnrolByDemographicValidatorService;
import in.gov.abdm.error.ABDMError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class EnrolChildService {

    @Autowired
    private AccountService accountService;

    @Autowired
    EnrolByDemographicValidatorService validator;

    public Mono<EnrolByAadhaarResponseDto> enrol(EnrolByAadhaarRequestDto enrolByAadhaarRequestDto, RequestHeaders requestHeaders) {
        return accountService.getAccountByHealthIdNumber(requestHeaders.getXToken().getHealthIdNumber())
                .flatMap(accountDto -> {
                    isValidParentAccount(accountDto);

                    return Mono.just(new EnrolByAadhaarResponseDto());
                }).switchIfEmpty(Mono.error(new AbhaNotFountException(ABDMError.ABHA_USER_NOT_FOUND.getCode(), ABDMError.ABHA_USER_NOT_FOUND.getMessage())));
    }

    public void isValidParentAccount(AccountDto accountDto) {
        validator.isValidParentAge(accountDto);
        if (StringUtils.isEmpty(accountDto.getMobile())) {
            //TODO throw error from error code INVALID_PARENTS_MOBILE_NUMBER
            //throw new ChildAbhaException(PARENTS_MOBILE_EXPECTION, CHILD_PARENTS_MOBILE_EXPECTION);
        }
    }
}
