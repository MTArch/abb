package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.child.abha.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponseDto {

    private String txnId;
    private String authResult;
    private String message;
    private List<AccountResponseDto> accounts;
}
