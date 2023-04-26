package in.gov.abdm.abha.enrollment.exception.aadhaar;

public class AadhaarGatewayUnavailableException extends RuntimeException{
    public AadhaarGatewayUnavailableException(Exception e){
        super(e);
    }
}
