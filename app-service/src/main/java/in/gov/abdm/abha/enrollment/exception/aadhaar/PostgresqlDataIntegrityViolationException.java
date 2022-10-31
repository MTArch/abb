package in.gov.abdm.abha.enrollment.exception.aadhaar;

import lombok.Data;

@Data
public class PostgresqlDataIntegrityViolationException extends RuntimeException{

    private String healthIdNumber="";

    private long id;



    public PostgresqlDataIntegrityViolationException(String message, String healthIdNumber) {
        super(message);
        this.healthIdNumber = healthIdNumber;
    }

    public PostgresqlDataIntegrityViolationException(String message, long id) {
        super(message);
        this.id = id;
    }
}
