package in.gov.abdm.abha.enrollmentdb.controller;
import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.domain.hidbenefit.HidBenefitService;
import in.gov.abdm.abha.enrollmentdb.model.hidbenefit.HidBenefitDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequestMapping(ABHAEnrollmentDBConstant.HID_BENEFIT_ENDPOINT)
@RestController
public class HidBenefitController {
    @Autowired
    HidBenefitService hidBenefitService;

    @PostMapping
    public Mono<HidBenefitDto> addHidBenefit(@RequestBody HidBenefitDto hidBenefit) {
        return hidBenefitService.addHidBenefit(hidBenefit);
    }

}
