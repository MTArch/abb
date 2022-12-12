package in.gov.abdm.abha.enrollment.utilities;

import in.gov.abdm.abha.enrollment.enums.AccountStatus;
import in.gov.abdm.abha.enrollment.model.aadhaar.otp.AadhaarUserKycDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response.AccountResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.model.idp.idpverifyotpresponse.Kyc;
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

    public AccountResponseDto mapKycToAccountResponse(Kyc kyc) {
        return AccountResponseDto.builder()
                .ABHANumber(kyc.getAbhaNumber())
                .name(kyc.getName())
                .preferredAbhaAddress(kyc.getAbhaAddress())
                .yearOfBirth(kyc.getYearOfBirth())
                .gender(kyc.getGender())
                .mobile(kyc.getMobile())
                .email(kyc.getEmail())
                .build();
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
                .build();
    }

}
