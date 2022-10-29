package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.enums.request.ScopeEnum;
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
    private static final String TRANSACTION_ID_REGEX_PATTERN = "[0-9abcdef-]{36}";


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

//        if (HelperUtil.isScopeAvailable(mobileOrEmailOtpRequestDto, Collections.singletonList(ScopeEnum.ABHA_ENROL))) {
//            if (!StringUtils.isEmpty(mobileOrEmailOtpRequestDto.getTxnId())) {
//                mobileOrEmailOtpRequestDto.setTxnId("");
//            }
//            return true;
//        } else {
//            if(StringUtils.isEmpty(mobileOrEmailOtpRequestDto.getTxnId())) {
//                return false;
//            }else{
//                return Pattern.compile(TRANSACTION_ID_REGEX_PATTERN).matcher(mobileOrEmailOtpRequestDto.getTxnId()).matches();
//            }
//        }


        if (HelperUtil.isScopeAvailable(mobileOrEmailOtpRequestDto, Collections.singletonList(ScopeEnum.ABHA_ENROL)))
        {
            if (!StringUtils.isEmpty(mobileOrEmailOtpRequestDto.getTxnId())) {
                mobileOrEmailOtpRequestDto.setTxnId("");
                return false;
            }
            else if(StringUtils.isEmpty(mobileOrEmailOtpRequestDto.getTxnId()))
            {
                return true;
            }
        }
        else {
            if(!StringUtils.isEmpty(mobileOrEmailOtpRequestDto.getTxnId())) {
                return Pattern.compile(TRANSACTION_ID_REGEX_PATTERN).matcher(mobileOrEmailOtpRequestDto.getTxnId()).matches();
            }else{
                // return Pattern.compile(TRANSACTION_ID_REGEX_PATTERN).matcher(mobileOrEmailOtpRequestDto.getTxnId()).matches();
                return true;
            }
        }
        return false;
    }
}
