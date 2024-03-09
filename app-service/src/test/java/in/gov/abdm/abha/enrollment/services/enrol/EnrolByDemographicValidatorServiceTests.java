package in.gov.abdm.abha.enrollment.services.enrol;

import in.gov.abdm.abha.enrollment.constants.PropertyConstants;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.AuthMethods;
import in.gov.abdm.abha.enrollment.enums.enrol.aadhaar.MobileType;
import in.gov.abdm.abha.enrollment.exception.application.AbhaBadRequestException;
import in.gov.abdm.abha.enrollment.exception.application.BadRequestException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.demographic.Demographic;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.demographic.DemographicAuth;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.AuthData;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.BioDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.ConsentDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.hidbenefit.RequestHeaders;
import in.gov.abdm.abha.enrollment.services.enrol.aadhaar.demographic.EnrolByDemographicValidatorService;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import org.apache.el.util.ReflectionUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

@ExtendWith(SpringExtension.class)
public class EnrolByDemographicValidatorServiceTests {

    @InjectMocks
    EnrolByDemographicValidatorService enrolByDemographicValidatorService;
    @Mock
    RSAUtil rsaUtil;

    public static final String AADHAAR_NUMBER = "QYhr7tdzsYyYVfFjnv/fRApJBixLC2xt1Xv1Sk/AU1SQfmagNEyamwg6o3/kiD9cKuyG3D1M1aNvVMgBCLYnuKhglQgnivzcIVVR3icwxjlJp/7gOEEB76OJzl7EG8AyqB1omC4KpgcAPjq/436t1150Mn4sDrbomJVgYZbDECD2808cxJ8ygs3iv0n/FQldvaU3bdDkKnHAE/XMnxVm7KGzn7/XI7ylxvGNmnVfwzfdnFlHnaL0oFbs0X9G9hDhI370BclD22qo8yh5y4jhbYYShtBKdB9E93CWt94Cx4y43fNAjAJJq2caMvR3M1vxFumTkc/Gn2IAtPxWMHi/e8qloF0oGO3I+j2ktWTRR6qbh7JyigcQLdOg8QPa95cpn4AyiRgXh0KyACtYFiakI3T1PsVx+GO/RrzuWIX5eYBe4TRbvD7DfuXJFrrxOhn8NOmTVOTf6OgYhAtL9OiLNLeplL1MFhZmnyqNJIAfyKZvKlQfFFMtugRZBJVmzTzI1qit1hAODI/X+0o4NMllfEMeFW6GWLnBCjl3ZM37+IS8YXLwsTYHvVd0s8zvPI6MohEdM6GyqpGnXyBYSTjblE3RG+pFvdovhRbH74yem1IdQKmFHWAoJXgWeCy9BQfBxv1SCI13IrpuvZhJ9I+/9SUX/Sl1gpg8iFHMXTqFEns=";
    public static final String TOKEN = "eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiI5MS0zODY0LTQ3MzUtNzc2OCIsImNsaWVudElkIjoiYWJoYS1wcm9maWxlLWFwcC1hcGkiLCJzeXN0ZW0iOiJBQkhBLU4iLCJtb2JpbGUiOiI3MDM3MjQ4NTEwIiwiYWJoYU51bWJlciI6IjkxLTM4NjQtNDczNS03NzY4IiwicHJlZmVycmVkQWJoYUFkZHJlc3MiOiI5MTM4NjQ0NzM1Nzc2OEBhYmRtIiwidHlwIjoiVHJhbnNhY3Rpb24iLCJleHAiOjE2ODAwODEyMDAsImlhdCI6MTY4MDA3OTQwMCwidHhuSWQiOiJiMzU5YjQ1OC1kYTBmLTQ0NjMtOTg5MC1mNGRhNGUzMjEyNjcifQ.RhofHMB7mJPXQLggZFMNc52Li7cA8fO_yI8WAyzpwRdKihuEMOJ6AE7uBI27vRr1iHr6mTMvjzc5eM9Izw9zmAGaEcuJQu6RSznCBNRHIs-dkQwHPtgKw4ICKdX6WdiOvCzaO9a4qYxoyeDRVvU5nZ4-4QFEYNJtDUaLBIKJEbXDtzr1pq9irxCczo9-99ZYeIzxduE_sTCNyCUi2MaAj2Bo0Ij4Qs555jJ9eDOrpLG2BsYHsrkEltN7_o7gm4DFd9uIWSzcPVRQZmuk4NlynpE5LXW1QUxZrg6hxhnbJWNw_E6fmDgXigyPrwT1UdPTrERCC7FjxBUVvpYcSftdQY0aVBqMooIwRfWC2Oqy-0F6wHDegotnyxsCSE_1QVR5QwgJF-16745Eq8yQM0WgJS8FeJ9i5ah-HpAVuZpFqYCHm520uKNFHzSAsidrTRUhJTbmwAVv0LSqjEL0I1Thp300e1W04owuobE2JVQr42eKIElAbFdcXO3XsaVJ5hbQO1i_pGSNqxywStNIVtfAhhBChJ-aOPrPmU55ZDyqkjwULbL35kb2Ai_QGEm7ie2HdEcAHusZyqqrzvDfmFY9lcw7RFITvJ_V4rp2b9Rx9UoQKQ5f1nn4Y9rnhdALVzqBh7Wjdv-OsN-zHQth-Vu79XhzTvNfB14iGf0D2RQGBIY";
    public static final String REFRESH_TOKEN = "eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiI5MS0zODY0LTQ3MzUtNzc2OCIsImNsaWVudElkIjoiYWJoYS1wcm9maWxlLWFwcC1hcGkiLCJzeXN0ZW0iOiJBQkhBLU4iLCJ0eXAiOiJSZWZyZXNoIiwiZXhwIjoxNjgxMzc1NDAxLCJpYXQiOjE2ODAwNzk0MDF9.TgXvxiZLYAzhcphDgQjWwdpyvg01pZ9ANbdrKIsdgw55ZMuEe_K2JVNs9ynLWDKJ54IPmNuHvFQS6Wvs17sMiYCCnEaUzkpn86-xM4O5TZHkGAi6WhdGTsYYIQZghqYBpH89Y88AyIYV3jZEUI89bjVlziK9nNKxTQLSRWpZSw42bPLbi8CSHv14H9ozlJGoRsjbXSUJrArw52yGwmWkOs8rQROrIaftqQjkmucTvoptXe80K6PRiL9tT2sY739iAigAj-6ffinlIis_6goNQ5aAJFUsAE7c7aVKMdW686pp0aILiDJYyfSdpzCXvj3ihBlBImhcyDlj38b4PGWUSNVkSCyOvl_pCmGjYF3lUgsqejYb_7_nYS9su0HlVKRCkeTBnA5JvomNOCwOfRaK7wuCoZU5P1Na--dYUjRGZV1M2r-hUzj7lJO2s0JxiY37BCmZuJXnzVdKVcqcWk2OyICHHJE8SfIvPh2HW3IUF-jFhDWSHP2a53UbiDPQK-S9bJkVFnsGm6OO4WB2C-Yd1TzcguYgf9If8GcRpion6KZB05luFvUv6Z-ymcDtSd_KE_IPD4tWhiapWgkVSZlhAS34F1DmlZr1oj8LBjbOrdiZz95BsiANMpT_CW1QWnEHNrq9w5jJcwC51Ityw_MoBNK9XwZ9CDyGtuTmIMCTgfI";
    public static final String PID = "QYhr7tdzsYyYVfFjnv/fRApJBixLC2xt1Xv1Sk/AU1SQfmagNhgsftedo3/kiD9cKuyG3D1M1aNvVMgBCLYnuKhglQgnivzcIVVR3icwxjlJp/7gOEEB76OJzl7EG8AyqB1omC4KpgcAPjq/436t1150Mn4sDrbomJVgYZbDECD2808cxJ8ygs3iv0n/FQldvaU3bdDkKnHAE/XMnxVm7KGzn7/XI7ylxvGNmnVfwzfdnFlHnaL0oFbs0X9G9hDhI370BclD22qo8yh5y4jhbYYShtBKdB9E93CWt94Cx4y43fNAjAJJq2caMvR3M1vxFumTkc/Gn2IAtPxWMHi/e8qloF0oGO3I+j2ktWTRR6qbh7JyigcQLdOg8QPa95cpn4AyiRgXh0KyACtYFiakI3T1PsVx+GO/RrzuWIX5eYBe4TRbvD7DfuXJFrrxOhn8NOmTVOTf6OgYhAtL9OiLNLeplL1MFhZmnyqNJIAfyKZvKlQfFFMtugRZBJVmzTzI1qit1hAODI/X+0o4NMllfEMeFW6GWLnBCjl3ZM37+IS8YXLwsTYHvVd0s8zvPI6MohEdM6GyqpGnXyBYSTjblE3RG+pFvdovhRbH74yem1IdQKmFHWAoJXgWeCy9BQfBxv1SCI13IrpuvZhJ9I+/9SUX/Sl1gpg8iFHMXTqFEns=";

    private EnrolByAadhaarRequestDto enrolByAadhaarRequestDto;
    private BioDto bioDto;
    private ConsentDto consentDto;
    private AuthData authData;
    private RequestHeaders requestHeaders;
    private Demographic demographic;
    private DemographicAuth demographicAuth;
    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(enrolByDemographicValidatorService,"documentPhotoMinSizeLimit","1");
        ReflectionTestUtils.setField(enrolByDemographicValidatorService,"documentPhotoMaxSizeLimit","2");
        demographicAuth=new DemographicAuth();
        DemographicAuth demographicAuthtest=new DemographicAuth("","","","","","","","");
        DemographicAuth demographicAuthtest2=DemographicAuth.builder().build();
        enrolByAadhaarRequestDto=new EnrolByAadhaarRequestDto();
        demographic= new Demographic();
        bioDto=new BioDto();
        consentDto=new ConsentDto();
        authData=new AuthData();
        requestHeaders=new RequestHeaders();
        ArrayList<AuthMethods> authMethods = new ArrayList<>();
        authMethods.add(AuthMethods.BIO);
        bioDto.setAadhaar(AADHAAR_NUMBER);
        bioDto.setFingerPrintAuthPid(PID);
        bioDto.setMobile("9876543872");
        consentDto.setCode("abha-enrollment");
        consentDto.setVersion("1.4");
        authData.setAuthMethods(authMethods);
        authData.setBio(bioDto);
        enrolByAadhaarRequestDto.setAuthData(authData);
        enrolByAadhaarRequestDto.setConsent(consentDto);
        demographic.setFirstName("1");
        demographic.setMiddleName("1");
        demographic.setDistrict("");
        demographic.setGender("J");
        demographic.setAddress("");
        demographic.setAadhaarNumber("");
        demographic.setMobile("");
        demographic.setDayOfBirth("a");
        demographic.setMonthOfBirth("a");
        demographic.setYearOfBirth("a");
        demographic.setMobileType(MobileType.WRONG);
        demographic.setLastName("2");
        demographic.setHealthWorkerName("1");
        demographic.setConsentFormImage("image");
        demographic.setPinCode("");
        demographic.setState("");
        demographic.setHealthWorkerMobile("");
        demographic.setGender("J");

        demographicAuth.setName("1");
        demographicAuth.setDateOfBirth("12-12-2000");
        demographicAuth.setGender("J");
        demographicAuth.setAddress("*");
        demographicAuth.setAadhaarNumber("");
        demographicAuth.setMobile(demographicAuthtest.getMobile());
        demographicAuth.setProfilePhoto(demographicAuthtest.getProfilePhoto());
        demographicAuth.setMobile(demographicAuthtest.getMobile());
        demographicAuth.setValidity(demographicAuthtest.getValidity());
    }
    @AfterEach
    void tearDown(){
        enrolByAadhaarRequestDto=null;
        consentDto=null;
        bioDto=null;
        authData=null;
        demographic=null;
    }

    @Test
    public void validateEnrolByBioTest(){

        authData.setDemographic(demographic);
        enrolByAadhaarRequestDto.setAuthData(authData);
       Assert.assertThrows(BadRequestException.class , () ->enrolByDemographicValidatorService.validateEnrolByDemographic(enrolByAadhaarRequestDto,requestHeaders));

    }
    @Test
    public void validateEnrolByBioTest2(){
        demographic.setDayOfBirth("");
        demographic.setMonthOfBirth("10");
        demographic.setYearOfBirth("2010");
        //demographic.setMobile("9283738292");
        requestHeaders.setBenefitName("name");
        authData.setDemographic(demographic);
        enrolByAadhaarRequestDto.setAuthData(authData);
        Assert.assertThrows(BadRequestException.class , () ->enrolByDemographicValidatorService.validateEnrolByDemographic(enrolByAadhaarRequestDto,requestHeaders));

    }
    @Test
    public void validateEnrolByBioTest4(){
        demographic.setDayOfBirth("");
        demographic.setMonthOfBirth("10");
        demographic.setYearOfBirth("2010");
        demographic.setMobile("9283738292");
        requestHeaders.setBenefitName("name");
        authData.setDemographic(demographic);
        enrolByAadhaarRequestDto.setAuthData(authData);
        Assert.assertThrows(BadRequestException.class , () ->enrolByDemographicValidatorService.validateEnrolByDemographic(enrolByAadhaarRequestDto,requestHeaders));

    }
    @Test
    public void validateEnrolByBioTest3(){
        Assert.assertThrows(BadRequestException.class , () ->enrolByDemographicValidatorService.validateEnrolByDemographic(demographicAuth,requestHeaders));

    }
    @Test
    public void validateEnrolByBioTestDemoAuth(){
        demographicAuth.setDateOfBirth("1");
        Assert.assertThrows(BadRequestException.class , () ->enrolByDemographicValidatorService.validateEnrolByDemographic(demographicAuth,requestHeaders));

    }
    @Test
    public void validateEnrolByBioTestDemoAuth2(){
        demographicAuth.setDateOfBirth("2000");
        Assert.assertThrows(BadRequestException.class , () ->enrolByDemographicValidatorService.validateEnrolByDemographic(demographicAuth,requestHeaders));

    }

   @Test
   public void validateEnrolByBioTestAbhaBadRequestException(){
       Assert.assertThrows(AbhaBadRequestException.class , () ->enrolByDemographicValidatorService.validateEnrolByDemographic(enrolByAadhaarRequestDto,requestHeaders));
   }
    @Test
    public void isValidProfilePhoto(){
       Boolean b = enrolByDemographicValidatorService.isValidProfilePhoto("text");
       Assert.assertEquals(b,false);
    }
    @Test
    public void isValidProfilePhotoEmpty(){
        Boolean b = enrolByDemographicValidatorService.isValidProfilePhoto("");
        Assert.assertEquals(b,false);
    }
}
