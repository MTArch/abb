package in.gov.abdm.abha.enrollment.model.aadhaar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AadhaarOtpRequestDto {
    private String aadhaarNumber;
}
