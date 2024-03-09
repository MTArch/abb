package in.gov.abdm.abha.enrollment.configuration;

import org.junit.After;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import javax.validation.constraints.AssertTrue;

@ExtendWith(SpringExtension.class)
public class BeanConfigurationTests {

    private BeanConfiguration beanConfiguration;
    @BeforeEach
    void setup(){
        beanConfiguration=new BeanConfiguration("",1,"",1);
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(beanConfiguration,"redisServer","ABDM_REDIS_SERVER");
        ReflectionTestUtils.setField(beanConfiguration,"redisPort",2);
        ReflectionTestUtils.setField(beanConfiguration,"redisPassword","ABDM_REDIS_PASSWORD");
        ReflectionTestUtils.setField(beanConfiguration,"redisDatabase",2);
    }
    @AfterEach
    void tearDown(){

    }
    @Test
    public void getClientTests(){
    beanConfiguration.getClient();

    }
    @Test
    public void redisStandAloneConnectionFactoryTests(){
        beanConfiguration.redisStandAloneConnectionFactory();

    }
    @Test
    public void redisTemplateTests(){
        RedisTemplate<String, Object> response = beanConfiguration.redisTemplate();
        Assert.assertEquals(response.isEnableDefaultSerializer(),true);
    }
}
