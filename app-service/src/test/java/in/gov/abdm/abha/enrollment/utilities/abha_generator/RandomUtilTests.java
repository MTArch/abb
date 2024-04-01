package in.gov.abdm.abha.enrollment.utilities.abha_generator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class RandomUtilTests {
    @Test
    public void getRandomLongTest(){
        long res  =RandomUtil.getRandomLong(3L,1L);
    }
    @Test
    public void getRandomDoubleTest(){
        double res  =RandomUtil.getRandomDouble();
    }
}
