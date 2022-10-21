package in.gov.abdm.abha.enrollmentdb.domain.transaction;

import org.springframework.data.annotation.Id;

import java.math.BigInteger;

public class Transaction {
    @Id
    private BigInteger id;
}
