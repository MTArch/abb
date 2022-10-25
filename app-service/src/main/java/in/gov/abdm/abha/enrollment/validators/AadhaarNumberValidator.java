package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.validators.annotations.AadhaarNumber;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class AadhaarNumberValidator implements ConstraintValidator<AadhaarNumber, String> {



    private String verhoeff_check = "enable";

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
            checkAadhar = !StringUtils.isEmpty(value);
        }

        if (checkAadhar) {
            if (isEncrypted) {
               // value = DecryptRSAUtil.decrypt(value);
              //  value = RSAUtil.decrypt(value);
            }
            getConfig();
         //   return (verhoeff_check.equalsIgnoreCase("disable")) ? true
           //         : (StringUtils.isEmpty(value) ? false : VerhoeffAlgorithm.validateVerhoeff(value));

        }
        return true;
    }

    private void getConfig() {
        verhoeff_check = System.getProperty("VERHOEFF_VALIDATE", "enable");

    }

}
