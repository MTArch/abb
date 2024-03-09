package in.gov.abdm.abha.enrollment.utilities;

import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.EnrolByAadhaarRequestDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.HidBenefitRequestPayload;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ResponseTokensDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.HashMap;

@ExtendWith(SpringExtension.class)
public class BenefitMapperTests {
    private EnrolByAadhaarResponseDto enrolByAadhaarResponseDto;
    private ABHAProfileDto abhaProfileDto;
    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
        enrolByAadhaarResponseDto=new EnrolByAadhaarResponseDto();
        abhaProfileDto=new ABHAProfileDto();
        abhaProfileDto.setAbhaNumber("1234");
        abhaProfileDto.setFirstName("name");
        abhaProfileDto.setPhrAddress(Arrays.asList("add"));
        abhaProfileDto.setLastName("name");
        abhaProfileDto.setMobile("9878");
        abhaProfileDto.setDob("12-12-2000");
        abhaProfileDto.setGender("M");
        abhaProfileDto.setPhoto("");
        abhaProfileDto.setMiddleName("name");
        abhaProfileDto.setStateCode("");
        abhaProfileDto.setDistrictCode("");
        abhaProfileDto.setPinCode("");
        abhaProfileDto.setAddress("");
        abhaProfileDto.setStateName("");
        abhaProfileDto.setDistrictName("");
        abhaProfileDto.setAbhaStatus(AccountStatus.ACTIVE);
        enrolByAadhaarResponseDto.setTxnId("1");
        enrolByAadhaarResponseDto.setAbhaProfileDto(abhaProfileDto);
        enrolByAadhaarResponseDto.setResponseTokensDto(ResponseTokensDto.builder().build());
        enrolByAadhaarResponseDto.setMessage("msg");
        HidBenefitRequestPayload hidBenefitRequestPayload=new HidBenefitRequestPayload("","","","","","","","","","","","","","","","","","","","","","","","","","","","","",true,new HashMap<>(),true,"", ResponseTokensDto.builder().build(),"");
        hidBenefitRequestPayload=HidBenefitRequestPayload.builder().build();

    }
    @AfterEach
    public void tearDown(){
        abhaProfileDto=null;
        enrolByAadhaarResponseDto=null;
    }
    @Test
    void mapHidBenefitRequestPayloadTest(){
        HidBenefitRequestPayload response = BenefitMapper.mapHidBenefitRequestPayload(enrolByAadhaarResponseDto);
    }
    @Test
    void mapHidBenefitRequestPayloadTest2(){
        abhaProfileDto.setDob("2000");
        enrolByAadhaarResponseDto.setAbhaProfileDto(abhaProfileDto);
        HidBenefitRequestPayload response = BenefitMapper.mapHidBenefitRequestPayload(enrolByAadhaarResponseDto);
    }

}
