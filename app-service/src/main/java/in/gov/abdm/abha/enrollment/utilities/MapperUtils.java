package in.gov.abdm.abha.enrollment.utilities;

import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.model.aadhaar.AadhaarUserKycDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MapperUtils {
    public ABHAProfileDto mapKycDetails(AadhaarUserKycDto aadhaarUserKycDto, AccountDto accountDto) {
        ABHAProfileDto abhaProfileDto = new ABHAProfileDto();

        abhaProfileDto.setMobile(aadhaarUserKycDto.getPhone());
        abhaProfileDto.setDob(aadhaarUserKycDto.getBirthdate());
        abhaProfileDto.setPhoto(aadhaarUserKycDto.getPhoto().getBytes());
        //abhaProfileDto.setStateCode(aadhaarUserKycDto.getState());

        abhaProfileDto.setAbhaNumber(accountDto.getHealthIdNumber());
        abhaProfileDto.setGender(accountDto.getGender());
        abhaProfileDto.setAddressLine1(accountDto.getAddress());
        abhaProfileDto.setFirstName(accountDto.getFirstName());
        abhaProfileDto.setLastName(accountDto.getLastName());
        abhaProfileDto.setMiddleName(accountDto.getMiddleName());
        abhaProfileDto.setPinCode(accountDto.getPincode());
        abhaProfileDto.setEmail(accountDto.getEmail());
        abhaProfileDto.setAbhaStatus(AccountStatus.valueOf(accountDto.getStatus()));
        abhaProfileDto.setABHAType(accountDto.getType());
        return abhaProfileDto;
    }
}
