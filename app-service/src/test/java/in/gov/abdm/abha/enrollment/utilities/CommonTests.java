package in.gov.abdm.abha.enrollment.utilities;

import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.demographic.Demographic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
public class CommonTests {
    @Test
    public void tests(){
        Demographic demographic = new DataMapper<Demographic>().mapper("demoAuth", Demographic.class);

    }
    @Test
    public void tests2(){
        EnrolmentCipher enrolmentCipher=new EnrolmentCipher();
        ReflectionTestUtils.setField(enrolmentCipher,"secretKey","test");
        enrolmentCipher.decrypt("dsasa");
    }
    @Test
    public void tests3(){
        EnrolmentCipher enrolmentCipher=new EnrolmentCipher();
        ReflectionTestUtils.setField(enrolmentCipher,"secretKey","test");
        enrolmentCipher.encrypt("dsasa");
    }
    @Test
    public void tests4(){
        EnrolmentCipher enrolmentCipher=new EnrolmentCipher();
       // ReflectionTestUtils.setField(enrolmentCipher,"secretKey","test");
        enrolmentCipher.decrypt("dsasa");
    }
    @Test
    public void tests5(){
        EnrolmentCipher enrolmentCipher=new EnrolmentCipher();
        //ReflectionTestUtils.setField(enrolmentCipher,"secretKey","test");
        enrolmentCipher.encrypt("dsasa");
    }
}
