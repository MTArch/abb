package in.gov.abdm.abha.enrollment.client;
import in.gov.abdm.abha.enrollment.configuration.AppConfigurations;
import in.gov.abdm.abha.enrollment.constants.AbhaConstants;
import in.gov.abdm.abha.enrollment.constants.URIConstant;
import in.gov.abdm.abha.enrollment.model.entities.HidBenefitDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import static in.gov.abdm.abha.enrollment.constants.PropertyConstants.ENROLLMENT_GATEWAY_ENROLLMENTDB_BASEURI;

@ReactiveFeignClient(name = AbhaConstants.ABHA_DB_HID_BENEFIT_CLIENT, url = ENROLLMENT_GATEWAY_ENROLLMENTDB_BASEURI, configuration = AppConfigurations.class)
public interface HidBenefitDBFClient {

    @PostMapping(URIConstant.DB_ADD_HID_BENEFIT_URI)
    Mono<HidBenefitDto> saveHidBenefit(@RequestBody HidBenefitDto hidBenefitDto);

    @GetMapping(URIConstant.DB_ADD_HID_BENEFIT_URI+"/check")
    public  Mono<Boolean> existByHealthIdAndBenefit(@RequestParam("healthIdNumber") String healthIdNumber,
                                                    @RequestParam("benefitName") String benefitName);


 }
