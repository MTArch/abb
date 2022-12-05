package in.gov.abdm.abha.enrollment.exception.aadhaar;

public class AadhaarExceptions extends RuntimeException{

    public AadhaarExceptions(String errorCode){
        super(errorCode);
    }
}
