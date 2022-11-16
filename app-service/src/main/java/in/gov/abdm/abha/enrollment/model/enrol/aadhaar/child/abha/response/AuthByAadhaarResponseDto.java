package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response;

import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AuthByAadhaarResponseDto {

    private String txnId;
    private String authResult;
    private List<AccountResponseDto> accounts;
}
