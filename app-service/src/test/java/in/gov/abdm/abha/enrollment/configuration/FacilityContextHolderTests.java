package in.gov.abdm.abha.enrollment.configuration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
@ExtendWith(SpringExtension.class)
public class FacilityContextHolderTests {

    FacilityContextHolder facilityContextHolder;
    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void tearDown(){

    }
    @Test
    public void setClientIdTests(){
        facilityContextHolder.setClientId("Test");
    }
    @Test
    public void getClientIdTests(){
        facilityContextHolder.getClientId();
    }
    @Test
    public void setSubjectTests(){
        facilityContextHolder.setSubject("test");
    }
    @Test
    public void getSubjectTests(){
        facilityContextHolder.getSubject();
    }
    @Test
    public void setSystemTests(){
        facilityContextHolder.setSystem("test");
    }
    @Test
    public void getSystemTests(){
        facilityContextHolder.getSystem();
    }
    @Test
    public void setUserTypeTests(){
        facilityContextHolder.setUserType("test");
    }
    @Test
    public void getUserTypeTests(){
        facilityContextHolder.getUserType();
    }
    @Test
    public void setRoleTests(){
        facilityContextHolder.setRole("test");
    }
    @Test
    public void getRoleTests(){
        facilityContextHolder.getRole();
    }
    @Test
    public void removeAllTests(){
        facilityContextHolder.removeAll();
    }

}
