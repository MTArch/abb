package in.gov.abdm.abha.enrollment.model.nepix;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyDLResponse {
    private String authResult;
    private String message;
}
