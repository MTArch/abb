package in.gov.abdm.abha.enrollment.exception.abha_db;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbhaDBException extends RuntimeException{
    public AbhaDBException(){
        super();
    }
    public AbhaDBException(String message){
        super(message);
        log.error(message);
    }
}
