package in.gov.abdm.abha.enrollment.validators;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AuthData;
import in.gov.abdm.abha.enrollment.validators.annotations.Demo;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validating demo object :
 *
 * if authMethods is 'otp' ,then demo can be null ,
 * if authMethods is 'pi' then demo object should not be null
 */
public class DemoAuthValidator implements ConstraintValidator<Demo, AuthData> {
    @Override
    public boolean isValid(AuthData authData, ConstraintValidatorContext context) {
        if (authData.getAuthMethods()!=null && !authData.getAuthMethods().isEmpty()
                && authData.getAuthMethods().get(0) != null && !authData.getAuthMethods().get(0).equals("")
                && authData.getAuthMethods().contains(AuthMethods.PI))
        {
            if(demoAuthNullorEmpty(authData))
                return false;
            else if(demoAuthNotNullorEmpty(authData))
                return true;
        }
        else if (authData.getAuthMethods()!=null && !authData.getAuthMethods().isEmpty()
                && authData.getAuthMethods().get(0) != null && !authData.getAuthMethods().get(0).equals("")
                && authData.getAuthMethods().stream().noneMatch(v->v.equals(AuthMethods.PI)))
        {
                return (demoAuthNullorEmpty(authData) || demoAuthNotNullorEmpty(authData));
        }
        else
            return true;
        return false;
    }

    private boolean demoAuthNotNullorEmpty(AuthData authData) {
        return !StringUtils.isEmpty(authData.getDemo())
                && authData.getDemo()!=null
                && authData.getDemo().getTimestamp()!=null
                && !authData.getDemo().getTimestamp().isEmpty()
                && authData.getDemo().getAadhaar()!=null
                && !authData.getDemo().getAadhaar().isEmpty()
                && authData.getDemo().getName()!=null
                && !authData.getDemo().getName().isEmpty()
                && authData.getDemo().getNameMatchStrategy()!=null
                && !authData.getDemo().getNameMatchStrategy().isEmpty()
                && authData.getDemo().getGender()!=null
                && !authData.getDemo().getGender().isEmpty()
                && authData.getDemo().getYob()!=null
                && !authData.getDemo().getYob().isEmpty()
                && authData.getDemo().getMobile()!=null
                && !authData.getDemo().getMobile().isEmpty();
    }

    private boolean demoAuthNullorEmpty(AuthData authData) {
        return (StringUtils.isEmpty(authData.getDemo())
                || authData.getDemo() == null)
                || ((authData.getDemo().getTimestamp() ==null
                || authData.getDemo().getTimestamp().isEmpty())
                || (authData.getDemo().getAadhaar()==null
                || authData.getDemo().getAadhaar().isEmpty())
                || (authData.getDemo().getName()==null
                || authData.getDemo().getName().isEmpty())
                || (authData.getDemo().getNameMatchStrategy()==null
                || authData.getDemo().getNameMatchStrategy().isEmpty())
                || (authData.getDemo().getGender()==null
                || authData.getDemo().getGender().isEmpty())
                || (authData.getDemo().getYob()==null
                || authData.getDemo().getYob().isEmpty())
                || (authData.getDemo().getMobile()==null
                || authData.getDemo().getMobile().isEmpty()));
    }
}
