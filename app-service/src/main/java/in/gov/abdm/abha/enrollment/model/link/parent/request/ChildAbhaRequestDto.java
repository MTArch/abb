package in.gov.abdm.abha.enrollment.model.link.parent.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildAbhaRequestDto {

    @JsonProperty("ABHANumber")
    private String ABHANumber;
}
