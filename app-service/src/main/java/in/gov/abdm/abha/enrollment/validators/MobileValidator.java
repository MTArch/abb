package in.gov.abdm.abha.enrollment.validators;

import in.gov.abdm.abha.enrollment.validators.annotations.Mobile;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class MobileValidator implements ConstraintValidator<Mobile, String> {

    private Boolean isOptional;
    private Boolean isEncrypted;

    String parttern = "(\\+91)?[1-9][0-9]{9}";

    boolean mobileCheck = true;

    @Override
    public void initialize(Mobile mobile) {
        this.isOptional = mobile.optional();
        this.isEncrypted = mobile.encrypted();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext cvc) {
        if (isOptional) {
            mobileCheck = !StringUtils.isEmpty(value);
        }

        if (mobileCheck) {
            if (isEncrypted) {
              //  value = RSAUtil.decrypt(value);
            }
            return StringUtils.isEmpty(value) || Pattern.compile(parttern).matcher(value).matches();
        }
        return true;

    }
}
