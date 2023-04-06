package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.validators.annotations.AadhaarNumber;
import org.springframework.util.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
/**
 * Validating aadhaar number should be valid and encrypted for abha creation using aadhaar
 */

public class AadhaarNumberValidator implements ConstraintValidator<AadhaarNumber, String> {



    private String verhoeffCheck = "enable";

    private Boolean isOptional;
    private Boolean isEncrypted;

    @Override
    public void initialize(AadhaarNumber aadhaarNumber) {
        this.isOptional = aadhaarNumber.optional();
        this.isEncrypted = aadhaarNumber.encrypted();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        boolean checkAadhar = true;

        if (isOptional) {
            checkAadhar = !ObjectUtils.isEmpty(value);
        }

        if (checkAadhar) {
            getConfig();

        }
        return true;
    }

    private void getConfig() {
        verhoeffCheck = System.getProperty("VERHOEFF_VALIDATE", "enable");

    }

}
