package in.gov.abdm.abha.enrollment.exception.hidbenefit;

public class BenefitNotFoundException extends RuntimeException{
    public BenefitNotFoundException(String errorCode, String errorMessage){
        super(errorCode+errorMessage);
    }

}
