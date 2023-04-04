package in.gov.abdm.abha.enrollment.utilities;

import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarUserKycDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AccountResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MapperUtils {
    public ABHAProfileDto mapKycDetails(AadhaarUserKycDto aadhaarUserKycDto, AccountDto accountDto) {
        ABHAProfileDto abhaProfileDto = new ABHAProfileDto();

        abhaProfileDto.setDob(aadhaarUserKycDto.getBirthdate());
        abhaProfileDto.setPhoto(aadhaarUserKycDto.getPhoto());
        abhaProfileDto.setStateCode(accountDto.getStateCode());
        abhaProfileDto.setDistrictCode(accountDto.getDistrictCode());

        abhaProfileDto.setAbhaNumber(accountDto.getHealthIdNumber());
        abhaProfileDto.setGender(accountDto.getGender());
        abhaProfileDto.setAddress(accountDto.getAddress());
        abhaProfileDto.setFirstName(accountDto.getFirstName());
        abhaProfileDto.setLastName(accountDto.getLastName());
        abhaProfileDto.setMiddleName(accountDto.getMiddleName());
        abhaProfileDto.setPinCode(accountDto.getPincode());
        abhaProfileDto.setMobile(accountDto.getMobile());
        abhaProfileDto.setEmail(accountDto.getEmail());
        abhaProfileDto.setAbhaStatus(AccountStatus.valueOf(accountDto.getStatus()));
        abhaProfileDto.setABHAType(accountDto.getType());
        return abhaProfileDto;
    }

    public ABHAProfileDto mapProfileDetails(AccountDto accountDto) {
        ABHAProfileDto abhaProfileDto = new ABHAProfileDto();
        abhaProfileDto.setDob(Common.getDob(accountDto.getDayOfBirth(), accountDto.getMonthOfBirth(), accountDto.getYearOfBirth()));
        abhaProfileDto.setPhoto(accountDto.getProfilePhoto());
        abhaProfileDto.setStateCode(accountDto.getStateCode());
        abhaProfileDto.setDistrictCode(accountDto.getDistrictCode());
        abhaProfileDto.setAbhaNumber(accountDto.getHealthIdNumber());
        abhaProfileDto.setGender(accountDto.getGender());
        abhaProfileDto.setAddress(accountDto.getAddress());
        abhaProfileDto.setFirstName(accountDto.getFirstName());
        abhaProfileDto.setLastName(accountDto.getLastName());
        abhaProfileDto.setMiddleName(accountDto.getMiddleName());
        abhaProfileDto.setPinCode(accountDto.getPincode());
        abhaProfileDto.setMobile(accountDto.getMobile());
        abhaProfileDto.setEmail(accountDto.getEmail());
        abhaProfileDto.setAbhaStatus(AccountStatus.valueOf(accountDto.getStatus()));
        abhaProfileDto.setABHAType(accountDto.getType());
        return abhaProfileDto;
    }

    public AccountResponseDto mapAccountDtoToAccountResponse(AccountDto accDto, String phrAddress) {
        return AccountResponseDto.builder()
                .ABHANumber(accDto.getHealthIdNumber())
                .name(accDto.getName())
                .preferredAbhaAddress(phrAddress)
                .yearOfBirth(accDto.getYearOfBirth())
                .gender(accDto.getGender())
                .mobile(accDto.getMobile())
                .email(accDto.getEmail())
                .kycPhoto(accDto.getKycPhoto())
                .build();
    }

}
