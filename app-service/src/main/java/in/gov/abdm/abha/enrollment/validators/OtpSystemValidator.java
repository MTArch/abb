package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.enums.request.OtpSystem;
import in.gov.abdm.abha.enrollment.validators.annotations.ValidOtpSystem;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates otp system should match the pre-defined values
 */
public class OtpSystemValidator implements ConstraintValidator<ValidOtpSystem,String> {

    /**
     * Implements the validation for otp system
     * Possible values can be aadhaar and abdm
     *
     * @param otpSystem object to validate
     * @param context context in which the constraint is evaluated
     *
     * @return
     */
    @Override
    public boolean isValid(String otpSystem, ConstraintValidatorContext context) {
        if(otpSystem!=null)
            return (otpSystem.equals(OtpSystem.AADHAAR.getValue()) || otpSystem.equals(OtpSystem.ABDM.getValue()));
        else
            return false;

    }
}
