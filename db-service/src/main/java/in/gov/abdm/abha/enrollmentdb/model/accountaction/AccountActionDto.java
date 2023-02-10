package in.gov.abdm.abha.enrollmentdb.model.accountaction;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountActionDto {
    private String action;
    @CreatedDate
    private LocalDateTime createdDate;
    private String field;
    private String healthIdNumber;
    private String newValue;
    private String previousValue;
    private String reactivationDate;
    private String reason;
    private String reasons;
    private boolean isNewAccount;
}
