package in.gov.abdm.abha.enrollment.services.enrol;

import in.gov.abdm.abha.enrollment.commontestdata.CommonTestData.*;
import in.gov.abdm.abha.enrollment.exception.application.BadRequestException;
import in.gov.abdm.abha.enrollment.model.enrol.aadhaar.request.ConsentDto;
import in.gov.abdm.abha.enrollment.model.enrol.document.EnrolByDocumentRequestDto;
import in.gov.abdm.abha.enrollment.services.enrol.document.EnrolByDocumentValidatorService;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import static in.gov.abdm.abha.enrollment.commontestdata.CommonTestData.*;

@ExtendWith(SpringExtension.class)
public class EnrolByDocumentValidatorServiceTests {
    @InjectMocks
    EnrolByDocumentValidatorService enrolByDocumentValidatorService;
    private EnrolByDocumentRequestDto enrolByDocumentRequestDto;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        enrolByDocumentRequestDto = new EnrolByDocumentRequestDto();
        enrolByDocumentRequestDto.setTxnId(TRANSACTION_ID_VALID);
        enrolByDocumentRequestDto.setDocumentType("DRIVING_LICENCE");
        enrolByDocumentRequestDto.setDob("11-11-2000");
        enrolByDocumentRequestDto.setGender("F");
        enrolByDocumentRequestDto.setFirstName(FIRST_NAME);
        enrolByDocumentRequestDto.setLastName(LAST_NAME);
        enrolByDocumentRequestDto.setMiddleName(MIDDLE_NAME);
        enrolByDocumentRequestDto.setAddress(ABHA_ADDRESS_VALID);
        enrolByDocumentRequestDto.setPinCode(PIN_CODE_VALUE);
        enrolByDocumentRequestDto.setDistrict(DISTRICT_NAME);
        enrolByDocumentRequestDto.setState(STATE_NAME);
        enrolByDocumentRequestDto.setFrontSidePhoto("photoFront");
        enrolByDocumentRequestDto.setBackSidePhoto("photoBack");
        enrolByDocumentRequestDto.setDocumentId("1");
        enrolByDocumentRequestDto.setConsent(new ConsentDto());

    }
    @AfterEach
    void tearDown(){
        enrolByDocumentRequestDto=null;
    }
    @Test
    public void validateEnrolByDocument(){
        enrolByDocumentRequestDto = new EnrolByDocumentRequestDto();
        enrolByDocumentRequestDto.setTxnId("");
        enrolByDocumentRequestDto.setDocumentType("");
        enrolByDocumentRequestDto.setDob("2000-12-12");
        enrolByDocumentRequestDto.setGender("r");
        enrolByDocumentRequestDto.setFirstName("");
        enrolByDocumentRequestDto.setLastName("");
        enrolByDocumentRequestDto.setMiddleName("1");
        enrolByDocumentRequestDto.setAddress("pune");
        enrolByDocumentRequestDto.setPinCode("");
        enrolByDocumentRequestDto.setDistrict("");
        enrolByDocumentRequestDto.setState("");
        enrolByDocumentRequestDto.setFrontSidePhoto("dGVzdCBpbnB1dA==");
        enrolByDocumentRequestDto.setBackSidePhoto("dGVzdCBpbnB1dA==");
        enrolByDocumentRequestDto.setDocumentId("");
        enrolByDocumentRequestDto.setDob("0000-12-12");
        Assert.assertThrows(BadRequestException.class,()->enrolByDocumentValidatorService.validateEnrolByDocument(enrolByDocumentRequestDto));
        enrolByDocumentRequestDto.setDob("2028-12-12");
        Assert.assertThrows(BadRequestException.class,()->enrolByDocumentValidatorService.validateEnrolByDocument(enrolByDocumentRequestDto));
        enrolByDocumentRequestDto.setDob("2000-12-12");
        Assert.assertThrows(BadRequestException.class,()->enrolByDocumentValidatorService.validateEnrolByDocument(enrolByDocumentRequestDto));
        enrolByDocumentRequestDto.setDob("2024-02628");
        Assert.assertThrows(BadRequestException.class,()->enrolByDocumentValidatorService.validateEnrolByDocument(enrolByDocumentRequestDto));
        enrolByDocumentRequestDto.setFrontSidePhoto("]\"");
        enrolByDocumentRequestDto.setBackSidePhoto("]\"");
        Assert.assertThrows(BadRequestException.class,()->enrolByDocumentValidatorService.validateEnrolByDocument(enrolByDocumentRequestDto));

    }
}
