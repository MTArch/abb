package in.gov.abdm.abha.enrollment.utilities;

import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.ABHAProfileDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.EnrolByAadhaarResponseDto;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response.HidBenefitRequestPayload;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class BenefitMapper {
    public HidBenefitRequestPayload mapHidBenefitRequestPayload(EnrolByAadhaarResponseDto enrolByAadhaarResponseDto) {
        HidBenefitRequestPayload hidBenefitRequestPayload = new HidBenefitRequestPayload();
        ABHAProfileDto abhaProfileDto = enrolByAadhaarResponseDto.getAbhaProfileDto();
        hidBenefitRequestPayload.setHealthIdNumber(abhaProfileDto.getAbhaNumber());
        Optional.ofNullable(abhaProfileDto.getPhrAddress()).flatMap(address -> Optional.of(address.stream().findFirst())).ifPresent(s -> hidBenefitRequestPayload.setHealthId(s.get()));
        hidBenefitRequestPayload.setMobile(abhaProfileDto.getMobile());
        hidBenefitRequestPayload.setFirstName(abhaProfileDto.getFirstName());
        String mName = "";
        if (Optional.ofNullable(abhaProfileDto.getMiddleName()).isPresent()) {
            mName = abhaProfileDto.getMiddleName().toString();
        }
        String lName = "";
        if (Optional.ofNullable(abhaProfileDto.getLastName()).isPresent()) {
            lName = abhaProfileDto.getLastName().toString();
        }
        hidBenefitRequestPayload.setMiddleName(mName);
        hidBenefitRequestPayload.setLastName(lName);
        hidBenefitRequestPayload.setName(abhaProfileDto.getFirstName() + " " + mName + " " + lName);
        if (abhaProfileDto.getDob().length() == 4) {
            hidBenefitRequestPayload.setYearOfBirth(abhaProfileDto.getDob());
        } else {
            String dob[] = abhaProfileDto.getDob().split("-");
            hidBenefitRequestPayload.setYearOfBirth(dob[2]);
            hidBenefitRequestPayload.setDayOfBirth(dob[0]);
            hidBenefitRequestPayload.setMonthOfBirth(dob[1]);
        }
        hidBenefitRequestPayload.setGender(abhaProfileDto.getGender());
        hidBenefitRequestPayload.setProfilePhoto(abhaProfileDto.getPhoto());
        hidBenefitRequestPayload.setStateCode(abhaProfileDto.getStateCode());
        hidBenefitRequestPayload.setDistrictCode(abhaProfileDto.getDistrictCode());
        hidBenefitRequestPayload.setPincode(abhaProfileDto.getPinCode());
        hidBenefitRequestPayload.setAddress(abhaProfileDto.getAddress());
        hidBenefitRequestPayload.setStateName(abhaProfileDto.getStateName());
        hidBenefitRequestPayload.setDistrictName(abhaProfileDto.getDistrictName());
        hidBenefitRequestPayload.setKycVerified(true);
        Optional.ofNullable(enrolByAadhaarResponseDto.getResponseTokensDto()).ifPresent(responseTokensDto -> hidBenefitRequestPayload.setToken(enrolByAadhaarResponseDto.getResponseTokensDto().getToken()));
        hidBenefitRequestPayload.setJwtResponse(enrolByAadhaarResponseDto.getResponseTokensDto());
        hidBenefitRequestPayload.setStatus(abhaProfileDto.getAbhaStatus().getValue());
        hidBenefitRequestPayload.setNew(enrolByAadhaarResponseDto.isNew());
        return hidBenefitRequestPayload;
    }
}
