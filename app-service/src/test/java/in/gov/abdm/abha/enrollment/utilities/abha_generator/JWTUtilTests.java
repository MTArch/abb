package in.gov.abdm.abha.enrollment.utilities.abha_generator;

import in.gov.abdm.abha.enrollment.model.entities.AccountDto;
import in.gov.abdm.abha.enrollment.utilities.jwt.JWTUtil;
import in.gov.abdm.abha.enrollment.utilities.rsa.RSAUtil;
import in.gov.abdm.abha.profile.utilities.GetKeys;
import in.gov.abdm.jwt.util.JWTToken;
import in.gov.abdm.jwt.util.JWTTokenRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.PrivateKey;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class JWTUtilTests {
    @InjectMocks
    JWTUtil jwtUtil;
    @Mock
    JWTToken jwtToken;
    @Mock
    RSAUtil rsaUtil;
    @Mock
    PrivateKey privateKey;
    private AccountDto accountDto;
    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        accountDto=new AccountDto();
        accountDto.setPreferredAbhaAddress("prefAdd");
        accountDto.setHealthId("1");
        accountDto.setHealthIdNumber("1");
        accountDto.setKycVerified(true);
        accountDto.setMobile("9876543234");
    }
    @AfterEach
    void tearDown(){

    }
//    @Test
//    public void generateTokenTests(){
//        JWTTokenRequest jwtTokenRequest=new JWTTokenRequest();
//        jwtTokenRequest.setSubject("test");
//       // Mockito.when(JWTToken.generateToken(any(),any())).thenReturn(GetKeys.privateKeyContent);
//        Mockito.when(rsaUtil.getJWTPrivateKey()).thenReturn(privateKey);
//        String res = jwtUtil.generateToken("1",accountDto);
//    }
}
