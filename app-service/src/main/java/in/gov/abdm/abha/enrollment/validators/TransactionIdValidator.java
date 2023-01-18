package in.gov.abdm.abha.enrollment.validators;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidTransactionId;
import in.gov.abdm.abha.enrollment.validators.request.HelperUtil;

/**
 * Validates Transaction id attribute
 * <p>
 * it should match transaction id regex pattern
 */
public class TransactionIdValidator implements ConstraintValidator<ValidTransactionId, MobileOrEmailOtpRequestDto> {

    /**
     * Constant for transaction id pattern matching
     */
    private static final String TRANSACTION_ID_REGEX_PATTERN = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";


    /**
     * Implements validation logic for transaction id
     * Txn id should be empty for the first api call ie. when scope is abha-enrol
     * Validates txn id against regex pattern if scope not empty or null
     *
     * @param mobileOrEmailOtpRequestDto object to validate
     * @param context                    in which the constraint is evaluated
     * @return
     */
    @Override
    public boolean isValid(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto, ConstraintValidatorContext context) {
        if(mobileOrEmailOtpRequestDto.getScope() == null){
            return false;
        }
        List<Scopes> requestScopes = mobileOrEmailOtpRequestDto.getScope().stream().distinct().collect(Collectors.toList());
		List<Scopes> enumNames = Stream.of(Scopes.values()).filter(name -> {
            return !name.equals(Scopes.WRONG);
        }).collect(Collectors.toList());
        if (requestScopes == null || requestScopes.isEmpty() || !Common.isAllScopesAvailable(enumNames, requestScopes))
            return true;

        if (requestScopes.size() == 1 && HelperUtil.isScopeAvailable(mobileOrEmailOtpRequestDto,
                Collections.singletonList(Scopes.ABHA_ENROL))) {
            return StringUtils.isEmpty(mobileOrEmailOtpRequestDto.getTxnId());
        } else if (requestScopes.size() == 3 && HelperUtil.isScopeAvailable(mobileOrEmailOtpRequestDto,
                List.of(Scopes.ABHA_ENROL, Scopes.MOBILE_VERIFY, Scopes.DL_FLOW))) {
            return StringUtils.isEmpty(mobileOrEmailOtpRequestDto.getTxnId());
        } else {
            if (!StringUtils.isEmpty(mobileOrEmailOtpRequestDto.getTxnId())) {
                return Pattern.compile(TRANSACTION_ID_REGEX_PATTERN).matcher(mobileOrEmailOtpRequestDto.getTxnId())
                        .matches();
            }else{
                return true;
            }
        }
    }
}
