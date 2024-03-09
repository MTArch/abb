package in.gov.abdm.abha.enrollment.configuration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

@ExtendWith(SpringExtension.class)
public class ContextHolderTests {
    ContextHolder contextHolder;
    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void tearDown(){

    }
    @Test
    public void setRequestIdTests(){
        contextHolder.setRequestId("test");
    }
    @Test
    public void getRequestIdTests(){
        ContextHolder.getRequestId();
    }
    @Test
    public void setClientIdTests(){
        ContextHolder.setClientId("test");
    }
    @Test
    public void getClientTests(){
        ContextHolder.getClientId();
    }
    @Test
    public void setClientIpTests(){
        ContextHolder.setClientIp("test");
    }
    @Test
    public void getClientIpTests(){
        ContextHolder.getClientIp();
    }
    @Test
    public void setTimestampTests(){
        ContextHolder.setTimestamp("test");
    }
    @Test
    public void getTimestampTests(){
        ContextHolder.getTimestamp();
    }
    @Test
    public void setBenefitRolesTests(){
        ContextHolder.setBenefitRoles(Arrays.asList("test"));
    }
    @Test
    public void getBenefitRolesTests(){
        ContextHolder.getBenefitRoles();
    }
    @Test
    public void removeAllTests(){
        ContextHolder.removeAll();
    }


}
