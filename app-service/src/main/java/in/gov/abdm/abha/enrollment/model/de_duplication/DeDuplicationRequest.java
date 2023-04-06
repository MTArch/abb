package in.gov.abdm.abha.enrollment.model.de_duplication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeDuplicationRequest {
    private String firstName;
    private String lastName;
    private Integer dob;
    private Integer mob;
    private Integer yob;
    private String gender;
}
