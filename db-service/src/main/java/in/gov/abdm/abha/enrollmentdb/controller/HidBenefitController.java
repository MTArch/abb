package in.gov.abdm.abha.enrollmentdb.controller;
import in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant;
import in.gov.abdm.abha.enrollmentdb.domain.hidbenefit.HidBenefitService;
import in.gov.abdm.abha.enrollmentdb.model.hidbenefit.HidBenefitDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static in.gov.abdm.abha.enrollmentdb.constant.ABHAEnrollmentDBConstant.*;

@RequestMapping(ABHAEnrollmentDBConstant.HID_BENEFIT_ENDPOINT)
@Slf4j
@RestController
public class HidBenefitController {
    @Autowired
    HidBenefitService hidBenefitService;

    @PostMapping
    public Mono<HidBenefitDto> addHidBenefit(@RequestBody HidBenefitDto hidBenefit) {
        log.info(ENROLLMENT_DB_LOG_MSG+"save data"+ENROLLMENT_DB_HID_BENEFIT);
        return hidBenefitService.addHidBenefit(hidBenefit);
    }
    @GetMapping(HID_CHECK)
    public  Mono<Boolean> existByHealthIdAndBenefit(@RequestParam("healthIdNumber") String healthIdNumber,
                                                    @RequestParam("benefitName") String benefitName) {
        log.info(ENROLLMENT_DB_LOG_MSG+"get data"+ENROLLMENT_DB_HID_BENEFIT);
        return hidBenefitService.existByHealthIdAndBenefit(healthIdNumber,benefitName);
    }


 }
