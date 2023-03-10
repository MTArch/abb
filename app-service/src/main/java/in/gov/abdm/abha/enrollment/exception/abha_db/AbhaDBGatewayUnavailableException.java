package in.gov.abdm.abha.enrollment.exception.abha_db;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbhaDBGatewayUnavailableException extends RuntimeException{
    public AbhaDBGatewayUnavailableException(){
        super();
    }
    public AbhaDBGatewayUnavailableException(String message){
        super();
        log.error(message);
    }
}
