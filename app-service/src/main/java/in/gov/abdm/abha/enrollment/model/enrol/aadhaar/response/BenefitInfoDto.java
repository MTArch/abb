package in.gov.abdm.abha.enrollment.model.enrol.aadhaar.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BenefitInfoDto {

    /**
     * It is Benefit code
     */
    private String code;
    /**
     * It is  Benefit name
     */
    private String name;
    /**
     * It is Benefit Validity
     */
    private String validity;
}
