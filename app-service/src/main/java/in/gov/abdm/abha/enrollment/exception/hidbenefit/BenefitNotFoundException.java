package in.gov.abdm.abha.enrollment.exception.hidbenefit;

public class BenefitNotFoundException extends RuntimeException{
    public BenefitNotFoundException() {
        super();
    }
    public BenefitNotFoundException(String errorMessage){
        super(errorMessage);
    }

}
