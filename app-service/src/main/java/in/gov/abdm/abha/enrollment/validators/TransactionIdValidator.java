package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.otp_request.MobileOrEmailOtpRequestDto;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidTransactionId;
import in.gov.abdm.abha.enrollment.validators.request.HelperUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collections;
import java.util.regex.Pattern;

/**
 * Validates Transaction id attribute
 *
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
     * Validates txn id against regex pattern if not empty or null
     *
     * @param mobileOrEmailOtpRequestDto object to validate
     * @param context                    context in which the constraint is evaluated
     * @return
     */
    @Override
    public boolean isValid(MobileOrEmailOtpRequestDto mobileOrEmailOtpRequestDto, ConstraintValidatorContext context) {

		if (HelperUtil.isScopeAvailable(mobileOrEmailOtpRequestDto, Collections.singletonList(Scopes.ABHA_ENROL))) {
			if (!StringUtils.isEmpty(mobileOrEmailOtpRequestDto.getTxnId())) {
				return false;
			} else if (StringUtils.isEmpty(mobileOrEmailOtpRequestDto.getTxnId())) {
				return true;
			}
		} else {
			if (!StringUtils.isEmpty(mobileOrEmailOtpRequestDto.getTxnId())) {
				return Pattern.compile(TRANSACTION_ID_REGEX_PATTERN).matcher(mobileOrEmailOtpRequestDto.getTxnId())
						.matches();
			}
		}
        return false;
    }
}
