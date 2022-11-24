package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {

    private String txnId;
    private String authResult;
    private List<AccountResponseDto> accounts;
}
