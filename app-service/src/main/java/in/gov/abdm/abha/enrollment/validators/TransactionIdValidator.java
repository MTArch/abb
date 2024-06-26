package in.gov.abdm.abha.enrollment.validators;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import in.gov.abdm.abha.enrollment.enums.LoginHint;
import org.apache.commons.lang3.StringUtils;

import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.utilities.Common;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidTxnId;
import in.gov.abdm.abha.enrollment.validators.request.HelperUtil;

/**
 * Validates Transaction id attribute
 * <p>
 * it should match transaction id regex pattern
 */
public class TransactionIdValidator implements ConstraintValidator<ValidTxnId, MobileOrEmailOtpRequestDto> {

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
     */
    @Override
    public boolean isValid(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto, ConstraintValidatorContext context) {

        if (mobileOrEmailOtpRequestDto.getScope() != null && mobileOrEmailOtpRequestDto.getLoginHint()!= null ) {

            List<Scopes> requestScopes = mobileOrEmailOtpRequestDto.getScope().stream().distinct().collect(Collectors.toList());
            List<Scopes> enumNames = Stream.of(Scopes.values()).filter(name -> !name.equals(Scopes.WRONG)).collect(Collectors.toList());
            if (Common.isAllScopesAvailable(requestScopes, List.of(Scopes.ABHA_ENROL, Scopes.VERIFY_ENROLLMENT))
                    && (mobileOrEmailOtpRequestDto.getTxnId() == null)) {
                return true;
            }
            if (requestScopes == null || requestScopes.isEmpty() || !Common.isAllScopesAvailable(enumNames, requestScopes))
                return true;

            return isValidMobileOrEmailOtpRequestDto(mobileOrEmailOtpRequestDto,requestScopes);

        }
        return true;
    }

    private boolean isValidMobileOrEmailOtpRequestDto(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto, List<Scopes> requestScopes){
        if (requestScopes.size() == 1 && HelperUtil.isScopeAvailable(mobileOrEmailOtpRequestDto,
                Collections.singletonList(Scopes.ABHA_ENROL))) {
            return StringUtils.isEmpty(mobileOrEmailOtpRequestDto.getTxnId());
        } else if (requestScopes.size() == 3 && HelperUtil.isScopeAvailable(mobileOrEmailOtpRequestDto,
                List.of(Scopes.ABHA_ENROL, Scopes.MOBILE_VERIFY, Scopes.DL_FLOW))) {
            return StringUtils.isEmpty(mobileOrEmailOtpRequestDto.getTxnId());
        } else {
            if ((mobileOrEmailOtpRequestDto.getLoginHint().getValue().equalsIgnoreCase(LoginHint.MOBILE.getValue())
                    || mobileOrEmailOtpRequestDto.getLoginHint().getValue().
                    equalsIgnoreCase(LoginHint.EMAIL.getValue()))
                    && (mobileOrEmailOtpRequestDto.getTxnId()==null || StringUtils.isEmpty(mobileOrEmailOtpRequestDto.getTxnId()))) {
                return false;
            }
            if (!StringUtils.isEmpty(mobileOrEmailOtpRequestDto.getTxnId())) {
                return Pattern.compile(TRANSACTION_ID_REGEX_PATTERN).matcher(mobileOrEmailOtpRequestDto.getTxnId())
                        .matches();
            } else {
                return true;
            }
        }
    }
}
