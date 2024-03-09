package in.gov.abdm.abha.enrollment.enumtest;

import in.gov.abdm.abha.enrollment.enums.*;
import in.gov.abdm.abha.enrollment.enums.childabha.AbhaType;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.Gender;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.MobileType;
import in.gov.abdm.abha.enrollment.enums.hidbenefit.HidBenefitStatus;
import in.gov.abdm.abha.enrollment.enums.link.parent.Relationship;
import in.gov.abdm.abha.enrollment.enums.request.AadhaarLogType;
import in.gov.abdm.abha.enrollment.enums.request.OtpSystem;
import in.gov.abdm.abha.enrollment.enums.request.Scopes;
import in.gov.abdm.abha.enrollment.model.notification.NotificationType;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

@ExtendWith(SpringExtension.class)
public class Enumtests {
    String acceptValue= EnrollmentStatus.ACCEPT.getValue();
    String rejectValue = EnrollmentStatus.REJECT.getValue();
    String otp = OtpSystem.AADHAAR.toString();
    @Test
    public void AbhaTypeTestvalid(){
        Assert.assertEquals(true, AbhaType.isValid(AbhaType.CHILD.toString()));
    }
    @Test
    public void AbhaTypeTestinvalid(){
        Assert.assertEquals(false, AbhaType.isValid("child"));
    }
    @Test
    public void AuthMethodsValid(){
        Assert.assertEquals(AuthMethods.OTP, AuthMethods.fromText(AuthMethods.OTP.getValue()));
    }
    @Test
    public void GenderValid(){
        Assert.assertEquals(Gender.FEMALE, Gender.byCode("F"));
    }
    @Test
    public void GenderInValid(){
        Assert.assertEquals(null, Gender.byCode("A"));
    }
    @Test
    public void GenderValid2(){
        Assert.assertEquals(true, Gender.isValid("Female"));
    }
    @Test
    public void GenderInValid2(){
        Assert.assertEquals(false, Gender.isValid("A"));
    }
    @Test
    public void GenderInValid3(){
        Assert.assertEquals(false, Gender.isValidByCode("A"));
    }
    @Test
    public void MobileTypeValid(){
        Assert.assertEquals(MobileType.SELF, MobileType.fromText(MobileType.SELF.getValue()));
    }
    @Test
    public void MobileTypeValid2(){
        Assert.assertEquals(MobileType.WRONG, MobileType.fromText(MobileType.WRONG.toString()));
    }
    @Test
    public void HidBenefitStatusValid(){
        Assert.assertEquals("LINKED", HidBenefitStatus.status(1));
    }
    @Test
    public void RelationshipValid(){
        Assert.assertEquals(false, Relationship.isValid(Relationship.WRONG.toString()));
    }
    @Test
    public void RelationshipValid2(){
        Assert.assertEquals(false, Relationship.isValid("test"));
    }
    @Test
    public void RelationshipValid3(){
        Assert.assertEquals(Relationship.MOTHER, Relationship.fromText(Relationship.MOTHER.getValue()));
    }
    @Test
    public void RelationshipValid4(){
        Assert.assertEquals(Relationship.WRONG, Relationship.fromText("test"));
    }
    @Test
    public void AccountStatusValid(){
        Assert.assertEquals(true, AccountStatus.isValid(AccountStatus.ACTIVE.toString()));
    }
    @Test
    public void AccountStatusInValid(){
        Assert.assertEquals(false, AccountStatus.isValid("test"));
    }
    @Test
    public void AccountStatusValid2(){
        Assert.assertEquals(AccountStatus.ACTIVE, AccountStatus.fromText(AccountStatus.ACTIVE.getValue()));
    }
    @Test
    public void AccountStatusInValid2(){
        Assert.assertEquals(AccountStatus.IN_ACTIVE, AccountStatus.fromText("test"));
    }
    @Test
    public void KycAuthTypeValid(){
        Assert.assertEquals(Arrays.asList(KycAuthType.FINGERSCAN.getValue(),KycAuthType.IRIS.getValue(),KycAuthType.OTP.getValue()), KycAuthType.getAllSupportedAuth());
    }
    @Test
    public void KycAuthTypeValid2(){
        Assert.assertEquals(Arrays.asList(KycAuthType.FINGERSCAN.getValue(),KycAuthType.IRIS.getValue()), KycAuthType.getAllSupportedBioAuth());
    }
    @Test
    public void KycAuthTypeValid3(){
        Assert.assertEquals(true, KycAuthType.isValid(KycAuthType.FINGERSCAN.toString()));
    }
    @Test
    public void KycAuthTypeValid4(){
        Assert.assertEquals(false, KycAuthType.isValid("test"));
    }
    @Test
    public void LoginHintValid(){
        Assert.assertEquals(LoginHint.AADHAAR, LoginHint.fromText(LoginHint.AADHAAR.getValue()));
    }
    @Test
    public void LoginHintValid2(){
        Assert.assertEquals(LoginHint.WRONG, LoginHint.fromText("test"));
    }
    @Test
    public void TransactionStatusInValid(){
        Assert.assertEquals(false, TransactionStatus.isValid("test"));
    }
    @Test
    public void TransactionStatusValid(){
        Assert.assertEquals(true, TransactionStatus.isValid(TransactionStatus.ACTIVE.toString()));
    }
    @Test
    public void AadhaarLogTypeValid(){
        Assert.assertEquals(true, AadhaarLogType.isValid(AadhaarLogType.AUTH_F.toString()));
    }
    @Test
    public void AadhaarLogTypeValid2(){
        Assert.assertEquals(false, AadhaarLogType.isValid("test"));
    }
    @Test
    public void OtpSystemValid(){
        Assert.assertEquals(OtpSystem.AADHAAR, OtpSystem.fromText(OtpSystem.AADHAAR.getValue()));
    }
    @Test
    public void OtpSystemValid2(){
        Assert.assertEquals(OtpSystem.WRONG, OtpSystem.fromText("test"));
    }
    @Test
    public void ScopesValid(){
        Assert.assertEquals(Scopes.ABHA_ENROL, Scopes.fromText(Scopes.ABHA_ENROL.getValue()));
    }
    @Test
    public void NotificationTypeValid(){
        Assert.assertEquals(NotificationType.SMS, NotificationType.fromText(NotificationType.SMS.getValue()));
    }



}
