package in.gov.abdm.abha.enrollment.model.enrol.pid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PidDto implements Serializable {

    String txnId;
    String rdPid;
    String qrScan;
}
