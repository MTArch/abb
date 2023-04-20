package in.gov.abdm.abha.enrollment.aadhaar.send_otp;

import in.gov.abdm.abha.enrollment.services.database.account.AccountService;
import in.gov.abdm.abha.enrollment.services.database.hidphraddress.HidPhrAddressService;
import in.gov.abdm.abha.enrollment.services.database.transaction.TransactionService;
import in.gov.abdm.abha.enrollment.services.enrol.abha_address.AbhaAddressService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.util.UUID;

@ExtendWith(SpringExtension.class)
public class AbhaAddressServiceTests {

    @InjectMocks
    AbhaAddressService abhaAddressService;

    @Mock
    TransactionService transactionService;
    @Mock
    AccountService accountService;
    @Mock
    HidPhrAddressService hidPhrAddressService;

    @BeforeEach
    void setup()
    {

    }

    @AfterEach
    void tearDown()
    {

    }

    @Test
    void getAbhaAddressSuccess()
    {

    }
}
